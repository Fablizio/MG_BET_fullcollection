import { InlineKeyboard } from 'grammy';

// Menu principale in stile "moderno" (inline keyboard)
export const mainInlineMenu = new InlineKeyboard()
    .text('🎟 Codice di prova', 'trial')
    .row()
    .text('ℹ️ Info account', 'info')
    .row()
    .text('💳 Rinnova abbonamento', 'renew')
    .row()
    .text('🖼️ Screenshot pagamento', 'payment')
    .row()
    .text('❓ Aiuto', 'help');

// Conferma/annulla (può rimanere com’è)
export const confirmMenu = new InlineKeyboard()
    .text('✅ Conferma', 'confirm')
    .text('❌ Annulla', 'cancel');
