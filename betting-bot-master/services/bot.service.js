const axios = require('axios');
const DbService = require('./db.service');
const ApiService = require('./api.service');
const DateUtility = require('../utility/date.utilty');

const baseURL = `${process.env.TELEGRAMAPI}/bot${process.env.TELEGRAMTOKEN}`;
const baseFileURL = `${process.env.TELEGRAMAPI}/file/bot${process.env.TELEGRAMTOKEN}`;
const webhookURL = `${process.env.WEBHOOKURL}`;
const site = `${process.env.SITE}`;

const headers = {
    'cache-control': 'no-cache',
    'content-type': 'application/json'
};

let messageBus;

module.exports = {
    async webHookInit(mb) {
        try {
            messageBus = mb;
            await axios.get(`${baseURL}/deleteWebhook`, {headers});
            await axios.post(`${baseURL}/setWebhook`, {url: webhookURL}, {headers});
            messageBus.emit('webhook-registered', webhookURL)
        } catch (e) {
            console.error(e);
        }
    },
    async sendMessage(data) {
        return await axios.post(`${baseURL}/sendMessage`, data, {headers});
    },
    async getFileUrl(file_id) {
        const getFile = await axios.get(`${baseURL}/getFile`, {
            params: {file_id},
            headers
        });
        return `${baseFileURL}/${getFile.data.result.file_path}`;
    },
    async sayWhatCanIdo(message) {
        messageBus.emit('send-message', {
            chat_id: message.chat.id,
            text: `Hello Better! Per sapere cosa posso fare clicca qui 👉🏻 /help`
        })
    },
    async sayHello(message) {
        messageBus.emit('send-message', {
            chat_id: message.chat.id,
            text: `Hello Better`
        });
    },
    async commandNotFound(message) {
        messageBus.emit('send-message', {
            chat_id: message.chat.id,
            text: `Mi dispiace ma non posso soddisfare la tua richiesta. Utilizza il comando /help per sapere cosa puoi fare`
        });
    },
    async startCommand(message) {
        await this.sayWhatCanIdo(message);
    },
    async helpCommand(message) {
        await DbService.removeCommandRequested(message.chat.id);
        messageBus.emit('send-message', {
            chat_id: message.chat.id,
            text: 'Su quale funzionalità hai bisogno di aiuto?',
            reply_markup: {
                inline_keyboard: [
                    [
                     {
                         text: "attiva codice",
                         callback_data: "attiva_abbonamento"
                     },
                        {
                            text: "rinnova abbonamento",
                            callback_data: "rinnova_abbonamento"
                        }
                    ],
                    [
                        {
                            text: "controlla account",
                            callback_data: "controlla_account"
                        },
                        {
                            text: "raddoppio del giorno",
                            callback_data: "raddoppio_del_giorno"
                        }
                    ],
                    [
                        {
                            text: "rimuovi raddoppio del giorno",
                            callback_data: "rimuovi_raddoppio_del_giorno"
                        },
                        {
                            text: "richiedi codice di prova",
                            callback_data: "richiedi_codice_di_prova"
                        }
                    ],
                    [
                        {
                            text: "associa codice",
                            callback_data: "associa_codice"
                        }
                    ]
                ]
            }
        })
    },
    helpGenerateCode(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: `La funzione attiva_abbonamento permette di richiedere la creazione di un abbonamento personale e la generazione del codice con validità 1 mese per accedere al sito,\n ` +
                'Richiedi il codice di prova cliccando qui 👉🏻 /richiedi_codice_di_prova '
        });
    },
    helpRenewCode(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: `La funzione rinnova_abbonamento consente di rinnovare l'abbonamento ed estendere di 1 mese la validità del codice di accesso.\n` +
                ' 👉🏻 /rinnova_abbonamento'
        });
    },
    helpCheckAccount(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: 'La funzione controlla_account ti mostra tutte le informazione del tuo account' +
                ' \n' +
                ' 👉🏻 /controlla_account'
        });
    },
    helpRegisterToDailyDoubling(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: 'La funzione raddoppio_del_giorno permette di iscriversi alla notifica quotidiana del raddoppio del giorno \n' +
                ' 👉🏻 /raddoppio_del_giorno'
        });
    },
    helpDeregisterToDailyDoubling(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: 'La funzione rimuovi_raddoppio_del_giorno rimuove la propria iscrizione alla notifica quotidiana del raddoppio del giorno \n' +
                ' 👉🏻 /rimuovi_raddoppio_del_giorno'
        });
    },
    helpTrialCode(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: `La funzione /richiedi_codice_di_prova permette di richiedere un codice gratuito dalla durata di due giorni per accedere al sito. ` +
                ' \n 👉🏻 /richiedi_codice_di_prova'
        });
    },
    helpAssociateCode(from) {
        messageBus.emit('send-message', {
            chat_id: from.id,
            text: `La funzione /associa_codice permette di associare il tuo codice al bot, dandoti accesso a tutte le sue funzioni!` +
                ' \n 👉🏻 /associa_codice'
        });
    },
    async generateCodeCommand(message) {
        let text;
   //    try {
   //        const {expiration} = await ApiService.checkAccount(message.chat.id);
   //        if (DateUtility.isInFuture(expiration)) {
   //            text = `E' già presente un abbonamento attivo che scadrà il ${DateUtility.format(expiration)}`;
   //        } else {
   //            await DbService.setCommandRequested(message.chat.id, 'generateCode:paymentCode');
   //            text = 'Per richiedere la generazione del codice fotografare il codice paysafecard';
   //        }
   //    } catch (e) {
   //        if (e.request.res.statusCode === 404) {
   //            await DbService.setCommandRequested(message.chat.id, 'generateCode:paymentCode');
   //            text = 'Per richiedere la generazione del codice fotografare il codice paysafecard';
   //        } else {
   //            text = `Si è verificato un problema.\nRiprovare più tardi.\nSe il problema persiste contattare l'amministratore.`
   //        }
   //    }


        try {
            const {expiration} = await ApiService.checkAccount(message.chat.id);
            if (expiration && DateUtility.isInFuture(expiration)) {
                text = `E' già presente un abbonamento attivo che scadrà il ${DateUtility.format(expiration)}`;
            } else {
                text = "Contatta colui che ti ha fatto conoscere MG BET! 😁 "
            }
        }catch (e){
            console.log(e)
        }

        messageBus.emit('send-message', {
            chat_id: message.chat.id,
            text: text
        });
    },
    async generateCode(chatId, nickname, message, username) {
        console.log('generateCode:paymentCode', username);
        let text = 'La tua richiesta è stata inviata con successo!\nRiceverai un messaggio con il codice a breve';
        try {
            if (!message.photo) {
                text = `Per richiedere la generazione del codice fotografare il codice paysafecard `
            } else {
                const photo = message.photo[0];
                const fileUrl = await this.getFileUrl(photo.file_id);
                await ApiService.newCode(fileUrl, chatId, nickname, username);
                await DbService.removeCommandRequested(chatId);
                await DbService.registerToGenerateCode(chatId, photo, nickname);
            }
        } catch (e) {
            if (e.request.res.statusCode === 422) {
                text = "E' già presente un abbonamento attivo";
            } else {
                text = `Si è verificato un problema.\nRiprovare più tardi.\nSe il problema persiste contattare l'amministratore.`;
            }
            await DbService.removeCommandRequested(chatId);
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    },
    async renewCodeCommand(message) {
        let chatId = message.chat.id;
        console.log('renewCodeCommand', chatId);
        let text;
        try {
            const {expiration} = await ApiService.checkAccount(chatId);
            if (!DateUtility.isInFuture(expiration)) {
                //await DbService.setCommandRequested(message.chat.id, 'renewCode:paymentCode');
               // text = 'Per richiedere il rinnovo fotografare il codice paysafecard';

                text = "Contatta colui che ti ha fatto conoscere MG BET! 😁 "

            } else {
                text = `Il codice fornito scadrà il ${DateUtility.format(expiration)}`;
            }
        } catch (e) {
            console.error(e);
            text = `Non hai ancora un account. \nRichiedi la prova gratuita cliccando qui 👇 \n /richiedi_codice_di_prova`;
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    },
    async renewCodeVerifyPaymentCode(chatId, message) {
        let text = 'La tua richiesta di rinnovo è stata inviata con successo!\nRiceverai un messaggio con il codice a breve';
        try {
            if (!message.photo) {
                text = `Per richiedere il rinnovo fotografare il codice paysafecard.`
            } else {
                const photo = message.photo[0];
                const fileUrl = await this.getFileUrl(photo.file_id);
                await ApiService.renewCode(fileUrl, chatId);
                await DbService.removeCommandRequested(chatId);
                await DbService.registerToRenewCode(chatId, photo);
            }
        } catch (e) {
            console.error(e);
            text = `Si è verificato un problema.\nRiprovare più tardi.\nSe il problema persiste contattare l'amministratore.`;
            await DbService.removeCommandRequested(chatId);
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    },
    async checkAccountCommand(message) {
        let chatId = message.chat.id;
        console.log('checkAccountCommand', chatId);
        let text;
        try {
            const account = await ApiService.checkAccount(chatId);
            text = `Codice: ${account.code}\nData di scadenza: ${DateUtility.format(account.expiration)}`;
            await DbService.removeCommandRequested(chatId);
        } catch (e) {
            text = `Non hai ancora un account.`
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    },
    async registerToDailyDoublingCommand(message) {
        let chatId = message.chat.id;
        console.log('registerToDailyDoubling', chatId);
        let text;
        try {
            const account = await ApiService.checkAccount(chatId);
            if (DateUtility.isInFuture(account.expiration)) {
                await DbService.removeCommandRequested(chatId);
                await DbService.registerToDailyDoubling(chatId);
                text = `Registrazione effettuata con successo. Riceverai un messaggio con il raddoppio del giorno.`;
                const dailyDoublingList = await DbService.getLastDailyDoubling();
                if (dailyDoublingList) {
                    for (const dailyDoubling of dailyDoublingList) {
                        await ApiService.sendDailyDoubling(messageBus, dailyDoubling.type, dailyDoubling.matches, [{chatId}]);
                    }
                }
            } else {
                text = `Il codice fornito è scaduto il ${DateUtility.format(account.expiration)}`;
            }
        } catch (e) {
            console.error('registerToDailyDoubling:code', e);
            text = `Non hai ancora un account.`
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    },
    async deregisterToDailyDoublingCommand(message) {
        const data = await DbService.deregisterToDailyDoubling(message.chat.id);
        let text = 'Ok! Non riceverai più il raddoppio del giorno.';
        if (data.deletedCount === 0) {
            text = 'Non sei iscritto al raddoppio del giorno.\nPer iscriverti clicca qui 👉🏻 /raddoppio_del_giorno'
        }
        messageBus.emit('send-message', {
            chat_id: message.chat.id,
            text
        });
    },
    async trialCodeCommand(message) {
        console.log('trialCodeCommand', message.chat.id);
        let text = 'Hello Better,\nla tua richiesta per il codice di prova è stata approvata.';
        try {
            const data = await ApiService.requestTrial(message.chat.id, message.chat.first_name, message.chat.username);
            text = `${text}\nCodice: ${data.code}\nScadenza: ${data.expiration}\n Sito: ${site}`;
            await DbService.removeCommandRequested(message.chat.id);
        } catch (e) {
            if (e.request.res.statusCode === 422) {
                text = "E' già presente un abbonamento attivo, o hai già richiesto il codice di prova";
            } else {
                text = `Si è verificato un problema.\nRiprovare più tardi.\nSe il problema persiste contattare l'amministratore.`;
            }
        }
        return this.sendMessage({
            chat_id: message.chat.id,
            text
        });
    },
    async associateCode(message) {
        let chatId = message.chat.id;
        console.log('associateCodeCommand', chatId);
        let text;
        try {
            await DbService.setCommandRequested(chatId, 'associateCode:code');
            text = 'Quale codice vuoi associare al bot?'
        } catch (e) {
            console.error(e);
            text = `Si è verificato un problema, riprovare più tardi.\nSe il problema persiste contattaci.`;
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    },
    async associateCodeVerifyCode(chatId, message) {
        let text = 'Adesso puoi usare tutte le funzioni del bot!\nPuoi visualizzare il tuo account con il comando\n' +
            ' 👉🏻 /controlla_account';
        try {
            await DbService.removeCommandRequested(chatId);
            await ApiService.associateCode(chatId, message.text, message.chat.first_name, message.chat.username);
        } catch (e) {
            console.error(e);
            const status = e.response.status;
            switch (status) {
                case 404:
                    text = `Il codice fornito non è valido.`;
                    break;
                case 403:
                    text = `Il codice inserito è già usato da un altro utente.`;
                    break;
                case 422:
                    text = `Codice già associato alla tua utenza.`;
                    break;
                default:
                    text = `Si è verificato un problema.\nRiprovare più tardi.\nSe il problema persiste contattare l'amministratore.`;
                    break;
            }
        }
        try {
            await this.registerToDailyDoublingCommand({chat: {id: chatId}});
        } catch (e) {
            console.error(e);
            text = `Si è verificato un problema.\nRiprovare più tardi.\nSe il problema persiste contattare l'amministratore.`;
        }
        return this.sendMessage({
            chat_id: chatId,
            text
        });
    }
};
