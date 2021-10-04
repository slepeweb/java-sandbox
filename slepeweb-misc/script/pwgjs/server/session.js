const dash = require('lodash')

class Session {
	static defaultTimeout = 10 * 60 * 1000 // 10 minutes
	
	constructor(u, k, t) {
		this.user = u
		this.key = k
		this.start = new Date().getTime()
		this.timeout = t ? t : Session.defaultTimeout
	}
	
	isLive() {
		var t = new Date().getTime()
		return t - this.start < this.timeout
	}
	
	// Session time remaining as a percentage of timeout period
	remainingPercent() {
		var now = new Date().getTime()
		return dash.floor(((this.timeout - now + this.start)/this.timeout) * 100) 
	}
}

class SessionService {
	constructor() {
		this.pool = []
	}
	
	save(s) {
		var sesh = this.find(s.user.username)
		
		if (! sesh) {		
			this.pool.push(s)
			
			var service = this
			setTimeout(function() {
				service.remove(s.user.username)
			}, s.timeout)
		}
		else {
			sesh.start = new Date().getTime()
		}
	}
	
	remove(name) {
		var s = dash.remove(this.pool, function(s) {
			return s.user.username == name
		})
		
		if (s) {
			console.log('Removed session from pool: ', name)
			return true
		}
		
		return false
	}
	
	find(name) {
		return dash.find(this.pool, function(s) {
			return s.user.username == name
		})
	}
	
	list() {
		return this.pool
	}
	
	isLive(name) {
		var s = this.find(name)
		return s && s.isLive()
	}
	
	countSessions() {
		return this.pool.length
	}
	
	secure(name, fn, args) {
		var sesh = this.find(name)
		if (sesh && sesh.isLive()) {
			fn(sesh, args)
		}
		else {
			throw('Not authorised!')
		}
	}
	
	defaultTimeout() {
		return Session.defaultTimeout
	}
}

exports.sessionService = new SessionService()
exports.Session = Session
