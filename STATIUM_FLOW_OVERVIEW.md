# Statium Flow Overview (MG_BET_fullcollection)

## Workspace mode
- Working on branch/fork-style lane: `statium-landing-fork`
- `main` untouched.

## Repository structure and role
- `betting-fe-main/` → Angular frontend (user app, pages, dashboard, history, payments)
- `betting-be-main/` → main backend service (Java/Spring style structure)
- `betting-backoffice-demo/` → backoffice/admin side
- `betting-batch-demo/` → scheduled/batch jobs
- `betting-bot-master/` + `betting-bot-2.0-main/` → bot logic/services/utility
- `betting-app-master/` → additional app frontend variant

## High-level flow
1. Data + model logic produced by bot services/batch components
2. Backend APIs expose data/events
3. Frontend renders predictions, history, pages and user actions
4. Backoffice supports operational/admin workflows

## Statium brand update implemented (frontend)
- New landing component added in `betting-fe-main/src/app/statium-landing/`
- Routing updated:
  - `/` → Statium landing
  - `/statium` → Statium landing
  - `/auth` → previous auth page
- Landing includes:
  - Rebrand narrative (Money Glitch → Statium)
  - What Statium does
  - How team operates
  - Collaboration highlight with The Funded Pick
  - Risk/disclaimer note
