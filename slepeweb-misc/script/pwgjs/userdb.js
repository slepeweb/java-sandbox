const Datastore = require('nedb')
const Cryptor = require('./crypt')
const crypt = new Cryptor()
const sc = require('path').basename(__filename)
const log = require('./logger.js')

const notNull = (s) => {
	return s == null ? '' : s
}

class UserDatabase {
	constructor() {
		this.ds = new Datastore({
			filename: './user.db', 
			autoload: true 
		})
		
		this.ds.ensureIndex({
			fieldName: 'username',
			unique: true
		})
	}
	
	find(name) {
		return new Promise((resolve, reject) => {		
			this.ds.findOne({username: name}, (err, u) => {
				err ? reject(err) : resolve(u)
			})
		})
	}
	
	save(u) {
		if (u.username && u.password) {
			this.find(u.username).then(
				(user) => {
					if (! user) {
						u.password = crypt.encrypt(u.password)
						this.ds.insert(u)
						log.info(sc, `User ${u.username} added`)
					}
					else {
						log.warn(sc, `User ${u.username} already exists`)
					}
				}).catch(
					(e) => {
						log.error(sc, 'Database save error', e)
					}
				)
		}
	}
}

const db = new UserDatabase()
module.exports = db
