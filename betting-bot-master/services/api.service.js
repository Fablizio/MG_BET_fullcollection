const axios = require('axios');
const DateUtility = require('../utility/date.utilty');

const baseURL = process.env.APIURL;
const headers = {
    'cache-control': 'no-cache',
    'content-type': 'application/json'
};

function evaluateTitleByType(type) {
    switch (type) {
        case 'SINGLE_MATCH': return 'Singola del giorno';
        case 'TWO_MATCHES': return 'Doppia del giorno';
        case 'DOPPIA_CHANCE': return 'Doppia chance del giorno';
        default: return 'Raddoppio del giorno';
    }
}

module.exports = {
    async checkAccount(chatId) {
        console.log(`${baseURL}/chatId/${chatId}`);
        return await axios.get(`${baseURL}/chatId/${chatId}`, {headers})
            .then((response) => response.data);
    },
    async newCode(filePath, telegramSession, nickname, username) {
        return await axios.post(`${baseURL}/code`, {
            filePath,
            telegramSession,
            nickname,
            username
        }, {headers})
            .then((response) => response.data);
    },
    async renewCode(filePath, telegramSession) {
        return await axios.put(`${baseURL}/code`, {
            filePath,
            telegramSession
        }, {headers})
            .then((response) => response.data);
    },
    async requestTrial(telegramSession, nickname, username) {
        const data = {
            telegramSession: `${telegramSession}`,
            nickname,
            username
        };
        return await axios.post(`${baseURL}/trial`, data, {headers})
            .then((response) => response.data);
    },
    async sendDailyDoubling(messageBus, type, matches, registered) {
        const title = evaluateTitleByType(type);
        for (const chat of registered) {
            const {expiration} = await this.checkAccount(chat.chatId);
            if (DateUtility.isInFuture(expiration)) {
                messageBus.emit('send-message', {
                    chat_id: chat.chatId,
                    text: `Hello Better,\n*${title}*\n${matches}`,
                    parse_mode: "Markdown"
                });
            } else {
                messageBus.emit('send-message', {
                    chat_id: chat.chatId,
                    text: `Hello Better,\n${title} disponibile ma il tuo abbonamento è scaduto. Rinnova subito!!!\n 👉🏻 /rinnova_abbonamento`
                });
            }
        }
    },
    associateCode(telegramSession, code, nickname, username) {
        return axios.post(`${baseURL}/associaCodice`, {
            telegramSession,
            code,
            nickname,
            username
        }, {headers})
            .then((response) => response.data);
    }
};
