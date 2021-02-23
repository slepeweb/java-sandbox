const userdb = require('./userdb')
const PwDatabase = require('./pwdb')
const pwdb = new PwDatabase()
const PwCalculator = require('./pwcalc')
const calculator = new PwCalculator()

const webModule = require('./webFramework')
const io = require('socket.io')(webModule.http)

const sc = require('path').basename(__filename)
const log = require('./logger.js')

process.on('SIGTERM', () => {
	webModule.server.close(() => {
		log.info(sc, 'Process terminated')
	})
})

io.on('connection', (socket) => {

	socket.on('company-list-request', () => {
		pwdb.findAll().then((list) => {
			socket.emit('company-list', list)
		}).catch((err) => {
			log.error(sc, err)
		})
	})
	
	socket.on('lookup', (obj) => {
		if (obj.id != 'none') {
			userdb.findById(obj.id).then((u) => {
				log.info(sc, `Looking up [${obj.company}]`)
				
				pwdb.findOne(obj.company).then((doc) => {
					if (doc) {
						if (! doc.password) {
							[doc.password, doc.chunked] = calculator.calc(obj.key, doc.partyid, doc.mask, doc.maxchars)
						}		
						socket.emit('document', doc)
					}
					else {
						socket.emit('flash', `Company [${obj.company}] not found`, true)
					}			
				}).catch((err) => {
					log.error(sc, err)
				})
			}).catch((err) => {
				log.error(sc, `No such user ${obj.id}`)
			})
		}
		else {
			log.info(sc, 'User not logged in; lookup request ignored')
			socket.emit('relogin')
		}
	}) 
})
