import 'dotenv/config';
import { z } from 'zod';

const schema = z.object({
  TELEGRAM_TOKEN: z.string().min(10),
  PUBLIC_URL: z.string().url(), // webhook base URL
  PORT: z.coerce.number().default(3000),
  MONGO_URL: z.string().min(5),
  API_URL: z.string().url(),
  SITE: z.string().url()
});

export const env = schema.parse(process.env);
