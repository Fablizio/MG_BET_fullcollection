import { env } from './env.js';
import { connectDB } from './services/db.service.js';
import { startServer } from './webhook.js';

async function main() {
  await connectDB();
  await startServer();
  // eslint-disable-next-line no-console
  console.log(`✅ Webhook attivo su ${env.PUBLIC_URL}/webhook — porta ${env.PORT}`);
}

main().catch((err) => {
  // eslint-disable-next-line no-console
  console.error(err);
  process.exit(1);
});
