const Datastore = require('nedb')

/*
	This might not be a good idea ... storing user details
	propbably requires user permission. Anyway, this class is
	not currently being used.
*/

const trim = (s) => {
	var notNull = s == null ? '' : s
	if (typeof notNull == 'string') {
		return notNull.trim()
	}
	return notNull
}


const preparePartyId = (s) => {
	// Lowercase and remove spaces
	return s.trim().toLowerCase().replace(/\s/, '')
}

class PwdDatabase {
	constructor() {
		this.ds = new Datastore({
			filename: './data.db',
			autoload: true
		})

		this.ds.ensureIndex({
			fieldName: 'key',
			unique: true
		})

		this.ds.ensureIndex({
			fieldName: 'email',
		})
	}

	// Function to define a unique key for a db record
	toKey(email) {
		return `${new Date().getTime()}-${email}`
	}

	// Function to upload data from the specified spreadsheetowner
	insert(doc) {
		// Beware!! 'this' does NOT represent an instance of PwdDatabase object
		// when you are in the body of a callback function.
		var db = this

		var promise = new Promise((resolve, reject) => {
			doc.key = this.toKey(doc.email)
			db.ds.insert(doc, (err, n) => {
				if (! err) {
					resolve(`Db record successfully inserted [${doc.key}]`)
				}
				else {
					reject(`Problem inserting db record [${doc.key}]`)
				}
			})
		})

		return promise
	}

	find(email) {
		var db = this
		return new Promise((resolve, reject) => {
			db.ds.find({email: email}).sort({key: 1}).exec((err, docs) => {
				err ? reject(err) : resolve(docs)
			})
		})
	}

	findAll(owner) {
		return new Promise((resolve, reject) => {
			this.ds.find({}).sort({key: 1, email: 1}).exec((err, docs) => {
				err ? reject(err) : resolve(docs)
			})
		})
	}

	remove(email) {
		return new Promise((resolve, reject) => {
			this.ds.remove({email: email}, {multi: true}, function(err, num) {
				err ? reject(err) : resolve(`Removed ${num} entries from the password database for user ${email}`)
			})
		})
	}
}

const db = new PwdDatabase()
module.exports = db
