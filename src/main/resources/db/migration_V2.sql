-- Migration 2.0 : ajout colonne description aux salles
ALTER TABLE salles ADD COLUMN IF NOT EXISTS description VARCHAR(255);

-- Index unique sur email des utilisateurs
CREATE UNIQUE INDEX IF NOT EXISTS idx_utilisateur_email ON utilisateurs(email);
