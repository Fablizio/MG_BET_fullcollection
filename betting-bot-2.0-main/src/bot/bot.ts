import { Bot, webhookCallback } from 'grammy';
import { env } from '../env.js';
import { registerCommands } from './commands.js';
import { registerMiddlewares } from './middlewares.js';

export const bot = new Bot(env.TELEGRAM_TOKEN);
registerMiddlewares(bot);
registerCommands(bot);

export const botWebhook = webhookCallback(bot, 'express');

export async function ensureWebhook(appBaseUrl: string) {
  await bot.api.setWebhook(`${appBaseUrl}/webhook`);
}
