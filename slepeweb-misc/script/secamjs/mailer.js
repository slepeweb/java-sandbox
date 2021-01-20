const mailer = require('nodemailer');

const transport = mailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'george.buttigieg@gmail.com',
    pass: 'g!g@5Eftg00g6E'
  }
})

const options = {
  from: 'george.buttigieg@gmail.com',
  to: 'george@buttigieg.org.uk',
  subject: 'Security camera alert',
  text: ''
}



const alert = (message) => {
	options.text = message
	transport.sendMail(options, (error, info) => {	
		if (error) {
			console.log(error)
		} 
		else {
			console.log('Email sent: ' + info.response)
		}
	})
}

exports.alert = alert