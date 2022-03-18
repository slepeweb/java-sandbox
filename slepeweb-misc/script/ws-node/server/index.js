const sc = require('path').basename(__filename)
const Logger = require('../slepeweb-modules/logger')
Logger.instance = new Logger('slepeweb-ws', __dirname + '/../logs')
const log = Logger.instance
log.info(sc, '===========================================')

const webModule = require('./webFramework')
const io = require('socket.io')(webModule.http)

process.on('SIGTERM', () => {
	webModule.server.close(() => {
		log.info(sc, 'Process terminated')
	})
})

io.on('connection', (socket) => {

	socket.on('html-to-pdf-request', (name) => {
		var remainder = 0

		if (name) {
			var sesh = sessionService.find(name)
			if (sesh) {
				remainder = sesh.remainingPercent()
			}
		}
		socket.emit('html-to-pdf-response', remainder)
	})

	socket.on('company-list-request', (username) => {
		try {
			sessionService.secure(username, (sesh) => {
				pwdb.findAll(username).then((list) => {
					socket.emit('company-list-response', list)
				}).catch((err) => {
					log.error(sc, err)
				})
			})
		}
		catch(err) {
			socket.emit('flash', err, true)
		}
	})

	socket.on('disconnect', (reason) => {
		log.info(sc, `User disconnected: ${reason}`)
	})
})
