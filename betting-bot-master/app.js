require('dotenv').config();
require('./services/db.service').connect();
const events = require('events');
const BotService = require('./services/bot.service');
const DbService = require('./services/db.service');
const Webhook = require('./webhook');

const messageBus = new events.EventEmitter();

Webhook.start(messageBus);

messageBus.on('webhook-started', (port) => {
    console.log(`Webhook listening on port ${port}!`);
    BotService.webHookInit(messageBus);
});

messageBus.on('webhook-registered', (webhookURL) => {
    console.log(`Webhook registrato su ${webhookURL}`);
});

messageBus.on('api-started', (port) => {
    console.log(`Api listening on port ${port}`);
});

messageBus.on('message-arrived', async ({message}) => {
    const chatId = message.chat.id;
    const lastCommand = await DbService.getLastCommandRequested(chatId);
    if (!lastCommand) {
        return BotService.sayHello(message); // TODO messaggio migliore
    }
    switch (lastCommand.command) {
        case 'generateCode:paymentCode':
            return BotService.generateCode(chatId, message.chat.first_name , message, message.chat.username);
        case 'renewCode:paymentCode':
            return BotService.renewCodeVerifyPaymentCode(chatId, message);
        case 'associateCode:code':
            return BotService.associateCodeVerifyCode(chatId, message);
        default:
            return BotService.sayHello(message); // TODO messaggio migliore
    }
});

messageBus.on('command-arrived', ({message, command}) => {
    switch (command.command) {
        case '/start':
            return BotService.startCommand(message);
        case '/help':
            return BotService.helpCommand(message);
    //   case '/attiva_abbonamento':
    //       return BotService.generateCodeCommand(message);
        case '/rinnova_abbonamento':
            return BotService.renewCodeCommand(message);
        case '/controlla_account':
            return BotService.checkAccountCommand(message);
        case '/raddoppio_del_giorno':
            return BotService.registerToDailyDoublingCommand(message);
        case '/rimuovi_raddoppio_del_giorno':
            return BotService.deregisterToDailyDoublingCommand(message);
        case '/richiedi_codice_di_prova':
            return BotService.trialCodeCommand(message);
        case '/associa_codice':
            return BotService.associateCode(message);
        default:
            return BotService.commandNotFound(message);
    }
});

messageBus.on('callbackQuery-arrived', ({from, data}) => {
    switch (data) {
        case 'attiva_abbonamento':
            return BotService.helpGenerateCode(from);
        case 'rinnova_abbonamento':
            return BotService.helpRenewCode(from);
        case 'controlla_account':
            return BotService.helpCheckAccount(from);
        case 'raddoppio_del_giorno':
            return BotService.helpRegisterToDailyDoubling(from);
        case 'rimuovi_raddoppio_del_giorno':
            return BotService.helpDeregisterToDailyDoubling(from);
        case 'richiedi_codice_di_prova':
            return BotService.helpTrialCode(from);
        case 'associa_codice':
            return BotService.helpAssociateCode(from);
    }
});

messageBus.on('send-message', async (message) => {
    try {
        await BotService.sendMessage(message);
    } catch (e) {
        console.error(e);
    }
});
