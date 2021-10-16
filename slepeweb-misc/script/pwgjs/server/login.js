const moment = require('moment')
const userdb = require('./userdb')
const Mailer = require('../slepeweb-modules/mailer')
const emailer = new Mailer({
	service: 'gmail',
	user: 'george.buttigieg56',
	password: 'g1gaL0ftgma15L',
	from: 'george.buttigieg56',
	to: 'george@buttigieg.org.uk',
	subject: 'Password Generator',
})

const Cryptor = require('./crypt')
const cryptor = new Cryptor()

const sc = require('path').basename(__filename)
const log = require('../slepeweb-modules/logger').instance

const sessionModule = require('./session')
const sessionService = sessionModule.sessionService

class LoginService {
	constructor() {
		this.monitor = new FailedLoginMonitor()
	}
	
	respond(message, isError = false) {
		return {
			msg: message, 
			err: isError
		}
	}

	notAuthorised(msg = 'Not Authorised!') {
		respond(msg, true)
	}
	
	login(formdata) {
		return new Promise((resolve, reject) => {
			var message = null
			var success = false
			var response = {msg: '', err: false}
	
			if (this.monitor.isUnderAttack()) {
				reject('Website down')
				return
			}
		
			if (! this.isValidPasswordFormat(formdata)) {
				log.warn(sc, message = `Failed login attempt - incorrect password construction [${formdata.username}]`)
				this.doMonitor(success, formdata)
				emailer.send(message)
				reject('Invalid user credentials (C)')
				return
			}
			
			userdb.findByName(formdata.username).then(
				(u) => {
					if (u) {
						// Allow user to login if no password set in the database.
						// (This is the case for the temp user)
						if (! u.password || cryptor.compare(formdata.password, u.password)) {
							log.info(sc, message = `User ${formdata.username} logged in`)
							if (! formdata.noemail) {
								emailer.send(message, {to: u.email})
							}
							
							var timeout = null
							if (formdata.noemail) {
								timeout = 3 * sessionService.defaultTimeout
							}
							
							sessionService.save(new sessionModule.Session(u, formdata.key, timeout))
					    	resolve(`User ${u.username} logged-in`)
				    	}
						else {
							log.warn(sc, message = `Failed login attempt - password mis-match [${formdata.username}]`)
							emailer.send(message)
							emailer.send(message, {to: u.email})
							reject('Invalid user credentials (A)')
						}
					}
					else {
						log.warn(sc, message = `Failed login attempt - no such user [${formdata.username}]`)
						emailer.send(message)
						reject('Invalid user credentials (B)')
					}
					
					// TODO: review monitor functionality
					//this.doMonitor(! response.err, formdata)
			}).catch(
				(err) => {
					log.error(sc, err)
					reject('User lookup error')
					//this.doMonitor(! response.err, formdata)
			})
		})
	}
	
	isValidPasswordFormat(u) {
		u.noemail = false
		
		if (u.password) {
			var matcher = u.password.match(/^(\d{2})(\d{2})(.*)$/)
			if (matcher) {
				var now = moment()
				var minutes = parseInt(matcher[1])
				var hours = parseInt(matcher[2])
				u.password = matcher[3]
				
				if (hours == now.hours() && Math.abs(minutes - now.minutes()) <= 2) {
					// Passed the first lock. Now check for noemail flag				
					var cursor = u.password.indexOf('^')
					u.noemail = cursor > -1
					
					if (cursor > -1) {
						u.password = u.password.substring(0, cursor) + u.password.substring(cursor + 1)
					}
					
					return true
				}
			}
		}
		
		return false
	}
	
	doMonitor(success, formdata) {
		if (! success) {
			this.monitor.add(formdata)
			
			if (this.monitor.isUnderAttack()) {
				// Send an email notification to the administrator
				log.fatal(sc, '*** Too many failed logins - server shutting down ***')
			}
		}
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

module.exports = new LoginService()