const express = require('express')
const router = express.Router()
const moment = require('moment')
const config = require('../config')

const userdb = require('../userdb')

const Mailer = require('../slepeweb-modules/mailer')
const emailer = new Mailer({
	service: 'gmail',
	user: 'george.buttigieg56',
	password: 'g1gaL0ftgma15L',
	from: 'george.buttigieg56',
	to: 'george@buttigieg.org.uk',
	subject: 'Password Generator',
})

const Cryptor = require('../crypt')
const cryptor = new Cryptor()

const sc = require('path').basename(__filename)
const log = require('../slepeweb-modules/logger.js').instance

const keys = {}

router.get('/login', (req, res) => {
    res.render('login', {title: 'Login', err: req.query.err, loggedIn: req.session.user})
})

router.get('/logout', (req, res) => {
	req.session.user = null
	res.redirect('/')
})

router.post('/login', (req, res) => {
	var message = null
	var success = false

	if (monitor.isUnderAttack()) {
    	res.render('noservice', {title: 'Website down'})
    	return
	}
	
	var formdata = {
		username: req.body.username,
		password: req.body.password,
		key: req.body.key,
		ip: req.ip,
	}
	
	if (! (formdata.username && formdata.key)) {
		log.warn(sc, 'Incomplete login details')
		doMonitor(success, formdata)
		res.redirect('/users/login?err=Please%20complete%20all%20fields')
		return
	}
	
	if (! isValidPassword(formdata)) {
		log.warn(sc, message = `Failed login attempt - incorrect password pattern [${req.body.password}]`)
		doMonitor(success, formdata)
		emailer.send(message)
		res.redirect('/users/login?err=Invalid%20user%20credentials (C)')
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
						emailer.send(message)
					}
					
			    	req.session.user = u
			    	var now = new Date()
			    	u.sessionStart = now.getTime()
			    	u.sessionTimeout = config.sessionTimeout
			    	
			    	if (u.password) {
			    		keys[u.password] = formdata.key
			    	}
			    	success = true
			    	res.redirect('/')
		    	}
				else {
					log.warn(sc, message = `Failed login attempt - password mis-match [${formdata.username}]`)
					emailer.send(message)
					res.redirect('/users/login?err=Invalid%20user%20credentials (A)')
				}
			}
			else {
				log.warn(sc, message = `Failed login attempt - no such user [${formdata.username}]`)
				emailer.send(message)
				res.redirect('/users/login?err=Invalid%20user%20credentials (B)')
			}
			
			doMonitor(success, formdata)
	}).catch(
		(err) => {
			log.error(sc, err)
			res.redirect('/users/login?err=User%20lookup%20error', err)
			doMonitor(success, formdata)
	})
	
})

const doMonitor = (success, formdata) => {
	if (! success) {
		monitor.add(formdata)
		
		if (monitor.isUnderAttack()) {
			// Send an email notification to the administrator
			log.fatal(sc, '*** Too many failed logins - server shutting down ***')
		}
	}
}

const isValidPassword = (formdata) => {
	formdata.noemail = false
	
	if (formdata.password) {
		var matcher = formdata.password.match(/^(\d{2})(\d{2})(.*)$/)
		if (matcher) {
			var now = moment()
			var minutes = parseInt(matcher[1])
			var hours = parseInt(matcher[2])
			formdata.password = matcher[3]
			
			if (hours == now.hours() && Math.abs(minutes - now.minutes()) <= 2) {
				// Passed the first lock. Now check for noemail flag				
				var cursor = formdata.password.indexOf('^')
				formdata.noemail = cursor > -1
				
				if (cursor > -1) {
					formdata.password = formdata.password.substring(0, cursor) + formdata.password.substring(cursor + 1)
				}
				
				return true
			}
		}
	}
	
	return false
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

router.get('/update', (req, res) => {
	var u = req.session.user
	if (u) {
		userdb.update({
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
	var iam = {}
	
	if (u) {
		iam.id = u._id
		iam.name = u.username
	}
	else {
		iam.id = 'none'
		iam.name = 'unknown'
	}
	res.json(iam)
})

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

exports.router = router
exports.keys = keys
