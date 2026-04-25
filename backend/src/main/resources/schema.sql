-- ============================================================
-- CampusLink — Schéma Base de Données
-- PostgreSQL 15
-- ============================================================

-- ===== TYPES ENUM =====
DO $$ BEGIN
  CREATE TYPE user_role AS ENUM ('ADMIN', 'BDE', 'USER');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE connection_status AS ENUM ('PENDING', 'ACCEPTED', 'BLOCKED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE event_status AS ENUM ('DRAFT', 'PUBLISHED', 'CANCELLED', 'PAST');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE notification_type AS ENUM ('EVENT', 'CONNECTION', 'PUSH', 'SYSTEM');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- ============================================================
-- MODULE : UTILISATEURS (partagé)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
  id            BIGSERIAL PRIMARY KEY,
  firebase_uid  VARCHAR(128) UNIQUE,
  nom           VARCHAR(100) NOT NULL,
  prenom        VARCHAR(100) NOT NULL,
  email         VARCHAR(255) NOT NULL UNIQUE,
  role          user_role    NOT NULL DEFAULT 'USER',
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Profil contextuel (couche académique)
CREATE TABLE IF NOT EXISTS profile_contexts (
  id            BIGSERIAL PRIMARY KEY,
  user_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  filiere       VARCHAR(150),
  annee         SMALLINT     CHECK (annee BETWEEN 1 AND 8),
  statut        VARCHAR(50)  DEFAULT 'ETUDIANT',  -- ETUDIANT | ENSEIGNANT | ADMINISTRATION
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  UNIQUE(user_id)
);

-- ============================================================
-- MODULE 1 — MISE EN RELATION (équipe RODAMI)
-- ============================================================
CREATE TABLE IF NOT EXISTS interests (
  id            BIGSERIAL PRIMARY KEY,
  user_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  tag           VARCHAR(100) NOT NULL,
  category      VARCHAR(100),
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  UNIQUE(user_id, tag)
);

CREATE TABLE IF NOT EXISTS connections (
  id              BIGSERIAL PRIMARY KEY,
  user_id_1       BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  user_id_2       BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status          connection_status NOT NULL DEFAULT 'PENDING',
  source_event_id BIGINT,           -- lien vers l'événement déclencheur
  created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  CHECK (user_id_1 <> user_id_2),
  UNIQUE(user_id_1, user_id_2)
);

-- Index pour les recommandations
CREATE INDEX IF NOT EXISTS idx_interests_user ON interests(user_id);
CREATE INDEX IF NOT EXISTS idx_interests_tag  ON interests(tag);
CREATE INDEX IF NOT EXISTS idx_connections_users ON connections(user_id_1, user_id_2);

-- ============================================================
-- MODULE 2 — GESTION DES ÉVÉNEMENTS (équipe RODAMI)
-- ============================================================
CREATE TABLE IF NOT EXISTS event_categories (
  id          BIGSERIAL PRIMARY KEY,
  nom         VARCHAR(100) NOT NULL UNIQUE,
  priorite    SMALLINT     NOT NULL DEFAULT 0,  -- défini par le BDE
  created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS events (
  id              BIGSERIAL PRIMARY KEY,
  titre           VARCHAR(255)   NOT NULL,
  description     TEXT,
  date_debut      TIMESTAMPTZ    NOT NULL,
  date_fin        TIMESTAMPTZ,
  lieu            VARCHAR(255),
  category_id     BIGINT         REFERENCES event_categories(id),
  organisateur_id BIGINT         NOT NULL REFERENCES users(id),
  status          event_status   NOT NULL DEFAULT 'DRAFT',
  max_participants INT,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS event_registrations (
  id              BIGSERIAL PRIMARY KEY,
  event_id        BIGINT      NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  user_id         BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  registered_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(event_id, user_id)
);

-- Lien connexion ↔ événement
ALTER TABLE connections
  ADD CONSTRAINT fk_connection_event
  FOREIGN KEY (source_event_id) REFERENCES events(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_events_date     ON events(date_debut);
CREATE INDEX IF NOT EXISTS idx_events_category ON events(category_id);
CREATE INDEX IF NOT EXISTS idx_events_status   ON events(status);

-- ============================================================
-- MODULE 3 — GOUVERNANCE / BDE (autre équipe — squelette)
-- ============================================================
CREATE TABLE IF NOT EXISTS governance_rules (
  id            BIGSERIAL PRIMARY KEY,
  rule_key      VARCHAR(200) NOT NULL UNIQUE,
  rule_value    TEXT         NOT NULL,
  is_fixed      BOOLEAN      NOT NULL DEFAULT FALSE,  -- règles Admin non modifiables
  set_by_role   user_role    NOT NULL DEFAULT 'BDE',
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_logs (
  id          BIGSERIAL PRIMARY KEY,
  actor_id    BIGINT      REFERENCES users(id),
  action      VARCHAR(200) NOT NULL,
  entity_type VARCHAR(100),
  entity_id   BIGINT,
  details     JSONB,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- MODULE 4 — NOTIFICATIONS (autre équipe — squelette)
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type        notification_type NOT NULL,
  title       VARCHAR(255)      NOT NULL,
  body        TEXT,
  is_read     BOOLEAN           NOT NULL DEFAULT FALSE,
  metadata    JSONB,
  created_at  TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_unread
  ON notifications(user_id, is_read) WHERE is_read = FALSE;

-- ============================================================
-- Données initiales
-- ============================================================
INSERT INTO event_categories (nom, priorite) VALUES
  ('Sport',       1),
  ('Culture',     2),
  ('Académique',  3),
  ('Social',      4),
  ('Informatique',5)
ON CONFLICT (nom) DO NOTHING;

INSERT INTO governance_rules (rule_key, rule_value, is_fixed, set_by_role) VALUES
  ('institutional.message.priority', 'true',   TRUE,  'ADMIN'),
  ('push.frequency.per.day',         '3',       FALSE, 'BDE'),
  ('push.enabled',                   'true',    FALSE, 'BDE'),
  ('cold_start.enabled',             'true',    TRUE,  'ADMIN')
ON CONFLICT (rule_key) DO NOTHING;
