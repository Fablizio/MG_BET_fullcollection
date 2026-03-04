import { MongoClient, Db } from 'mongodb';
import { env } from '../env.js';
import { DateUtility } from '../utility/date.utility.js';

let db: Db;

export async function connectDB() {
  const client = new MongoClient(env.MONGO_URL, { maxPoolSize: 10 });
  await client.connect();
  db = client.db();
  return db;
}

export function getDB() {
  if (!db) throw new Error('DB not initialized');
  return db;
}

// === Porting delle funzioni esistenti (nomi conservati) ===
export const DbService = {
  async saveUser(user: { chatId: number; firstName?: string; username?: string }) {
    const now = new Date();
    await getDB().collection('users').updateOne(
      { chatId: user.chatId },
      { $set: { ...user, updatedAt: now }, $setOnInsert: { createdAt: now } },
      { upsert: true }
    );
  },
  async findGenerateCodeRequest(chatId: number) {
    return getDB().collection('generateCodeRequests').findOne({ chatId });
  },
  async saveGenerateCodeRequest(chatId: number, telegramSession: string) {
    const now = new Date();
    await getDB().collection('generateCodeRequests').updateOne(
      { chatId },
      { $set: { chatId, telegramSession, date: now, day: DateUtility.format(now) } },
      { upsert: true }
    );
  },
  async registerToDailyDoubling(chatId: number) {
    await getDB().collection('dailyDoublingSubscribers').updateOne(
      { chatId },
      { $set: { chatId, subscribed: true, date: new Date() } },
      { upsert: true }
    );
  },
  async deregisterToDailyDoubling(chatId: number) {
    await getDB().collection('dailyDoublingSubscribers').updateOne(
      { chatId },
      { $set: { subscribed: false, date: new Date() } }
    );
  },
  async saveLastDailyDoubling(payload: {
    type: string;
    matches: string[];
  }) {
    const now = new Date();
    await getDB().collection('lastDailyDoubling').insertOne({
      ...payload,
      day: DateUtility.format(now),
      date: now
    });
  },
  async getLastDailyDoubling() {
    const now = new Date();
    return getDB().collection('lastDailyDoubling').find({ day: DateUtility.format(now) }).toArray();
  }
};
