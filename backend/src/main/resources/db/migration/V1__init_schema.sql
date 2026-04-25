-- ============================================================
-- CampusLink — Migration V1 : Schéma Initial
-- Flyway (JDBC) — compatible dollar-quoted PostgreSQL syntax
-- ============================================================

-- ============================================================
-- MODULE : UTILISATEURS (partagé)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
  id            BIGSERIAL PRIMARY KEY,
  firebase_uid  VARCHAR(128) UNIQUE,
  nom           VARCHAR(100) NOT NULL,
  prenom        VARCHAR(100) NOT NULL,
  email         VARCHAR(255) NOT NULL UNIQUE,
  role          VARCHAR(50)  NOT NULL DEFAULT 'USER',
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS profile_contexts (
  id            BIGSERIAL PRIMARY KEY,
  user_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  filiere       VARCHAR(150),
  annee         SMALLINT     CHECK (annee BETWEEN 0 AND 12),
  statut        VARCHAR(50)  DEFAULT 'ETUDIANT',
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  UNIQUE(user_id)
);

-- ============================================================
-- MODULE 1 — MISE EN RELATION
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
  user_id_1       BIGINT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  user_id_2       BIGINT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status          VARCHAR(50)       NOT NULL DEFAULT 'PENDING',
  source_event_id BIGINT,
  created_at      TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
  CHECK (user_id_1 <> user_id_2),
  UNIQUE(user_id_1, user_id_2)
);

CREATE INDEX IF NOT EXISTS idx_interests_user ON interests(user_id);
CREATE INDEX IF NOT EXISTS idx_interests_tag  ON interests(tag);
CREATE INDEX IF NOT EXISTS idx_connections_users ON connections(user_id_1, user_id_2);

-- ============================================================
-- MODULE 2 — GESTION DES ÉVÉNEMENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS event_categories (
  id          BIGSERIAL PRIMARY KEY,
  nom         VARCHAR(100) NOT NULL UNIQUE,
  priorite    SMALLINT     NOT NULL DEFAULT 0,
  created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS events (
  id              BIGSERIAL PRIMARY KEY,
  titre           VARCHAR(255)  NOT NULL,
  description     TEXT,
  date_debut      TIMESTAMPTZ   NOT NULL,
  date_fin        TIMESTAMPTZ,
  lieu            VARCHAR(255),
  category_id     BIGINT        REFERENCES event_categories(id),
  organisateur_id BIGINT        NOT NULL REFERENCES users(id),
  status          VARCHAR(50)   NOT NULL DEFAULT 'DRAFT',
  max_participants INT,
  created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS event_registrations (
  id            BIGSERIAL PRIMARY KEY,
  event_id      BIGINT      NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  user_id       BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  registered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(event_id, user_id)
);

ALTER TABLE connections
  ADD CONSTRAINT fk_connection_event
  FOREIGN KEY (source_event_id) REFERENCES events(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_events_date     ON events(date_debut);
CREATE INDEX IF NOT EXISTS idx_events_category ON events(category_id);
CREATE INDEX IF NOT EXISTS idx_events_status   ON events(status);

-- ============================================================
-- MODULE 3 — GOUVERNANCE (squelette)
-- ============================================================
CREATE TABLE IF NOT EXISTS governance_rules (
  id          BIGSERIAL PRIMARY KEY,
  rule_key    VARCHAR(200) NOT NULL UNIQUE,
  rule_value  TEXT         NOT NULL,
  is_fixed    BOOLEAN      NOT NULL DEFAULT FALSE,
  set_by_role VARCHAR(50)  NOT NULL DEFAULT 'BDE',
  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
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
-- MODULE 4 — NOTIFICATIONS (squelette)
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type        VARCHAR(50)       NOT NULL,
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
  ('Sport',        1),
  ('Culture',      2),
  ('Académique',   3),
  ('Social',       4),
  ('Informatique', 5)
ON CONFLICT (nom) DO NOTHING;

INSERT INTO governance_rules (rule_key, rule_value, is_fixed, set_by_role) VALUES
  ('institutional.message.priority', 'true', TRUE,  'ADMIN'),
  ('push.frequency.per.day',         '3',    FALSE, 'BDE'),
  ('push.enabled',                   'true', FALSE, 'BDE'),
  ('cold_start.enabled',             'true', TRUE,  'ADMIN')
ON CONFLICT (rule_key) DO NOTHING;

INSERT INTO users (id, nom, prenom, email, role) VALUES
  (1, 'Admin', 'CampusLink', 'admin@campuslink.fr', 'ADMIN')
ON CONFLICT (id) DO NOTHING;

-- Mettre à jour la séquence pour éviter les conflits lors des futurs inserts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO profile_contexts (user_id, filiere, annee, statut) VALUES
  (1, 'Administration', 5, 'PERSONNEL')
ON CONFLICT (user_id) DO NOTHING;

