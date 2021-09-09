const Datastore = require('nedb')
const Cryptor = require('./crypt')
const crypt = new Cryptor()
const sc = require('path').basename(__filename)
const log = require('./slepeweb-modules/logger').instance

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
		
		this.count(this)
		
		// Give count callback some time to execute
		setTimeout(() => {
			log.info(sc, `Empty flag is ${this.empty}`)
		}, 1000)		
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
		return new Promise((resolve, reject) => {		
			var msg
			
			if (u.username) {
				this.findByName(u.username).then(
					(user) => {						
						if (! user) {
							if (u.password) {
								u.password = crypt.encrypt(u.password)
							}
							this.ds.insert(u)
							log.info(sc, msg = `User ${u.username} added`)
							resolve(msg)						
						}
						else {
							log.warn(sc, msg = `User ${u.username} already exists`)
							reject(msg)				
						}
					}).catch(
						(e) => {
							log.error(sc, msg = 'Database save error', e)
							reject(msg)						
						}
					)
			}
			else {
				reject('Data error: missing user name')						
			}
		})
	}

	update(u) {
		var changes = {}
		var msg
		
		if (u.password) {
			changes.password = crypt.encrypt(u.password)
		}
		
		if (u.email) {
			changes.email = u.email
		}
		
		if (u.defaultlogin) {
			changes.defaultlogin = u.defaultlogin
		}
		
		if (u.admin) {
			changes.admin = u.admin == 'true'
		}
		
		return new Promise((resolve, reject) => {
			this.ds.update({username: u.username}, {$set: changes}, {}, (err) => {
				if (err) {
					log.error(sc, msg = `Failed to update user ${u.username}`, err)
					reject(msg)
				}
				else {
					resolve(`User record updated (${u.username})`)
				}
			})
		})
	}
	
	count(db) {
		var msg
		this.ds.count({}, function (err, count) {
			if (err) {
				log.info(sc, msg = `Failed to count users in database; assume non-zero`)
				db.empty = false
			}
			else {
				log.info(sc, msg = `There are ${count} entries in the user database`)
				db.empty = (count == 0)
			}
		})
	}
	
	remove(name) {
		return new Promise((resolve, reject) => {
			this.ds.remove({username: name}, {}, function (err, numRemoved) {
				var msg = 'none'
			
				if (err) {
					reject(`Failed to remove user [${name}]`)
				}
				else {
			  		log.info(sc, msg = `User ${name} removed`)
			  		resolve(msg)
			  	}
			})
		})
	}
}

const db = new UserDatabase()
module.exports = db
