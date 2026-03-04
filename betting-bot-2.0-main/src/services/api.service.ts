import axios from 'axios';
import {env} from '../env.js';

function evaluateTitleByType(type: string) {
    switch (type) {
        case 'SINGLE_MATCH':
            return 'Singola del giorno';
        case 'TWO_MATCHES':
            return 'Doppia del giorno';
        case 'DOPPIA_CHANCE':
            return 'Doppia chance del giorno';
        default:
            return 'Raddoppio del giorno';
    }
}

const http = axios.create({ baseURL: env.API_URL, headers: { 'content-type': 'application/json' } });

async function fileUrlToBase64(fileUrl: string): Promise<string> {
    const res = await axios.get<ArrayBuffer>(fileUrl, { responseType: 'arraybuffer' });
    const buff = Buffer.from(res.data);
    return buff.toString('base64');
}

export const ApiService = {
    async requestTrialCode(telegramSession: string, nickname?: string, username?: string) {
        try {
            const { data } = await http.post('/trial', { telegramSession, nickname, username });
            return data; // { ok: true, code?: string, ... }
        } catch (err: any) {
            console.log(err);
        }
    },

    async getTelegramSessionActive() {
        try {
            const { data } = await http.get('/telegram-session');
            return data;
        } catch (err: any) {
            console.log(err);
        }
    },

    async pushDailyDoubling(toChatId: number, payload: { type: string; matches: string[] }) {
        const title = evaluateTitleByType(payload.type);
        const text = `Hello Better,\n${title} disponibile!\n\n${payload.matches.join('\n')}\n\nBuona fortuna!`;
        return { chat_id: toChatId, text };
    },

    // ✅ NUOVO: invio screenshot pagamento verso /api/sendImage con base64Image + telegramId
    async sendPaymentScreenshot(telegramSession: string, fileUrl: string) {
        try {
            const base64Image = await fileUrlToBase64(fileUrl);
            const { data } = await http.post('/sendImage', {
                base64Image,
                telegramId: telegramSession,
            });
            return data; // atteso: { ok: true } o simile
        } catch (err: any) {
            console.log(err);
        }
    },
    // 🔹 NUOVO: recupero info account per telegramId
    async getAccountInfo(telegramId: string) {
        try {
            // ATTENZIONE: assumo che env.API_URL sia già .../api
            const { data } = await http.get(`/chatId/${telegramId}`);
            return data;
        } catch (err: any) {
            console.log(err);
        }
    },
};
