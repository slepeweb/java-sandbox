const sc = require('path').basename(__filename)
const Logger = require('./slepeweb-modules/logger')
Logger.instance = new Logger('pwg', __dirname + '/logs')
const log = Logger.instance
log.info(sc, '===========================================')

const userdb = require('./userdb')
const pwdb = require('./pwdb')

const PwCalculator = require('./pwcalc')
const calculator = new PwCalculator()

const webModule = require('./webFramework')
const io = require('socket.io')(webModule.http)

const usersMod = require('./routes/users')
const uploadMod = require('./routes/upload')

process.on('SIGTERM', () => {
	webModule.server.close(() => {
		log.info(sc, 'Process terminated')
	})
})

io.on('connection', (socket) => {

	socket.on('company-list-request', (owner) => {
		pwdb.findAll(owner.name).then((list) => {
			socket.emit('company-list', list)
		}).catch((err) => {
			log.error(sc, err)
		})
	})
	
	socket.on('lookup', (res) => {
		if (res.owner.id != 'none') {
			userdb.findById(res.owner.id).then((u) => {
				log.info(sc, `Looking up [${res.company}] for user [${res.owner.name}]`)
				
				pwdb.findOne(res.owner.name, res.company).then((doc) => {
					if (doc) {
						if (! doc.password && doc.partyid != 'none') {
							[doc.password, doc.chunked] = 
								calculator.calc(usersMod.keys[u.password], doc.partyid, doc.mask, doc.maxchars)
						}		
						socket.emit('document', doc)
					}
					else {
						socket.emit('flash', `Company [${res.company}] not found`, true)
					}			
				}).catch((err) => {
					log.error(sc, err)
				})
			}).catch((err) => {
				log.error(sc, `No such user ${res.owner.id}`)
			})
		}
		else {
			log.info(sc, 'User not logged in; lookup request ignored')
			socket.emit('relogin')
		}
	})
	
	socket.on('disconnect', (reason) => {
		log.info(sc, `User disconnected: ${reason}`)
	})
})
