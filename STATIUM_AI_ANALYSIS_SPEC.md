# Statium AI Analysis Spec

## Overview
Implementare una feature premium chiamata **Analisi AI** dentro Statium.

L'utente vede un pulsante **Analisi AI** accanto a ogni partita nella lista match. Premendo il pulsante, il sistema genera un'analisi dettagliata della partita usando **API-Football v3** e la invia in **privato su Telegram** all'utente collegato al proprio account.

Ogni analisi costa **1 gettone**.

---

## Business model

### Token rules
- Ogni utente con rinnovo mensile attivo riceve **5 gettoni bonus** a ogni rinnovo.
- I gettoni bonus **scadono alla fine del ciclo mensile**.
- L'utente può acquistare **pacchetti extra di gettoni** direttamente in app.
- Ogni richiesta di **Analisi AI** consuma **1 gettone**.
- I gettoni acquistati dovrebbero preferibilmente **non scadere** (oppure avere scadenza molto lunga, non mensile).

### Consumption priority
Ordine di consumo consigliato:
1. bonus mensili in scadenza più vicina
2. gettoni acquistati

---

## User flow

### In app
Per ogni partita mostrare:
- prediction algoritmica standard già presente
- pulsante **Analisi AI**
- label **1 gettone**
- opzionale: saldo gettoni visibile

### On click
Quando l'utente preme **Analisi AI**:
1. backend verifica autenticazione
2. backend verifica Telegram collegato
3. backend verifica disponibilità gettoni
4. backend verifica dedupe / eventuale analisi già riutilizzabile
5. se necessario, backend riserva 1 gettone
6. backend crea un job asincrono
7. worker genera l'analisi
8. sistema invia il risultato su Telegram privato
9. se invio OK -> gettone consumato
10. se errore -> gettone rilasciato

### UX responses
- Se tutto ok: `Analisi in preparazione. Te la inviamo su Telegram.`
- Se non ha gettoni: `Hai finito i gettoni. Acquistane altri per continuare.`
- Se Telegram non è collegato: `Collega Telegram per ricevere l'analisi privata.`

---

## Core technical rule
**Non consumare mai il gettone al click.**

Usare sempre il flusso:

```text
reserve -> generate -> send -> consume/release
```

Questo evita di scalare gettoni in caso di errore API o fallimento invio Telegram.

---

## Data source
Fonte primaria dati:
- **API-Football v3**
- Base URL: `https://v3.football.api-sports.io`
- Auth header: `x-apisports-key`

Endpoint da usare secondo disponibilità:
- fixtures
- fixtures/statistics
- fixtures/lineups
- standings
- injuries
- fixtures/headtohead

---

## Analysis modes

### Prematch
Usare principalmente:
- forma squadre
- H2H
- standings
- injuries
- lineups se disponibili

### Live
Usare principalmente:
- minuto attuale
- punteggio live
- statistiche live
- momentum
- contesto live
- altri dati in-play disponibili

---

## Required analysis output
Ogni analisi deve includere sempre:
1. **1X2** (primary + fallback)
2. **Over/Under**
3. **Top 3 risultati esatti plausibili**
4. **Contesto sintetico**
5. **Note di rischio**
6. **Fonte dati**
7. **Tipo analisi** (`prematch` / `live`)
8. **Timestamp snapshot**
9. **Compliance note finale**

Esempio output Telegram:

```text
📊 Analisi AI — Fiorentina vs Parma

• 1X2: 1
  Fallback: X

• Over/Under: Under 2.5

• Risultati plausibili:
  - 1-0
  - 0-0
  - 2-0

• Contesto:
  Fiorentina superiore per controllo e qualità delle occasioni, ma gara ancora bloccata.

• Rischi:
  - match a margini stretti
  - bassa concretezza offensiva
  - possibile deriva verso pareggio

• Fonte dati: API-Football v3
• Tipo analisi: live
• Snapshot: 2026-03-08T16:50:00Z

Gettoni residui: 4

Analisi informativa/statistica. Nessun esito garantito.
```

---

## Dedupe policy
Per evitare doppio consumo inutile.

### Prematch
Una sola analisi per utente per fixture.

Chiave logica:
```text
prematch:userId:fixtureId
```

### Live
Una sola analisi per utente per fixture per finestra temporale.

Finestra consigliata:
- **10 minuti**

Chiave logica:
```text
live:userId:fixtureId:timeBucket
```

Esempio:
- richiesta live al 52' -> bucket A
- richiesta live al 56' -> stesso bucket A, non scalare un altro gettone
- richiesta live al 67' -> bucket B, nuova analisi, nuovo gettone

---

## Architecture

### Main modules
- `AnalysisController`
- `AnalysisService`
- `AnalysisJobWorker`
- `TokenService`
- `ApiFootballClient`
- `AnalysisEngine`
- `TelegramDeliveryService`
- `SubscriptionService`
- `PurchaseService`

### High-level flow
```text
UI click
-> token reserve
-> analysis job queued
-> API-Football fetch
-> analysis generation
-> Telegram DM
-> token consume/release
```

---

## Database design

### users
Required fields:
- `id`
- `telegram_chat_id`
- `telegram_linked`
- `subscription_status`
- `subscription_renewal_at`
- `subscription_expires_at`

