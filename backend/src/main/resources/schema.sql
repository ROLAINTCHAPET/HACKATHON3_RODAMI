-- ============================================================
-- CampusLink — Schéma Base de Données
-- PostgreSQL 15
-- ============================================================

-- ===== TYPES ENUM =====
-- Utilisation de guillemets simples pour compatibilité R2DBC
DO 'BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = ''user_role'') THEN
    CREATE TYPE user_role AS ENUM (''ADMIN'', ''BDE'', ''USER'', ''GUEST'');
  END IF;
END';

DO 'BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = ''connection_status'') THEN
    CREATE TYPE connection_status AS ENUM (''PENDING'', ''ACCEPTED'', ''BLOCKED'');
  END IF;
END';

DO 'BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = ''event_status'') THEN
    CREATE TYPE event_status AS ENUM (''DRAFT'', ''PUBLISHED'', ''CANCELLED'', ''PAST'');
  END IF;
END';

DO 'BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = ''notification_type'') THEN
    CREATE TYPE notification_type AS ENUM (''EVENT'', ''CONNECTION'', ''PUSH'', ''SYSTEM'');
  END IF;
END';



-- ============================================================
-- MODULE : UTILISATEURS (partagé)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
  id            BIGSERIAL PRIMARY KEY,
  firebase_uid  VARCHAR(128) UNIQUE,
  nom           VARCHAR(100) NOT NULL,
  prenom        VARCHAR(100) NOT NULL,
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255),
  role          VARCHAR(50)  NOT NULL DEFAULT 'USER',
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Profil contextuel (couche académique)
CREATE TABLE IF NOT EXISTS profile_contexts (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT      NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  filiere         VARCHAR(200),
  annee           SMALLINT,     -- 1, 2, 3, 4, 5
  semester        SMALLINT     DEFAULT 1,  -- TWIST 07 : Bascule globale
  rythme          VARCHAR(100) DEFAULT 'COURS', -- COURS, PROJET, STAGE
  principal_location VARCHAR(255), -- Bâtiment principal (stable par semestre)
  statut          VARCHAR(50)  DEFAULT 'ETUDIANT',
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Gestion des associations (RF-19)
CREATE TABLE IF NOT EXISTS associations (
  id              BIGSERIAL PRIMARY KEY,
  nom             VARCHAR(200) NOT NULL UNIQUE,
  description     TEXT,
  responsable_id  BIGINT       NOT NULL REFERENCES users(id),
  status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

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
  association_id  BIGINT         REFERENCES associations(id),
  status          event_status   NOT NULL DEFAULT 'DRAFT',
  max_participants INT,
  share_token     UUID           UNIQUE DEFAULT gen_random_uuid(),
  is_flash        BOOLEAN        NOT NULL DEFAULT FALSE,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- MODULE 1 — MISE EN RELATION (équipe RODAMI)
-- ============================================================
CREATE TABLE IF NOT EXISTS interests (
  id            BIGSERIAL PRIMARY KEY,
  user_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  tag           VARCHAR(100) NOT NULL,
  category      VARCHAR(100),
  source_event_id BIGINT      REFERENCES events(id) ON DELETE SET NULL, -- TWIST 09 : Traçabilité pour cleanup
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  UNIQUE(user_id, tag, source_event_id)
);

CREATE TABLE IF NOT EXISTS connections (
  id              BIGSERIAL PRIMARY KEY,
  requester_id    BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  receiver_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status          connection_status NOT NULL DEFAULT 'PENDING',
  source_event_id BIGINT,           -- lien vers l'événement déclencheur
  reality_score   DECIMAL(3,2)    NOT NULL DEFAULT 0.1, -- TWIST 09 : Score d'interaction réelle
  interaction_count INT           NOT NULL DEFAULT 0,   -- TWIST 09 : Nombre d'interactions
  created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  CHECK (requester_id <> receiver_id),
  UNIQUE(requester_id, receiver_id)
);

-- Index pour les recommandations
CREATE INDEX IF NOT EXISTS idx_interests_user ON interests(user_id);
CREATE INDEX IF NOT EXISTS idx_interests_tag  ON interests(tag);
CREATE INDEX IF NOT EXISTS idx_connections_users ON connections(requester_id, receiver_id);

CREATE TABLE IF NOT EXISTS event_registrations (
  id              BIGSERIAL PRIMARY KEY,
  event_id        BIGINT      NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  user_id         BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  is_attended     BOOLEAN     NOT NULL DEFAULT FALSE, -- TWIST 09 : Présence confirmée
  registered_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(event_id, user_id)
);

-- Sécurité TWIST 08 / 09 : S'assurer que les colonnes existent
DO 'BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name=''events'' AND column_name=''share_token'') THEN
    ALTER TABLE events ADD COLUMN share_token UUID UNIQUE DEFAULT gen_random_uuid();
  END IF;
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name=''events'' AND column_name=''association_id'') THEN
    ALTER TABLE events ADD COLUMN association_id BIGINT REFERENCES associations(id);
  END IF;
  
  -- TWIST 09 : Colonnes pour la mesure des interactions réelles
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name=''connections'' AND column_name=''reality_score'') THEN
    ALTER TABLE connections ADD COLUMN reality_score DECIMAL(3,2) NOT NULL DEFAULT 0.1;
    ALTER TABLE connections ADD COLUMN interaction_count INT NOT NULL DEFAULT 0;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name=''event_registrations'' AND column_name=''is_attended'') THEN
    ALTER TABLE event_registrations ADD COLUMN is_attended BOOLEAN NOT NULL DEFAULT FALSE;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name=''interests'' AND column_name=''source_event_id'') THEN
    ALTER TABLE interests ADD COLUMN source_event_id BIGINT REFERENCES events(id) ON DELETE SET NULL;
  END IF;
