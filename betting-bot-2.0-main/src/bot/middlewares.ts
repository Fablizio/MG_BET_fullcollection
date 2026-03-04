import type { Bot } from 'grammy';
import pino from 'pino';

const logger = pino({ level: process.env.LOG_LEVEL ?? 'info' });

export function registerMiddlewares(bot: Bot) {
  bot.use(async (ctx, next) => {
    logger.info({ updateId: ctx.update.update_id, from: ctx.from?.id, type: Object.keys(ctx.update)[1] }, 'update');
    try {
      await next();
    } catch (err) {
      logger.error({ err }, 'bot error');
      if (ctx.chat?.id) {
        await ctx.api.sendMessage(ctx.chat.id, '😵 Si è verificato un errore inatteso. Riprova tra poco.');
      }
    }
  });
}
