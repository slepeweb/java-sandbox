const sc = require('path').basename(__filename)
const Logger = require('../slepeweb-modules/logger')
Logger.instance = new Logger('pwg', __dirname + '/../logs')
const log = Logger.instance
log.info(sc, '===========================================')

const userService = require('../server/users')
const userdb = require('../server/userdb')
const sessionModule = require('../server/session')
const sessionService = sessionModule.sessionService
const name = 'george'

userdb.findByName(name).then((u) => {
	if (!u) {
		userdb.add({
			username: 'george', 
			password: 'giga8yte', 
			admin: true, 
			email: 'george@buttigieg.org.uk', 
			defaultlogin: 'george@buttigieg.org.uk'
		}).then(() => {
			console.log('Added george')
		}).catch((err) => {
			console.log('Failed to add george')
		})
	}
	else {
		console.log(`Found user ${u.username} `, u)
		console.log('This user can see/edit the following list of users:')
		
		setTimeout(() => {
			userdb.findAll(u).then((list) => {
				list.forEach(function(value) {
					console.log(`${value.username}, ${value.email}, ${value.defaultlogin}, ${value.admin ? 'Yes' : 'No'}`)
				})
			}).catch((msg) => {
				console.log(msg)
			})
		}, 1000)
		
		var timeout = 0.5 * 60 * 1000
		var mysesh = new sessionModule.Session(u, 'askjdhk', timeout)
		sessionService.save(mysesh)

		setTimeout(() => {
			console.log('Try again checking session is live')
			userService.list(name).then((list) => {
				list.forEach(function(value) {
					console.log(`${value.username}, ${value.email}, ${value.defaultlogin}, ${value.admin ? 'Yes' : 'No'}`)
				})
			}).catch((emptylist, msg) => {
				console.log(msg)
			})
		}, 3000)

		setTimeout(() => {
			console.log('Session should have expired now ...')
			userService.list(name).then((list) => {
				list.forEach(function(value) {
					console.log(`${value.username}, ${value.email}, ${value.defaultlogin}, ${value.admin ? 'Yes' : 'No'}`)
				})
			}).catch((msg) => {
				console.log(msg)
			})
		}, timeout + 5000)
	}
}).catch((err) => {
})

