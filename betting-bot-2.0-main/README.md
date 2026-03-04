# mg-bet-bot — Refactor 2025 (TypeScript + grammY)

## Setup rapido
```bash
cp .env.example .env
npm ci
npm run dev
```

- Imposta `PUBLIC_URL` (https pubblico del tuo server) per il webhook.
- Richiede MongoDB 6+.

## Comandi/feature portati
- `/start` + menu persistente
- Registrazione/deregistrazione **Raddoppio del giorno**
- **Richiedi codice di prova** e **Associa codice** (regex “ASSOCIA CODICE: <code>”)
- Endpoint API compatibili: `POST /api/newCode`, `POST /api/pushDailyDoubling`

## Note di migrazione
- Sostituito `moment` con **dayjs**.
- Aggiornato driver **mongodb** (v6+) con `MongoClient` moderno.
- Uso di **grammY** per aderire rapidamente alle ultime **Telegram Bot API**.
