const express = require('express')
const router = express.Router()

const userdb = require('../userdb')
const email = require('../mailer')

const Cryptor = require('../crypt')
const cryptor = new Cryptor()

const sc = require('path').basename(__filename)
const log = require('../logger.js')

router.get('/login', (req, res) => {
    res.render('login', {title: 'Login', err: req.query.err, loggedIn: req.session.user})
})

router.get('/logout', (req, res) => {
	req.session.user = null
	res.redirect('/')
})

router.post('/login', (req, res) => {
	if (monitor.isUnderAttack()) {
    	res.render('noservice', {title: 'Website down'})
    	return
	}
	
	var success = false
	var message = null
	
	var formdata = {
		username: req.body.username,
		password: req.body.password,
		key: req.body.key,
		ip: req.ip,
	}
	
	if (formdata.username && formdata.key) {
		// Back door to avoid unnecessary emails
		checkBackDoor(formdata)
			
		userdb.findByName(formdata.username).then(
			(u) => {
				if (u) {
					// Allow user to login if no password set in the database.
					// (This is the case for the temp user)
					if (! u.password || cryptor.compare(formdata.password, u.password)) {
						log.info(sc, message = `User ${formdata.username} logged in`)
						if (! formdata.noemail) {
							email(message)
						}
						
				    	req.session.user = u
				    	u.key = formdata.key
				    	success = true
				    	res.redirect('/')
			    	}
					else {
						log.warn(sc, message = `Failed login attempt - password mis-match [${formdata.username}]`)
						email(message)
						res.redirect('/users/login?err=Invalid%20user%20credentials (A)')
					}
				}
				else {
					log.warn(sc, message = `Failed login attempt - no such user [${formdata.username}]`)
					email(message)
					res.redirect('/users/login?err=Invalid%20user%20credentials (B)')
				}
		}).catch(
			(err) => {
				log.error(sc, err)
				res.redirect('/users/login?err=User%20lookup%20error', err)
			})
	}
	else {
		log.error(sc, 'Incomplete login details')
		res.redirect('/users/login?err=Please%20complete%20all%20fields')
	}
	
	if (! success) {
		monitor.add(formdata)
		
		if (monitor.isUnderAttack) {
			// Send an email notification to the administrator
			log.fatal('*** Too many failed logins - server shutting down ***')
		}
	}
})

const checkBackDoor = (formdata) => {
	formdata.noemail = false
	
	if (formdata.password) {
		var cursor = formdata.password.indexOf('^')
		formdata.noemail = cursor > -1
		
		if (cursor > -1) {
			formdata.password = formdata.password.substring(0, cursor) + formdata.password.substring(cursor + 1)
		}
	}
}

router.get('/add', (req, res) => {
	var u = req.session.user
	if (u) {
		userdb.save({
			username: req.query.username, 
			password: req.query.password
		})
	}
	res.redirect('/')
})

router.get('/remove', (req, res) => {
	var u = req.session.user
	if (u) {
		userdb.remove(req.query.username)
	}
	res.redirect('/')
})

router.get('/whoami', (req, res) => {
	var u = req.session.user
	var obj = {}
	
	if (u) {
		obj.id = u._id
		obj.key = u.key
	}
	else {
		obj.id = 'none'
	}
	res.json(obj)
})

class FailedLoginMonitor {
	constructor() {
		this.heap = []
		this.timeout = 2 * 60 * 1000
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
			
			index--;
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

module.exports = router
