import express from 'express';
import cors from 'cors';
import {botWebhook, ensureWebhook} from './bot/bot.js';
import {env} from './env.js';
import {BotService} from './services/bot.service.js';
import {ApiService} from './services/api.service.js';

export function createServer() {
    const app = express();
    app.use(cors());
    app.use(express.json({limit: '5mb'}));

    // Endpoint Webhook Telegram
    app.post('/webhook', botWebhook);
    // ✅ NUOVO ENDPOINT: Broadcast message
    app.post('/api/sendBroadcast', async (req, res) => {
        try {
            const {text, chats} = req.body;

            if (!text || !Array.isArray(chats)) {
                return res.status(400).send({error: 'Invalid payload: {text, chats[]}'});
            }

            // Invia il messaggio a tutti i chat_id
            for (const chatId of chats) {
                try {
                    await BotService.sendMessage({
                        chat_id: chatId,
                        text: text
                    });
                } catch (err) {
                    console.error(`Errore nell'invio al chat ${chatId}:`, err);
                }
            }

            return res.send({message: 'Broadcast sent', count: chats.length});
        } catch (e) {
            console.error(e);
            return res.status(500).send({error: 'KO'});
        }
    });
    app.post('/api/pushDailyDoubling', async (req, res) => {
        try {
            const matchesArray = req.body;

            if (!Array.isArray(matchesArray) || matchesArray.length === 0) {
                return res.status(400).send({ error: 'Invalid payload: expected an array of matches' });
            }

            const separator = '━━━━━━━━━\n';

            // Prendo il type dalla prima schedina (tutte dovrebbero avere lo stesso type)
            const type = matchesArray[0]?.type || '';

            let message = '🔥 *DAILY DOUBLING* 🔥\n\n';

            // ========================
            //  SINGLE_MATCH --> come prima
            // ========================
            if (type === 'SINGLE_MATCH') {
                for (const m of matchesArray) {
                    message +=
                        `📅 *Data:* ${m.dateMatch}\n` +
                        `🏆 *Campionato:* ${m.campionato}\n` +
                        `⚽ *Partita:* ${m.team}\n` +
                        `📊 *Pronostico:* ${m.prediction}\n` +
                        `💰 *Quota:* ${m.quota}\n` +
                        separator;
                }

                // ========================
                //  TWO_MATCHES --> visualizzazione combinata
                // ========================
            } else if (type === 'TWO_MATCHES') {

                message +=
                    '🎟 *Combinazione di 2 eventi*\n' +
                    'Di seguito una selezione di due eventi a fini esclusivamente informativi e statistici.\n\n';

                // Dettaglio eventi
                matchesArray.forEach((m, index) => {
                    message +=
                        `${index + 1}️⃣ *Evento ${index + 1}*\n` +
                        `📅 *Data:* ${m.dateMatch}\n` +
                        `🏆 *Campionato:* ${m.campionato}\n` +
                        `⚽ *Partita:* ${m.team}\n` +
                        `📊 *Pronostico:* ${m.prediction}\n` +
                        `💰 *Quota:* ${m.quota}\n\n`;
                });

                // Quota totale informativa
                const totalOdds = matchesArray.reduce((acc, m) => {
                    const q = Number(m.quota);
                    return acc * (isNaN(q) ? 1 : q);
                }, 1);

                message += `📈 *Quota totale (dato informativo):* ${totalOdds.toFixed(2)}\n`;
                message += separator;

                // ========================
                //  Fallback generico (altri type)
                // ========================
            } else {
                for (const m of matchesArray) {
                    message +=
                        `📅 *Data:* ${m.dateMatch}\n` +
                        `🏆 *Campionato:* ${m.campionato}\n` +
                        `⚽ *Partita:* ${m.team}\n` +
                        `📊 *Pronostico:* ${m.prediction}\n` +
                        `💰 *Quota:* ${m.quota}\n` +
                        separator;
                }
            }

            // ✅ DISCLAIMER LEGALE (senza usare la parola "giocare")
            const disclaimer =
                '\n⚠️ *Disclaimer*\n' +
                'Le informazioni fornite da questo servizio hanno esclusivamente scopo informativo e statistico.\n' +
                'Non costituiscono in alcun modo un invito alla scommessa o alla partecipazione ad attività di gioco d’azzardo.\n' +
                'Nessuna garanzia è offerta circa l’esattezza o l’esito delle previsioni. Utilizza sempre queste informazioni con responsabilità.';

            message += disclaimer;

            const data = await ApiService.getTelegramSessionActive();

            // Invio messaggio a tutti
            for (const chat_id of data.sessions) {
                try {
                    await BotService.sendMessage({
                        chat_id: chat_id,
                        text: message,
                        parse_mode: 'Markdown'
                    });
                } catch (err) {
                    console.error(`Errore nell'invio al chat ${chat_id}:`, err);
                }
            }

            return res.send({ message: 'Daily Doubling inviato correttamente!' });
        } catch (e) {
            console.error('Errore pushDailyDoubling:', e);
            return res.status(500).send({ error: 'KO' });
        }
    });



    return app;
}

export async function startServer(app = createServer()) {
    await ensureWebhook(env.PUBLIC_URL);
    return new Promise<void>((resolve) => {
        app.listen(env.PORT, () => resolve());
    });
}
