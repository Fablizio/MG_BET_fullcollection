import type { Bot } from 'grammy';
import { mainInlineMenu } from './keyboards.js';
import { ApiService } from '../services/api.service.js';
import { BotService } from '../services/bot.service.js';

export function registerCommands(bot: Bot) {
    // /start
    bot.command('start', async (ctx) => {
       // await DbService.saveUser({
       //     chatId: ctx.chat!.id,
       //     firstName: ctx.from?.first_name,
       //     username: ctx.from?.username,
       // });

        const welcomeMessage =
            `Ciao ${ctx.from?.first_name ?? 'Hello Better'}!\n` +
            `Benvenuto nel bot. Usa il menu qui sotto per iniziare.\n\n` +
            `⚠️ *Disclaimer*\n` +
            `Le informazioni fornite da questo bot hanno esclusivamente scopo informativo e statistico.\n` +
            `Non costituiscono in alcun modo un invito al gioco, alla scommessa o alla partecipazione ad attività di gioco d’azzardo.\n` +
            `Nessuna garanzia è offerta circa l’esattezza o l’esito delle previsioni. Gioca responsabilmente.`;

        await ctx.reply(welcomeMessage, {
            // ⬇️ QUI USIAMO LA NUOVA INLINE KEYBOARD
            reply_markup: mainInlineMenu,
            parse_mode: 'Markdown',
        });
    });

    // ============================
    // ℹ️ Info account (testo + inline)
    // ============================

    // Reply keyboard (messaggio "ℹ️ Info account")
    bot.hears('ℹ️ Info account', async (ctx) => {
        const telegramId = String(ctx.chat?.id);

        const account = await ApiService.getAccountInfo(telegramId);

        if (!account) {
            await ctx.reply('⚠️ Non sono riuscito a recuperare le informazioni del tuo account. Riprova più tardi.');
            return;
        }

        let expirationFormatted = account.expiration;
        if (account.expiration && account.expiration.includes('-')) {
            const [year, month, day] = account.expiration.split('-');
            expirationFormatted = `${day}/${month}/${year}`;
        }

        const messageLines = [
            'ℹ️ <b>Info account</b>',
            '',
            `👤 Nickname: <b>${account.nickname ?? '-'}</b>`,
            `🔑 Codice: <b>${account.code ?? '-'}</b>`,
            `📅 Scadenza: <b>${expirationFormatted ?? '-'}</b>`,
            `💎 Codice amico: <b>${account.friendCode ?? '-'}</b>`,
        ];

        await ctx.reply(messageLines.join('\n'), { parse_mode: 'HTML' });
    });

    // Inline keyboard (callback "info")
    bot.callbackQuery('info', async (ctx) => {
        await ctx.answerCallbackQuery();

        const telegramId = String(ctx.chat?.id);

        const account = await ApiService.getAccountInfo(telegramId);

        if (!account) {
            await ctx.reply('⚠️ Non sono riuscito a recuperare le informazioni del tuo account. Riprova più tardi.');
            return;
        }

        let expirationFormatted = account.expiration;
        if (account.expiration && account.expiration.includes('-')) {
            const [year, month, day] = account.expiration.split('-');
            expirationFormatted = `${day}/${month}/${year}`;
        }

        const messageLines = [
            'ℹ️ <b>Info account</b>',
            '',
            `👤 Nickname: <b>${account.nickname ?? '-'}</b>`,
            `🔑 Codice: <b>${account.code ?? '-'}</b>`,
            `📅 Scadenza: <b>${expirationFormatted ?? '-'}</b>`,
        ];

        await ctx.reply(messageLines.join('\n'), { parse_mode: 'HTML' });
    });

    // ============================
    // ❓ Aiuto
    // ============================

    bot.hears('❓ Aiuto', async (ctx) => {
        const help = [
            '<b>🤖 Guida rapida</b>',
            '• "🎟 Richiedi codice di prova" — invia la richiesta al servizio backend.',
            '  🔑 Il codice del giorno è valido per <b>3 giorni</b> e consente l’accesso a <b>tutte le funzionalità. Riceverai in automatico la notifiche dei raddoppi.</b>.',
            '',
            '📱 <b>Installazione dell’app:</b>',
            '1️⃣ Apri il link <a href="https://www.mgbet.it/mgbet-app/">MGBET APP</a> direttamente dal <b>telefono</b>.',
            '2️⃣ Vai nelle <b>impostazioni del browser</b> e seleziona <b>Aggiungi a schermata Home</b>.',
            '   Se non trovi questa voce, tocca <b>Condividi</b> e poi <b>Aggiungi a schermata Home</b>.',
        ].join('\n');

        await ctx.reply(help, { parse_mode: 'HTML' });
    });

    bot.callbackQuery('help', async (ctx) => {
        await ctx.answerCallbackQuery();

        const help = [
            '<b>🤖 Guida rapida</b>',
            '• "🎟 Richiedi codice di prova" — invia la richiesta al servizio backend.',
            '  🔑 Il codice del giorno è valido per <b>3 giorni</b> e consente l’accesso a <b>tutte le funzionalità. Riceverai in automatico la notifiche dei raddoppi.</b>.',
            '',
            '📱 <b>Installazione dell’app:</b>',
            '1️⃣ Apri il link <a href="https://www.mgbet.it/mgbet-app/">MGBET APP</a> direttamente dal <b>telefono</b>.',
            '2️⃣ Vai nelle <b>impostazioni del browser</b> e seleziona <b>Aggiungi a schermata Home</b>.',
            '   Se non trovi questa voce, tocca <b>Condividi</b> e poi <b>Aggiungi a schermata Home</b>.',
        ].join('\n');

        await ctx.reply(help, { parse_mode: 'HTML' });
    });

    // ============================
    // 🎟 Richiedi codice di prova
    // ============================

    bot.hears('🎟 Richiedi codice di prova', async (ctx) => {
        try {
            const payload = await ApiService.requestTrialCode(
                ctx.from!.id.toString(),
                ctx.from?.first_name,
                ctx.from?.username,
            );

            //await DbService.saveGenerateCodeRequest(ctx.chat!.id, ctx.from?.id?.toString() ?? '');

            if (payload?.code) {
                const rows = [
                    '🎟 <b>Codice di prova generato con successo!</b>',
                    '',
                    `Codice: <code>${payload.code}</code>`,
                    payload.expiration ? `Scadenza: ${payload.expiration}` : '',
                    '',
                    '✅ Puoi ora usufruire di <b>tutti i servizi</b> cliccando qui:',
                    '<a href="https://www.mgbet.it/mgbet-app/">MGBET APP</a>',
                    '',
                    '📱 Per aggiungere l’app alla schermata Home:',
                    '1️⃣ Apri il link direttamente dal <b>telefono</b>;',
                    '2️⃣ Vai nelle <b>impostazioni del browser</b> e scegli <b>Aggiungi a schermata Home</b>;',
                    '   Se non la trovi, seleziona <b>Condividi</b> → <b>Aggiungi a schermata Home</b>.',
                    '',
                    '🔔 Una volta aggiunta, riceverai automaticamente le notifiche dei <b>raddoppi</b> qui su telegram.',
                    '',
                    '💬 Unisciti anche alla nostra grande community Discord! Qui possiamo scambiare idee, fare due chiacchiere e molto altro:',
                    '<a href="https://mee6.xyz/en/i/VZK3sEHlMX">Entra nel Discord</a>',
                ];
                await ctx.reply(rows.join('\n'), { parse_mode: 'HTML' });
            } else if (payload?.ok) {
                await ctx.reply('Richiesta inviata ✅ Ti invieremo il codice appena pronto.');
            } else {
                await ctx.reply('Ha già richiesto un codice di prova.');
            }
        } catch (e) {
            await ctx.reply('Ha già richiesto un codice di prova.');
        }
    });

    bot.callbackQuery('trial', async (ctx) => {
        await ctx.answerCallbackQuery();

        try {
            const payload = await ApiService.requestTrialCode(
                ctx.from!.id.toString(),
                ctx.from?.first_name,
                ctx.from?.username,
            );

            //await DbService.saveGenerateCodeRequest(ctx.chat!.id, ctx.from?.id?.toString() ?? '');

            if (payload?.code) {
                const rows = [
                    '🎟 <b>Codice di prova generato con successo!</b>',
                    '',
                    `Codice: <code>${payload.code}</code>`,
                    payload.expiration ? `Scadenza: ${payload.expiration}` : '',
                    '',
                    '✅ Puoi ora usufruire di <b>tutti i servizi</b> cliccando qui:',
                    '<a href="https://www.mgbet.it/mgbet-app/">MGBET APP</a>',
                    '',
                    '📱 Per aggiungere l’app alla schermata Home:',
                    '1️⃣ Apri il link direttamente dal <b>telefono</b>;',
                    '2️⃣ Vai nelle <b>impostazioni del browser</b> e scegli <b>Aggiungi a schermata Home</b>;',
                    '   Se non la trovi, seleziona <b>Condividi</b> → <b>Aggiungi a schermata Home</b>.',
                    '',
                    '🔔 Una volta aggiunta, riceverai automaticamente le notifiche dei <b>raddoppi</b> qui su telegram.',
                    '',
                    '💬 Unisciti anche alla nostra grande community Discord! Qui possiamo scambiare idee, fare due chiacchiere e molto altro:',
                    '<a href="https://mee6.xyz/en/i/VZK3sEHlMX">Entra nel Discord</a>',
                ];
                await ctx.reply(rows.join('\n'), { parse_mode: 'HTML' });
            } else if (payload?.ok) {
                await ctx.reply('Richiesta inviata ✅ Ti invieremo il codice appena pronto.');
            } else {
                await ctx.reply('Ha già richiesto un codice di prova.');
            }
        } catch (e) {
            await ctx.reply('Ha già richiesto un codice di prova.');
        }
    });

    // ============================
    // 💳 Rinnova abbonamento
    // ============================

    bot.hears('💳 Rinnova abbonamento', async (ctx) => {
        await ctx.reply('🔗 <b>Rinnova abbonamento</b>: <a href="https://app.suby.fi/sub/9Q5TA27GSAU4VN0RTIOF">Clicca qui</a>', { parse_mode: 'HTML' });
    });

    bot.callbackQuery('renew', async (ctx) => {
        await ctx.answerCallbackQuery();
        await ctx.reply('🔗 <b>Rinnova abbonamento</b>: <a href="https://app.suby.fi/sub/9Q5TA27GSAU4VN0RTIOF">Clicca qui</a>', { parse_mode: 'HTML' });
    });

    // ============================
    // 🖼️ Invia screenshot del pagamento
    // ============================

    bot.hears('🖼️ Invia screenshot del pagamento', async (ctx) => {
        const msg = [
            '📸 <b>Invia qui lo screenshot del pagamento</b>.',
            '',
            '👉 Per favore invia lo screenshot come <b>Foto/Immagine</b> (va bene anche come file immagine).',
        ].join('\n');
        await ctx.reply(msg, { parse_mode: 'HTML' });
    });

    bot.callbackQuery('payment', async (ctx) => {
        await ctx.answerCallbackQuery();

        const msg = [
            '📸 <b>Invia qui lo screenshot del pagamento</b>.',
            '',
            '👉 Per favore invia lo screenshot come <b>Foto/Immagine</b> (va bene anche come file immagine).',
        ].join('\n');
        await ctx.reply(msg, { parse_mode: 'HTML' });
    });

    // ============================
    // Ricezione FOTO
    // ============================
    bot.on('message:photo', async (ctx) => {
        try {
            const sizes = ctx.message.photo;
            const best = sizes[sizes.length - 1];
            const fileUrl = await BotService.getFileUrl(best.file_id);

            await ApiService.sendPaymentScreenshot(
                ctx.from!.id.toString(),
                fileUrl
            );

            await ctx.reply('✅ Screenshot ricevuto! Stiamo verificando il pagamento e ti aggiorniamo a breve.');
        } catch (err) {
            await ctx.reply('⚠️ Errore durante l’invio dello screenshot. Riprova tra poco.');
        }
    });

    // ============================
    // Ricezione DOCUMENTO immagine
    // ============================
    bot.on('message:document', async (ctx) => {
        try {
            const doc = ctx.message.document;
            if (doc.mime_type && doc.mime_type.startsWith('image/')) {
                const fileUrl = await BotService.getFileUrl(doc.file_id);

                await ApiService.sendPaymentScreenshot(
                    ctx.from!.id.toString(),
                    fileUrl
                );

                await ctx.reply('✅ Screenshot (come file) ricevuto! Stiamo verificando il pagamento.');
            } else {
                await ctx.reply('ℹ️ Invia per favore uno screenshot come <b>foto</b> o come <b>file immagine</b>.', { parse_mode: 'HTML' });
            }
        } catch (err) {
            await ctx.reply('⚠️ Errore durante l’invio dello screenshot. Riprova tra poco.');
        }
    });
}
