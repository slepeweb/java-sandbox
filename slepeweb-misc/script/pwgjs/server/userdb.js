const Datastore = require('nedb')
const Cryptor = require('./crypt')
const crypt = new Cryptor()
const sc = require('path').basename(__filename)
const log = require('../slepeweb-modules/logger').instance
const pwdb = require('./pwdb')
const Util = require('./util')

const notNull = (s) => {
	return s == null ? '' : s
}

class UserDatabase {
	constructor() {
		this.ds = new Datastore({
			filename: './db/user.db', 
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
	
	findAll(u) {
		return new Promise((resolve, reject) => {
			if (u.admin) {	
				this.ds.find({}).sort({username: 1}).exec((err, docs) => {
					err ? reject(err) : resolve(docs)
				})
			}
			else {
				var list = []
				list.push(u)
				resolve(list)
			}
		})
	}
	
	add(u) {
		return new Promise((resolve, reject) => {
			var msg		
			if (u.username) {
				this.findByName(u.username).then((user) => {	
					if (! user) {
						if (u.password) {
							u.password = crypt.encrypt(u.password)
						}
						
						this.ds.insert(u, function(err, newDoc) {
							if (! err) {
								log.info(sc, msg = `User ${newDoc.username} added`)
								resolve(Util.respond(newDoc, msg))						
							}
							else {
								log.warn(sc, msg = 'Failed to add user')
								reject(msg)				
							}
						})
					}
					else {
						log.warn(sc, msg = `User ${u.username} already exists`)
						reject(msg)				
					}
				}).catch((e) => {
					log.error(sc, msg = 'Database save error', e)
					reject(msg)						
				})
			}
			else {
				reject('Data error: missing user name')						
			}
		})
	}

	update(u) {
		return new Promise((resolve, reject) => {
			var changes = {}
			
			if (u.password) {
				changes.password = crypt.encrypt(u.password)
			}
			
			if (u.email) {
				changes.email = u.email
			}
			
			if (u.defaultlogin) {
				changes.defaultlogin = u.defaultlogin
			}
			
			if (u.admin != '') {
				// Time to change string value to boolean, before saving in db
				changes.admin = (u.admin == 'yes')
			}
		
			this.ds.update({username: u.username}, {$set: changes}, {}, (err) => {
				var msg
				if (err) {
					log.error(sc, msg = `Failed to update user ${u.username}`, err)
					reject(msg)
				}
				else {
					this.findByName(u.username).then((updated) => {
						resolve(Util.respond(updated, msg = `User record updated (${updated.username})`))
					}).catch((err) => {
						reject(err)
					})
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
	
	/*
	 * Deleting a user from the user db must also delete all corresponding
	 * records in the passwords db. *** TODO ***
	 */
	remove(name) {
		return new Promise((resolve, reject) => {
			this.ds.remove({username: name}, {}, function (err, numRemoved) {
				var msg = 'none'
			
				if (err || numRemoved == 0) {
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
