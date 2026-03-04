const { MongoClient } = require('mongodb');
const DateUtility = require('../utility/date.utilty');

let db;

module.exports = {
    connect() {
        MongoClient.connect(process.env.MONGOURL, { poolSize: 10, useNewUrlParser: true },
            (err, client) => {
                if (err) {
                    console.error("Connection failed to database");
                    process.exit(1);
                }
                console.log("Connected successfully to database");
                db = client.db()
            })
    },
    setCommandRequested(chatId, command) {
        return db.collection("chats").update(
            { chatId },
            {
                chatId,
                command,
                date: new Date()
            },
            { upsert: true }
        );
    },
    removeCommandRequested(chatId) {
        return db.collection("chats").deleteOne({ chatId });
    },
    getLastCommandRequested(chatId) {
        return db.collection("chats").findOne({ chatId });
    },
    registerToGenerateCode(chatId, photo, username) {
        return db.collection("generateCode").insertOne(
            {
                chatId,
                photo,
                username,
                date: new Date()
            }
        );
    },
    findGenerateCodeRequest(chatId) {
        return db.collection("generateCode").findOne(
            {
                chatId
            }
        );
    },
    removeGenerateCodeRequest(_id) {
        return db.collection("generateCode").deleteOne({ _id });
    },
    registerToRenewCode(chatId, photo) {
        return db.collection("renewCode").insertOne(
            {
                chatId,
                photo,
                date: new Date()
            }
        );
    },
    findRenewCodeRequest(chatId) {
        return db.collection("renewCode").findOne(
            {
                chatId

            }
        );
    },
    removeRenewCodeRequest(_id) {
        return db.collection("renewCode").deleteOne({ _id });
    },
    registerToDailyDoubling(chatId) {
        return db.collection("dailyDoubling").update(
            { chatId },
            {
                chatId,
                date: new Date()
            },
            { upsert: true }
        );
    },
    findRegisteredToDailyDoubling() {
        return db.collection("dailyDoubling").find({}).toArray();
    },
    deregisterToDailyDoubling(chatId) {
        return db.collection("dailyDoubling").deleteOne({ chatId });
    },
    saveDailyDoubling(type, matches) {
        const now = new Date();
        return db.collection("lastDailyDoubling").insert(
            {
                type,
                matches,
                day: DateUtility.format(now),
                date: now
            }
        );
    },
    getLastDailyDoubling() {
        const now = new Date();
        return db.collection("lastDailyDoubling")
            .find({ day: DateUtility.format(now) })
            .toArray();
    }
};
