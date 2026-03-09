# Statium AI Analysis Task List

## Goal
Implementare la feature **Analisi AI** a consumo tramite gettoni, con consegna privata su Telegram.

---

## Epic 1 - Product / UX

### Task 1.1 - Add AI Analysis button in match list
- Add button `Analisi AI` next to each fixture row/card.
- Show label `1 gettone`.
- Disable while request is in progress.

### Task 1.2 - Token balance visibility
- Show token balance in app header or relevant dashboard area.
- Distinguish bonus vs paid balance if useful.

### Task 1.3 - No-token UX
- Add modal / popup when user has no available tokens.
- Copy example:
  - `Hai finito i gettoni.`
  - `Con il rinnovo mensile ne ricevi 5.`
  - `Oppure acquista un pacchetto.`
- CTA: `Acquista gettoni`

### Task 1.4 - Telegram not linked UX
- Add blocking state if user requests analysis without linked Telegram.
- Copy example:
  - `Collega Telegram per ricevere l'analisi privata.`

---

## Epic 2 - Database / persistence

### Task 2.1 - Add token lots table
- Create `token_lots` table.
- Support source types:
  - `bonus_monthly`
  - `pack_purchase`
  - `admin_credit`

### Task 2.2 - Add token reservations tables
- Create `token_reservations`
- Create `token_reservation_items`
- Support statuses:
  - `reserved`
  - `consumed`
  - `released`
  - `expired`

### Task 2.3 - Add analysis jobs table
- Create `analysis_jobs`
- Support modes:
  - `prematch`
  - `live`
- Support statuses:
  - `queued`
  - `processing`
  - `delivered`
  - `failed`

### Task 2.4 - Add token ledger
- Create `token_ledger`
- Track:
  - `grant`
  - `purchase`
  - `reserve`
  - `consume`
  - `release`
  - `expire`

---

## Epic 3 - Token service

### Task 3.1 - Compute available token balance
- Build service to calculate available balance from token lots.
- Ignore expired lots.
- Consumption priority:
  1. bonus monthly first
  2. purchased tokens after

### Task 3.2 - Reserve one token
- Implement reservation flow.
- Do not decrement lot usage yet.
- Reservation must expire after ~10 minutes.

### Task 3.3 - Consume reservation
- On successful Telegram delivery:
  - increment `tokens_used`
  - mark reservation `consumed`
  - write ledger event `consume`

### Task 3.4 - Release reservation
- On failure:
  - mark reservation `released`
  - write ledger event `release`

### Task 3.5 - Expired reservation cleanup
- Add cleanup job for expired reservations.
- Release token automatically if reservation timed out.

---

## Epic 4 - Subscription / purchases

### Task 4.1 - Monthly bonus grant
- At each subscription renewal:
  - create token lot with 5 bonus tokens
  - expiration = subscription cycle end
  - write ledger event `grant`

### Task 4.2 - Purchased token pack grant
- On successful purchase:
  - create token lot with purchased quantity
  - recommended `expires_at = NULL`
  - write ledger event `purchase`

### Task 4.3 - Bonus expiration handling
- Add scheduled job or logical expiry handling for expired bonus tokens.
- Write ledger event `expire` if needed for audit visibility.

---

## Epic 5 - Analysis request API

### Task 5.1 - Create POST /api/analysis/request
- Validate authentication.
- Validate input (`fixtureId`, `mode`).
- Validate linked Telegram.
- Check dedupe.
- Reserve token if needed.
- Create async analysis job.

### Task 5.2 - Create GET /api/tokens/balance
- Return:
  - `bonusAvailable`
  - `paidAvailable`
  - `totalAvailable`

### Task 5.3 - Create GET /api/tokens/packages
- Return token pack catalog.

### Task 5.4 - Create POST /api/tokens/purchase/confirm
- Finalize token pack purchase after payment confirmation.

---

## Epic 6 - Dedupe strategy

### Task 6.1 - Prematch dedupe
- One analysis per user + fixture.
- Reuse until kickoff or defined invalidation moment.

### Task 6.2 - Live dedupe
- Use time buckets (recommended 10 minutes).
- Same user + same fixture + same bucket should not consume extra token.

### Task 6.3 - Reuse existing analysis result
- If analysis already exists inside valid dedupe window:
  - do not create new token reservation
  - return success / reuse message

---

## Epic 7 - API-Football integration

### Task 7.1 - Build API-Football client
- Integrate base URL `https://v3.football.api-sports.io`
- Send `x-apisports-key`

### Task 7.2 - Fixture packet builder
- Build match packet from:
  - fixtures
  - statistics
  - lineups
  - standings
  - injuries
  - head-to-head

### Task 7.3 - Support prematch mode
- Prefer form / H2H / standings / injuries / lineups.

### Task 7.4 - Support live mode
- Prefer score / minute / statistics / momentum / context.

---

## Epic 8 - Analysis engine

### Task 8.1 - Define structured analysis JSON contract
- Output must include:
  - match info
  - oneXtwo
  - overUnder
  - exactScores
  - context
  - risks
  - source
  - generatedAt
  - compliance

### Task 8.2 - Implement deterministic scoring logic
- Score home edge
- Score goal environment
- Score volatility
- Score momentum (live)

### Task 8.3 - Market selection logic
- Choose:
  - 1X2 primary + fallback
  - Over/Under line + direction
  - top 3 exact scores

### Task 8.4 - Risk note builder
- Add concise caution notes to every output.

---

## Epic 9 - Telegram delivery

### Task 9.1 - Build Telegram formatter
- Format final analysis for private DM.
- Include:
  - match title
  - 1X2
  - over/under
  - exact scores
  - context
  - risks
  - source
  - mode
  - snapshot timestamp
  - optional remaining token balance
  - compliance note

### Task 9.2 - Build Telegram send service
- Deliver analysis to linked user `telegram_chat_id`.
- Return success/failure explicitly.

### Task 9.3 - Handle send failure safely
- If send fails:
  - job = failed
  - reservation = released

---

## Epic 10 - Async worker / queue

### Task 10.1 - Create analysis queue worker
- Process queued jobs asynchronously.

### Task 10.2 - Worker success path
- Fetch packet
- Generate analysis
- Format Telegram message
- Send Telegram DM
- Consume token reservation
- Mark job delivered

### Task 10.3 - Worker failure path
- Mark job failed
- Release token reservation
- Persist error reason

---

## Epic 11 - Admin / observability

### Task 11.1 - Admin visibility for token events
- View grants, purchases, reservations, consumes, releases, expirations.

### Task 11.2 - Admin visibility for analysis jobs
- View queued, processing, delivered, failed jobs.

### Task 11.3 - Monitoring / logs
- Add logs for:
  - reserve
  - consume
  - release
  - API-Football errors
  - Telegram delivery errors

---

## Epic 12 - Acceptance checklist

### Task 12.1 - User with token can request analysis
### Task 12.2 - Analysis goes to Telegram private chat
### Task 12.3 - Token is consumed only after successful send
### Task 12.4 - Token is released on error
### Task 12.5 - 5 bonus tokens granted on renewal
### Task 12.6 - Bonus tokens expire with cycle
### Task 12.7 - Purchased tokens are added correctly
### Task 12.8 - Duplicate requests do not consume extra tokens inside valid dedupe window

---

## Recommended delivery order
1. DB schema
2. Token service
3. Analysis request endpoint
4. Queue + worker
5. API-Football packet builder
6. Analysis engine
7. Telegram delivery
8. UX polish
9. Admin visibility
