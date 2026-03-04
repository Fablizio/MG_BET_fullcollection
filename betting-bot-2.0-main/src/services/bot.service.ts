import axios from 'axios';
import { env } from '../env.js';

const baseURL = `https://api.telegram.org/bot${env.TELEGRAM_TOKEN}`;
const fileURL = `https://api.telegram.org/file/bot${env.TELEGRAM_TOKEN}`;

export const BotService = {
  async sendMessage(data: { chat_id: number; text: string; parse_mode?: 'Markdown' | 'HTML'; reply_markup?: any }) {
    return axios.post(`${baseURL}/sendMessage`, data, { headers: { 'content-type': 'application/json' } });
  },
  async getFileUrl(file_id: string) {
    const { data } = await axios.get(`${baseURL}/getFile`, { params: { file_id } });
    return `${fileURL}/${data.result.file_path}`;
  }
};
