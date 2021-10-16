const sessionService = require('./session').sessionService
const userdb = require('./userdb')
const pwdb = require('./pwdb')
const Cryptor = require('./crypt')
const cryptor = new Cryptor()

const sc = require('path').basename(__filename)
const log = require('../slepeweb-modules/logger.js').instance

class UserService {
	constructor() {
	}
	
	/*
	 * The user upsert form provides the following data:
	 * - username (mandatory)
	 * - password
	 * - email address
	 * - defaultlogin
	 * - admin
	 * - mode ('add' or 'update')
	 * 
	 * 'add' requires all these parameters.
	 * 'update' only requires username, and one or more of the other parameters.
	 * 'remove' only requires username.
	 *
	 * Only administrators can add users UNLESS it's the first user, who automatically
	 * becomes an administrator.
	 *
	 * General users can update their own properties (except admin status), but only
	 * an administrator can update another user's properties.
	 *
	 * The 'admin' parameter can only be used by administrators.
	 */

	upsert(formMode, formData, actor) {
		return new Promise((resolve, reject) => {
			/*
			 * params is the processed form data
			 */
			var params = {
				username: formData.username, 
				email: formData.email, 
				defaultlogin: formData.defaultlogin,
				// formData.admin is either a) '', b) 'yes' or c) 'no'
				admin: formData.admin,
			}
			
			// Don't attempt to update password if form input not populated
			if (formMode == 'add' || formData.password) {
				params.password = formData.password
			} 
		
			if (formMode == 'add') {
				// Only an administrator can assign admin status to a new user.
				// The first user added to an empty db is automatically assigned
				// admin status.
				// When formMode is 'add', params.admin needs to be a boolean value
				params.admin = userdb.empty || (actor && actor.admin && formData.admin == 'yes')
				
				if (userdb.empty || (actor && actor.admin)) {
					userdb.add(params).then((res) => {
						resolve(res)
					}).catch((err) => {
						reject(err)
					})
				}
				else if (! (actor && actor.admin)) {
					reject('Only an admin user can add other users!')
				}
				else {
					reject('Insufficient authority!')
				}
			}
			else if (formMode == 'update') {
				// Only an administrator can assign admin status to another user.
				if (actor.admin || actor.username == params.username) {
					userdb.update(params).then((res) => {
						resolve(res)
					}).catch((err) => {
						reject(err)
					})
				}
				else {
					reject('Insufficient authority!')
				}
			}
		})
	}
	
	remove(actor, subject) {
		/*
		 * Only an admin user can delete another user profile from the database
		 */
		return new Promise((resolve, reject) => {
			if (actor && actor.admin) {
				userdb.remove(subject).then((msg) => {
					// Also delete associated data
					pwdb.remove(subject).then((s) => {
						// TODO? Nothing interesting to feed back
					}).catch((e) => {
						// TODO? No big deal if op fails
					})
					
					resolve(msg)
				}).catch((msg) => {
					reject(msg)
				})
			}
			else {
				reject('Insufficient authority!')
			}
		})
	}
	
	list(u) {
		return new Promise((resolve, reject) => {
			userdb.findAll(u).then((list) => {
				resolve(list)
			}).catch((msg) => {
				reject('Failed to identify users')
			})
		})
	}
}

class Response {
	constructor(m, e, load) {
		this.payload = load
		this.msg = m
		this.err = e
	}
}

class FailedLoginMonitor {
	constructor() {
		this.heap = []
		this.timeout = 3 * 60 * 1000
		this.alertThreshold = 5
		this.attackThreshold = 20
	}
	
	add(u) {
		this.removeStale()
		u.when = new Date()
		this.heap.push(u)
		var len = this.heap.length
		
		if (len > 0 && len % this.alertThreshold == 0) {
			// There have been AT LEAST five failed login attempts in the last 2 minutes.
			// Send an email notification to the administrator
			console.warn('*** Possible security breach in progress ***', this.heap)
		}
	}
	
	removeStale() {
		var now = new Date()
		var index = this.heap.length - 1
		
		while (index >= 0) {
			if ((now - this.heap[index].when) > this.timeout) {
				this.heap.splice(index, 1)
			} 
			
			index--
		}
	}
	
	list() {
		return this.heap
	}
	
	isUnderAttack() {
		return this.heap.length >= this.attackThreshold
	}
}

const monitor = new FailedLoginMonitor()

module.exports = new UserService()