### token_lots
Purpose: rappresentare i lotti di gettoni disponibili.

Required fields:
- `id`
- `user_id`
- `source_type` (`bonus_monthly`, `pack_purchase`, `admin_credit`)
- `source_ref`
- `tokens_total`
- `tokens_used`
- `expires_at`
- `created_at`
- `updated_at`

### token_reservations
Purpose: reserve / consume / release.

Required fields:
- `id`
- `user_id`
- `analysis_job_id`
- `status` (`reserved`, `consumed`, `released`, `expired`)
- `tokens_count`
- `created_at`
- `expires_at`
- `consumed_at`
- `released_at`

### token_reservation_items
Purpose: collegare reservation ai lotti usati.

Required fields:
- `id`
- `reservation_id`
- `token_lot_id`
- `tokens_count`
- `created_at`

### analysis_jobs
Required fields:
- `id`
- `user_id`
- `fixture_id`
- `mode` (`prematch`, `live`)
- `status` (`queued`, `processing`, `delivered`, `failed`)
- `reservation_id`
- `telegram_chat_id`
- `result_text`
- `result_json`
- `error_message`
- `dedupe_key`
- `created_at`
- `started_at`
- `completed_at`

### token_ledger
Purpose: storico contabile / audit.

Required fields:
- `id`
- `user_id`
- `event_type` (`grant`, `purchase`, `reserve`, `consume`, `release`, `expire`)
- `amount`
- `token_lot_id`
- `reservation_id`
- `analysis_job_id`
- `note`
- `created_at`

---

## API endpoints

### Request analysis
`POST /api/analysis/request`

Request:
```json
{
  "fixtureId": 123456,
  "mode": "live"
}
```

Success response:
```json
{
  "code": "OK",
  "message": "Analisi in preparazione. Te la inviamo su Telegram.",
  "jobId": 9912
}
```

Error responses:

```json
{
  "code": "INSUFFICIENT_TOKENS",
  "message": "Non hai gettoni disponibili"
}
```

```json
{
  "code": "TELEGRAM_NOT_LINKED",
  "message": "Collega Telegram per ricevere l'analisi"
}
```

### Token balance
`GET /api/tokens/balance`

Example response:
```json
{
  "bonusAvailable": 3,
  "paidAvailable": 7,
  "totalAvailable": 10
}
```

### Token packages
`GET /api/tokens/packages`

Example response:
```json
[
  { "id": "pack_5", "tokens": 5, "price": 4.99 },
  { "id": "pack_15", "tokens": 15, "price": 9.99 },
  { "id": "pack_50", "tokens": 50, "price": 24.99 }
]
```

### Purchase confirmation
`POST /api/tokens/purchase/confirm`

Provider-specific payload to be defined according to payment flow.

---

## Reservation logic

### Mandatory flow
```text
1. reserve token
2. create analysis job
3. fetch API-Football data
4. generate analysis
5. send Telegram DM
6. consume reservation if success
7. release reservation if failure
```

### Reservation timeout
Each reservation should expire after about **10 minutes**.
If the job never completes, the token must become available again.

---

## Renewal logic
At monthly renewal:
- create a new `token_lot`
- `source_type = bonus_monthly`
- `tokens_total = 5`
- `expires_at = cycle_end`
- write `grant` event in ledger

---

## Purchase logic
When a token pack purchase is confirmed:
- create a new `token_lot`
- `source_type = pack_purchase`
- `tokens_total = purchased_quantity`
- `expires_at = null` (recommended)
- write `purchase` event in ledger

---

## Error handling

### If API-Football fails
- mark job as `failed`
- release reservation

### If Telegram send fails
- mark job as `failed`
- release reservation

### If worker crashes after reservation
- reservation timeout / cleanup must release token again

---

## Admin / audit visibility
Recommended admin visibility:
- requested analyses
- delivered analyses
- failed analyses
- token grants
- token purchases
- token consumption
- token releases
- user token balances

---

## Suggested implementation phases

### Phase 1 - Core MVP
- DB schema
- token lots
- reservations
- analysis jobs
- request endpoint
- worker
- Telegram delivery
- API-Football integration

### Phase 2 - Hardening
- dedupe
- reservation timeout cleanup
- bonus expiration handling
- audit/admin visibility

### Phase 3 - Product polish
- full token balance UX
- monetization analytics
- improved Telegram formatting
- richer live refresh logic

---

## Acceptance criteria
The feature is correct if:
1. a user with at least 1 token can request an analysis
2. the system creates and processes the job asynchronously
3. the analysis is sent to the linked Telegram private chat
4. the token is consumed only after successful delivery
5. if an error occurs, the token becomes available again
6. 5 bonus tokens are granted at monthly renewal
7. bonus tokens expire at cycle end
8. purchased tokens are added correctly
9. duplicate requests do not consume unnecessary extra tokens inside the valid dedupe window

---

## Final implementation note
This feature should be built as the combination of three independent but connected blocks:
- **Token Service**
- **Analysis Service**
- **Telegram Delivery Service**

The reserve/consume/release logic is a core requirement and should not be simplified away.
