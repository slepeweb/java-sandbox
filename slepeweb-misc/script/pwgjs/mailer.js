const mailer = require('nodemailer');

const transport = mailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'george.buttigieg56',
    pass: 'g1gaL0ftgma15L',
  }
})

const options = {
  from: 'george.buttigieg56@gmail.com',
  to: 'george@buttigieg.org.uk',
  subject: 'PW Login Notification',
  text: ''
}

const sc = require('path').basename(__filename)
const log = require('./logger.js')

const alert = (message) => {
	options.text = message
	transport.sendMail(options, (err, info) => {	
		if (err) {
			error(sc, err)
		} 
		else {
			log.info(sc, 'Email sent: ' + info.response)
		}
	})
}

module.exports = alert