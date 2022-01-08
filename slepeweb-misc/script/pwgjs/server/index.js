const sc = require('path').basename(__filename)
const Logger = require('../slepeweb-modules/logger')
Logger.instance = new Logger('pwg', __dirname + '/../logs')
const log = Logger.instance
log.info(sc, '===========================================')

const userdb = require('./userdb')
const pwdb = require('./pwdb')

const pwCalculatorService = require('./pwcalc')
const webModule = require('./webFramework')
const io = require('socket.io')(webModule.http)
const userService = require('./users')
const loginService = require('./login')
const sessionService = require('./session').sessionService

process.on('SIGTERM', () => {
	webModule.server.close(() => {
		log.info(sc, 'Process terminated')
	})
})

io.on('connection', (socket) => {

	socket.on('login-request', (u) => {
		loginService.login(u).then((msg) => {
			socket.emit('login-response', msg, false, u.username)
		}).catch((err) => {
			socket.emit('login-response', err, true, u.username)
		})
		
	})
	
	socket.on('progress-request', (name) => {
		var remainder = 0
		
		if (name) {
			var sesh = sessionService.find(name)
			if (sesh) {
				remainder = sesh.remainingPercent()
			}
		}
		socket.emit('progress-response', remainder)
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
		
	socket.on('company-lookup-request', (arg) => {
		try {
			sessionService.secure(arg.owner, (sesh) => {
				if (arg.owner) {
					userdb.findByName(arg.owner).then((u) => {
						log.info(sc, `Looking up [${arg.company}] for user [${arg.owner}]`)
						
						pwdb.findOne(arg.owner, arg.company).then((doc) => {
							if (doc) {
								if (! doc.password) {
									[doc.password, doc.chunked] = pwCalculatorService.calc(sesh.key, doc.partyid, doc.mask, doc.maxchars)
								}
								else {
									doc.chunked = ''
								}
								socket.emit('company-lookup-response', doc)
							}
							else {
								socket.emit('flash', `Company [${arg.company}] not found`, true)
							}
						}).catch((err) => {
							log.error(sc, err)
						})
					}).catch((err) => {
						log.error(sc, `No such user ${arg.owner}`)
					})
				}
			})
		}
		catch(err) {
			socket.emit('flash', err, true)
		}
	})
	
	socket.on('retire-session', (username) => {
		if (username) {
			sessionService.remove(username)
		}
	})
	
	socket.on('user-list-request', (name) => {
		try {
			sessionService.secure(name, (sesh) => {
				userService.list(sesh.user).then((list) => {
					socket.emit('user-list-response', list, sesh.user.admin)
				}).catch((msg) => {
					socket.emit('user-list-response', [], sesh.user.admin, msg, true)
				})
			})
		}
		catch(err) {
			socket.emit('user-list-response', [], sesh.user.admin, err, true)
		}
	})
	
	socket.on('user-upsert-prepare-request', (uw, name) => { 
		try {
			sessionService.secure(name, (sesh) => {
				socket.emit('user-upsert-prepare-response', uw, sesh.user.admin)
			})
		}
		catch(err) {
			socket.emit('flash', err, true)
		}
	})
	
	socket.on('user-upsert-request', (formMode, formData, name) => { 
		try {
			sessionService.secure(name, (sesh) => {
				// Check passwords provided when necessary
				var passwordErrorMessage = null
				
				/* 
					When UPDATING a user profile and having provided a password, he
					must confirm it too.
				*/
				if (formMode == 'update') {
					if (formData.password && (! formData.confirmpassword || formData.password != formData.confirmpassword)) {
						passwordErrorMessage = 'Passwords do not match'
					}
				}
				/* 
					When adding a NEW user profile, both a password AND password-confirmation
					must be provided.
				*/
				else {
					if (! formData.password) {
						passwordErrorMessage = 'Password not provided'
					}
					else if (formData.password != formData.confirmpassword) {
						passwordErrorMessage = 'Passwords do not match'
					}
				}
				
				if (! passwordErrorMessage) {
					// The user being upserted is identified in formData
					userService.upsert(formMode, formData, sesh.user).then((res) => {
						// If user has updated his own details, then these need to be stored
						// in the session
						if (name == res.payload.username) {
							sesh.user = res.payload
							sessionService.save(sesh)
						}
						
						socket.emit('user-upsert-response', res.message)
					}).catch((err) => {
						socket.emit('user-upsert-response', res.message, res.error)
					})
				}
				else {
					socket.emit('user-upsert-response', passwordErrorMessage, true)
				}
			})
		}
		catch(err) {
			socket.emit('user-upsert-response', `Unexpected error: ${err}`, true)
		}
	})
	
	socket.on('user-delete-request', (name, target) => {
		sessionService.secure(name, (sesh) => {
			if (sesh.user.admin) {
				userdb.remove(target).then((msg) => {
					socket.emit('user-delete-response', msg, false)
				}).catch((err) => {
					socket.emit('user-delete-response', err, true)
				})
			}
			else {
				socket.emit('user-delete-response', 'Insufficient authority!', true)
			}
		})
	})
	
	socket.on('disconnect', (reason) => {
		log.info(sc, `User disconnected: ${reason}`)
	})
})
