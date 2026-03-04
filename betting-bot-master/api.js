const express = require('express');
const DbService = require('./services/db.service');
const BotService = require('./services/bot.service');
const ApiService = require('./services/api.service');
const DateUtility = require('./utility/date.utilty');

let messageBus;

async function sendMessage(chat_id, text) {
    await BotService.sendMessage({ chat_id, text });
}

async function newCode({ chatId, code, dataDiScadenza }) {
    try {
        const data = await DbService.findGenerateCodeRequest(+chatId);
        await sendMessage(data.chatId, `Hello Better,\nla tua richiesta è stata approvata.\nCodice: ${code}\nScadenza: ${DateUtility.format(dataDiScadenza)}`);
        await DbService.removeGenerateCodeRequest(data._id);
        await DbService.registerToDailyDoubling(chatId);
    } catch (e) {
        throw e;
    }
}

async function renewCode({ chatId, code, paymentCode, dataDiScadenza }) {
    console.log("renewCode", chatId, code, paymentCode, dataDiScadenza);
    try {
        const data = await DbService.findRenewCodeRequest(+chatId);
        await sendMessage(+chatId, `Hello Better,\nla tua richiesta di rinnovo è stata approvata.\nCodice: ${code}\nScadenza: ${DateUtility.format(dataDiScadenza)}`);
        await DbService.removeRenewCodeRequest(data._id)
    } catch (e) {
        throw e;
    }
}

const router = express.Router();

/**
 * @Body
 * type
 * chatId
 * code
 * paymentCode
 * dataDiScadenza
 */
router.post('/esito', async function (req, res) {
    try {
        const { type, chatId, message } = req.body;
        if (message) {
            messageBus.emit('send-message', {
                chat_id: chatId,
                text: `${message}`
            });
            return res.send({ message: 'OK' });
        }
        switch (type) {
            case 'NEW':
                await newCode(req.body);
                break;
            case 'RENEW':
                await renewCode(req.body);
                break;
            default:
                return res.status(420).send({ error: 'Tipo non riconosciuto!' })
        }
    } catch (e) {
        console.error(e);
        return res.status(500).send({ error: 'Si è verificato un errore!' })
    }
    return res.send({ message: 'OK' });
});

/**
 * [
 *   {
 *       campionato: "",
 *       team: "",
 *       prediction: "",
 *       quota: "",
 *       dateMatch: "",
 *       presa: ""
 *   },
 *   ...
 */
router.post('/dailydoubling', async function (req, res) {
    try {
        const separetor = "---------------\n";
        let matches = separetor;
        let type = '';
        req.body.forEach((m) => {
            type = m.type;
            matches += `*Data:* ${m.dateMatch}\n*Campionato:* ${m.campionato}\n*Partita:* ${m.team}\n*Risultato:* ${m.prediction}\n*Quota:* ${m.quota}\n${separetor}`;
        });
        await DbService.saveDailyDoubling(type, matches);
        await ApiService.sendDailyDoubling(messageBus,type, matches, await DbService.findRegisteredToDailyDoubling());
    } catch (e) {
        console.error(e);
        return res.send({ error: 'KO' });
    }
    return res.send({ message: 'OK' });
});

/**
 *  {
 *      chats: [1,2,3,...],
 *      text: 'ciao a tutti i better'
 *   }
 */
router.post('/sendBroadcast', async function (req, res) {
    try {
        const { chats, text } = req.body;
        // distinct
        [...new Set(chats)].forEach((chat_id, i) => {
            // setTimeout(() => {
            messageBus.emit('send-message', { chat_id, text });
            // }, 1000 * i);
        });
    } catch (e) {
        console.error(e);
        return res.send({ error: 'KO' });
    }
    return res.send({ message: 'OK' });
});

/**
 *  {
 *      chatId: number,
 *      text: 'hello better'
 *   }
 */
router.post('/sendMessage', async function (req, res) {
    try {
        messageBus.emit('send-message', {
            chat_id: req.body.chatId,
            text: req.body.text
        });
    } catch (e) {
        console.error(e);
        return res.send({ error: 'KO' });
    }
    return res.send({ message: 'OK' });
});

module.exports = {
    setMessageBus: (mb) => messageBus = mb,
    router
};
