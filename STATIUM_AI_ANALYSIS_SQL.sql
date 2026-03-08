-- Statium AI Analysis - MySQL schema draft
-- Purpose: token-based AI analysis delivery via private Telegram DM

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY,
  email VARCHAR(255) NULL,
  telegram_chat_id VARCHAR(64) NULL,
  telegram_linked BOOLEAN NOT NULL DEFAULT FALSE,
  subscription_status VARCHAR(32) NOT NULL DEFAULT 'inactive',
  subscription_renewal_at DATETIME NULL,
  subscription_expires_at DATETIME NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS token_lots (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  source_type VARCHAR(32) NOT NULL, -- bonus_monthly | pack_purchase | admin_credit
  source_ref VARCHAR(64) NULL,
  tokens_total INT NOT NULL,
  tokens_used INT NOT NULL DEFAULT 0,
  expires_at DATETIME NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_token_lots_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_token_lots_user ON token_lots(user_id);
CREATE INDEX idx_token_lots_source ON token_lots(source_type);
CREATE INDEX idx_token_lots_expiry ON token_lots(expires_at);

CREATE TABLE IF NOT EXISTS token_reservations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  analysis_job_id BIGINT NULL,
  status VARCHAR(32) NOT NULL, -- reserved | consumed | released | expired
  tokens_count INT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  expires_at DATETIME NOT NULL,
  consumed_at DATETIME NULL,
  released_at DATETIME NULL,
  CONSTRAINT fk_token_reservations_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_token_reservations_user ON token_reservations(user_id);
CREATE INDEX idx_token_reservations_status ON token_reservations(status);
CREATE INDEX idx_token_reservations_expiry ON token_reservations(expires_at);

CREATE TABLE IF NOT EXISTS token_reservation_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  token_lot_id BIGINT NOT NULL,
  tokens_count INT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_token_reservation_items_reservation FOREIGN KEY (reservation_id) REFERENCES token_reservations(id),
  CONSTRAINT fk_token_reservation_items_lot FOREIGN KEY (token_lot_id) REFERENCES token_lots(id)
);

CREATE INDEX idx_token_reservation_items_reservation ON token_reservation_items(reservation_id);
CREATE INDEX idx_token_reservation_items_lot ON token_reservation_items(token_lot_id);

CREATE TABLE IF NOT EXISTS analysis_jobs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  fixture_id BIGINT NOT NULL,
  mode VARCHAR(16) NOT NULL, -- prematch | live
  status VARCHAR(32) NOT NULL, -- queued | processing | delivered | failed
  reservation_id BIGINT NULL,
  telegram_chat_id VARCHAR(64) NULL,
  result_text LONGTEXT NULL,
  result_json JSON NULL,
  error_message TEXT NULL,
  dedupe_key VARCHAR(128) NULL,
  created_at DATETIME NOT NULL,
  started_at DATETIME NULL,
  completed_at DATETIME NULL,
  CONSTRAINT fk_analysis_jobs_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_analysis_jobs_reservation FOREIGN KEY (reservation_id) REFERENCES token_reservations(id)
);

CREATE INDEX idx_analysis_jobs_user ON analysis_jobs(user_id);
CREATE INDEX idx_analysis_jobs_fixture ON analysis_jobs(fixture_id);
CREATE INDEX idx_analysis_jobs_status ON analysis_jobs(status);
CREATE INDEX idx_analysis_jobs_dedupe_key ON analysis_jobs(dedupe_key);
CREATE INDEX idx_analysis_jobs_created_at ON analysis_jobs(created_at);

CREATE TABLE IF NOT EXISTS token_ledger (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  event_type VARCHAR(32) NOT NULL, -- grant | purchase | reserve | consume | release | expire
  amount INT NOT NULL,
  token_lot_id BIGINT NULL,
  reservation_id BIGINT NULL,
  analysis_job_id BIGINT NULL,
  note VARCHAR(255) NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_token_ledger_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_token_ledger_lot FOREIGN KEY (token_lot_id) REFERENCES token_lots(id),
  CONSTRAINT fk_token_ledger_reservation FOREIGN KEY (reservation_id) REFERENCES token_reservations(id),
  CONSTRAINT fk_token_ledger_analysis_job FOREIGN KEY (analysis_job_id) REFERENCES analysis_jobs(id)
);

CREATE INDEX idx_token_ledger_user ON token_ledger(user_id);
CREATE INDEX idx_token_ledger_event_type ON token_ledger(event_type);
CREATE INDEX idx_token_ledger_created_at ON token_ledger(created_at);

-- Recommended logical rules:
-- 1. Bonus monthly lots expire at subscription cycle end.
-- 2. Purchased lots should preferably have expires_at = NULL.
-- 3. Consume order: monthly bonus first, then purchased tokens.
-- 4. Do not consume token at click time.
--    Use: reserve -> generate -> send telegram -> consume/release.
-- 5. Live dedupe recommended with 10-minute buckets.
