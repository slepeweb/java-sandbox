const mailer = require('nodemailer');
const clone = require('clone');
const sc = require('path').basename(__filename)
const log = require('./logger.js').instance

class Mailer {
	constructor(p) {
		this.params = clone(p)
		this.params.text = ''
		this.transport = null
		if (p.service && p.user && p.password) {
			this.transport = mailer.createTransport({
				service: p.service,
				auth: {
					user: p.user,
					pass: p.password,
				}
			})
		}
	}
	
	send(message, p) {
		if (! this.transport) {
			log.error('Mailer construction error; message not sent:', message)
		}
		
		var cloned = clone(this.params)
		cloned.text = message
		
		// p provides ability to override default mail options
		if (p) {
			Object.keys(p).forEach((k) => {
				cloned[k] = p[k]
			})
		}
		
		this.transport.sendMail(cloned, (err, info) => {	
			if (err) {
				log.error(sc, err)
			} 
			else {
				log.info(sc, 'Email sent: ' + info.response)
			}
		})
	}	
}

module.exports = Mailer