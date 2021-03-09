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
		
		this.tempUser = 'temp'
	}
	
	findByName(name) {
		return new Promise((resolve, reject) => {		
			this.ds.findOne({username: name}, (err, u) => {
				err ? reject(err) : resolve(u)
			})
		})
	}
	
	findById(id) {
		return new Promise((resolve, reject) => {		
			this.ds.findOne({_id: id}, (err, u) => {
				err ? reject(err) : resolve(u)
			})
		})
	}
	
	save(u) {
		if (u.username) {
			this.findByName(u.username).then(
				(user) => {
					if (! user) {
						if (u.password) {
							u.password = crypt.encrypt(u.password)
						}
						this.ds.insert(u)
						log.info(sc, `User ${u.username} added`)
						
						// Remove temporary user, if it's still there
						this.remove(this.tempUser)
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
	
	updateKey(id, key) {
		this.ds.update({_id: id}, {key: key}, {}, (err, numReplaced) => {
			log.error(sc, `Failed to store key for user ${id}`, err)
		})
	}
	
	count(callback, params) {
		this.ds.count({}, function (err, count) {
			log.info(sc, `There are ${count} entries in the user database`)
			if (callback && params) {
				callback(count, params.a, params.b)
			}
		})
	}
	
	remove(name) {
		var callback = (count, paramA, paramB) => {
			if (count > 1) {
				this.ds.remove({username: name}, {}, function (err, numRemoved) {
				  log.info(sc, `User ${name} removed`)
				})
			}
		}
		
		this.count(callback, {a: null, b: null})
	}
}

const db = new UserDatabase()
module.exports = db