END';

-- Lien connexion ↔ événement (Sécurisé)
DO 'BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = ''fk_connection_event'') THEN
    ALTER TABLE connections ADD CONSTRAINT fk_connection_event FOREIGN KEY (source_event_id) REFERENCES events(id) ON DELETE SET NULL;
  END IF;
END';

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
-- MODULE 1 — CATALOGUE D'INTÉRÊTS (tags prédéfinis)
-- ============================================================
CREATE TABLE IF NOT EXISTS interest_catalog (
  id            BIGSERIAL PRIMARY KEY,
  tag           VARCHAR(100) NOT NULL UNIQUE,
  category      VARCHAR(100) NOT NULL,
  emoji         VARCHAR(10),
  display_order SMALLINT     NOT NULL DEFAULT 0
);

-- Emplois du temps (RF-20 / TWIST 04)
CREATE TABLE IF NOT EXISTS academic_schedules (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  summary         VARCHAR(255),
  start_time      TIMESTAMPTZ NOT NULL,
  end_time        TIMESTAMPTZ NOT NULL,
  location        VARCHAR(255),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_schedule_user_time ON academic_schedules(user_id, start_time);

-- Traçabilité et Audit (RF-18)
CREATE TABLE IF NOT EXISTS audit_logs (
  id          BIGSERIAL PRIMARY KEY,
  actor_id    BIGINT      REFERENCES users(id),
  action      VARCHAR(200) NOT NULL,
  entity_type VARCHAR(100),
  entity_id   BIGINT,
  details     TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

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
  ('cold_start.enabled',             'true',    TRUE,  'ADMIN'),
  ('algo.highlight.category',        'Sport',   FALSE, 'BDE'), -- RF-16 : Priorité ajustable
  ('matching.max_suggestions',       '10',      TRUE,  'ADMIN'), -- RF-17 : Règle figée
  ('academic.current_semester',      '1',       FALSE, 'ADMIN') -- TWIST 07 : Pilotage global
ON CONFLICT (rule_key) DO NOTHING;

-- ===== Catalogue d'intérêts prédéfinis (5 catégories) =====
INSERT INTO interest_catalog (tag, category, emoji, display_order) VALUES
  -- 🏀 Sport
  ('Football',    'Sport', '⚽', 1),
  ('Basketball',  'Sport', '🏀', 2),
  ('Tennis',      'Sport', '🎾', 3),
  ('Natation',    'Sport', '🏊', 4),
  ('Escalade',    'Sport', '🧗', 5),
  ('Yoga',        'Sport', '🧘', 6),
  ('Running',     'Sport', '🏃', 7),
  -- 🎵 Culture
  ('Musique',     'Culture', '🎵', 10),
  ('Théâtre',     'Culture', '🎭', 11),
  ('Cinéma',      'Culture', '🎬', 12),
  ('Photographie','Culture', '📷', 13),
  ('Danse',       'Culture', '💃', 14),
  ('Lecture',     'Culture', '📚', 15),
  -- 💻 Tech
  ('Programmation','Tech', '💻', 20),
  ('Intelligence Artificielle','Tech', '🤖', 21),
  ('Cybersécurité','Tech', '🔒', 22),
  ('Robotique',   'Tech', '🦾', 23),
  ('Data Science', 'Tech', '📊', 24),
  ('Web Design',  'Tech', '🎨', 25),
  -- 📚 Académique
  ('Mathématiques','Académique', '🔢', 30),
  ('Physique',    'Académique', '⚛️', 31),
  ('Droit',       'Académique', '⚖️', 32),
  ('Médecine',    'Académique', '🩺', 33),
  ('Langues',     'Académique', '🌍', 34),
  ('Économie',    'Académique', '📈', 35),
  -- 🎉 Social
  ('Soirées',     'Social', '🎉', 40),
  ('Voyages',     'Social', '✈️', 41),
  ('Bénévolat',   'Social', '🤝', 42),
  ('Cuisine',     'Social', '🍳', 43),
  ('Jeux de société','Social', '🎲', 44),
  ('Gaming',      'Social', '🎮', 45)
ON CONFLICT (tag) DO NOTHING;

-- ===== Utilisateur Admin par défaut (Module 2) =====
INSERT INTO users (id, nom, prenom, email, role) VALUES
  (1, 'Admin', 'CampusLink', 'admin@campuslink.fr', 'ADMIN')
ON CONFLICT (id) DO NOTHING;

-- Mettre à jour la séquence pour éviter les conflits lors des futurs inserts
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1));

INSERT INTO profile_contexts (user_id, filiere, annee, statut) VALUES
  (1, 'Administration', 5, 'PERSONNEL')
ON CONFLICT (user_id) DO NOTHING;
