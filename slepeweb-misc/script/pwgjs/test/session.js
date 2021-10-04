const dash = require('lodash')
const sessionModule = require('../server/session')
const sessionService = sessionModule.sessionService
const Session = sessionModule.Session

var timeout = 2 * 60 * 1000
var sesh = new Session({username: 'george'}, 'askjdhk', timeout)

console.log('Adding session for user george ...')
sessionService.save(sesh)
console.log('Pool contains ', sessionService.countSessions(), ' sessions')
sesh = sessionService.find('george')
console.log('Found user ', sesh.user.username)

setTimeout(() => {
	console.log('Adding session for user adam ...')
	sessionService.save(new Session({username: 'adam'}, 'qwiueyb', timeout))
	console.log('Pool contains ', sessionService.countSessions(), ' sessions')
}, 10000)

setTimeout(() => {
	console.log('Testing secure access ...')
	sessionService.secure('george', (s) => {
		console.log(`Secure function executing with session set to [${s.user.username}]`)
	})
}, 20000)

/*
// Testing session timeout
var loop = setInterval(() => {
	var list = sessionService.list()
	if (list.length > 0) {
		dash.forEach(list, (s) => {
			console.log('Time remaining for user ', 
				s.user.username, ': ', s.remainingPercent(), '%')
		})
	}
	else {
		clearInterval(loop)
	}
}, 20000)
*/