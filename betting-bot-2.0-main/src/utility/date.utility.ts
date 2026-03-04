import dayjs from 'dayjs';

export const DateUtility = {
  format(date: Date | string | number) {
    return dayjs(date).format('DD/MM/YYYY');
  },
  formatWithTime(date: Date | string | number) {
    return dayjs(date).format('DD/MM/YYYY HH:mm');
  }
};
