const c = require('./constants.js')
const mailer = require('nodemailer');

const transport = mailer.createTransport({
  service: c.email.service,
  auth: {
    user: c.email.username,
    pass: c.email.password,
  }
})

const options = {
  from: c.email.from,
  to: c.email.to,
  subject: c.email.subject,
  text: ''
}

const sc = require('path').basename(__filename)
const {debug, info, warn, error} = require('./logger.js')

const alert = (message) => {
	options.text = message
	transport.sendMail(options, (err, info) => {	
		if (err) {
			error(sc, err)
		} 
		else {
			info(sc, 'Email sent: ' + info.response)
		}
	})
}

exports.alert = alert