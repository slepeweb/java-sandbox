const sc = require('path').basename(__filename)
const Logger = require('../slepeweb-modules/logger')
Logger.instance = new Logger('pwg', __dirname + '/../logs')
const log = Logger.instance
log.info(sc, '===========================================')

const moment = require('moment')
var now = moment()

var pad = (n) => {
	var s = '' + n
	if (s.length == 1) {
		s = '0' + s
	}
	return s
}

const loginService = require('../server/login')
const sessionService = require('../server/session').sessionService

var pfix = pad(now.minutes()) + pad(now.hours())

const output = (user, pwd) => {
	var s = `Logging in as user ${user} with pwd ${pwd}: `
	loginService.login({username: user, password: pwd, key: 'gg'}).then((msg) => {
		console.log(s, msg)
	}).catch((err) => {
		console.log(s, err)
	})
}

output('george', pfix + 'gigabyte')

setTimeout(() => {
	output('george', pfix + 'giga8yte')
}, 1000)

setTimeout(() => {
	output('george', '1019giga8yte')
}, 2000)

setTimeout(() => {
	output('david', pfix + 'giga8yte')
}, 3000)