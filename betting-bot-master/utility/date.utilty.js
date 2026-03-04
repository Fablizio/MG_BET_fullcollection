const moment = require('moment');

module.exports = {
    format(date) {
        return moment(date).format('DD/MM/YYYY');
    },
    isInFuture(date) {
        return moment(date).isSameOrAfter(moment());
    },
    isToday(date) {
        return moment(date).isSame(moment(), 'day');
    }
};
