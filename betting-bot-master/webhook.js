const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

const api = require('./api');

const port = process.env.PORT || 3000;

function isCommand({entities}) {
    return entities && entities[0].type === 'bot_command';
}

function evaluateCommand({text, entities}) {
    console.log({text, entities});
    const entity = entities[0];
    const command = text.substr(entity.offset, entity.offset + entity.length);
    const args = text.substr(entity.offset + entity.length, text.length).split(' ');
    return {
        command,
        args: args.filter(a => a !== '')
    }
}

module.exports = {
    start(messageBus) {
        const app = express();
        app.use(bodyParser.json());
        app.use(cors());

        app.post('/webhook', (req, res) => {
            const message = req.body.message || req.body.edited_message;
            const callbackQuery = req.body.callback_query;
            if(callbackQuery) {
                console.log(`*** Nuovo callbackQuery da ${callbackQuery.from.first_name} ***`);
                messageBus.emit('callbackQuery-arrived', callbackQuery);
                return res.send("");
            }
            console.log(`*** Nuovo messaggio da ${message.chat.first_name} ***`);
            if (isCommand(message)) {
                messageBus.emit('command-arrived', {message, command: evaluateCommand(message)});
                return res.send("");
            }
            messageBus.emit('message-arrived', {message});
            return res.send("");
        });

        api.setMessageBus(messageBus);
        app.use('/api', api.router);

        app.listen(port, () => messageBus.emit('webhook-started', port));
    }
};
