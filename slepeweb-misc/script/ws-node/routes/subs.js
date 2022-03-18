const express = require('express')
const router = express.Router()
const Mailer = require('../slepeweb-modules/mailer')
const emailer = new Mailer({
	service: 'gmail',
	user: 'george.buttigieg56',
	password: 'g1gaL0ftgma15L',
	from: 'george.buttigieg56',
	to: 'george@buttigieg.org.uk',
	subject: 'Sidmouth Tennis Club, Membership Renewal, 2022-23',
})

const Html2Pdf = require('../server/html2pdf.js')

router.post('/pdf', (req, res) => {
	res.setHeader('Content-Type', 'application/json')
	res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000')
	res.setHeader('Access-Control-Allow-Methods', 'POST, GET, OPTIONS')
	res.setHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')

	const body = req.body
	let now = new Date().getTime()
	body.filePath = `/tmp/${now}.pdf`

	// Generate the pdf
	const builder = new Html2Pdf(body)
	builder.convert().then((filePath) => {
		// Send emails with pdf attached
		// To subscriber:
		let message =
`Thank you for completing your subscription renewal form for the 2022-23
season. A copy is attached to this email for your records.`

		emailer.send(message, {
			to: body.email,
			attachments: {
				filename: 'Subscription Renewal',
				path: body.filePath,
				contentType: 'application/pdf',
			}
		})

		// To membership secretary
		message = `Subscription renewal form attached`
		emailer.send(message, {
			to: body.secretary,
			attachments: {
				filename: 'Subscription Renewal',
				path: body.filePath,
				contentType: 'application/pdf',
			}
		})
	})

	res.status(200).end()
})

router.options('/pdf', (req, res) => {
	console.log('Options request received for /pdf')
	res.setHeader('Allow', 'GET, POST, OPTIONS')
	res.setHeader('Access-Control-Allow-Origin', '*')
	res.setHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')
	res.status(204).end()
})

router.post('/page', (req, res) => {

	// Don't think these headers are needed ...
	res.setHeader('Access-Control-Allow-Origin', '*')
	res.setHeader('Access-Control-Allow-Methods', 'POST, GET, OPTIONS')

	res.render('subs/index', {
		title: 'Completed Application',
		content: req.body,
	})
})

// This GET handler is for dev purposes only
router.get('/pagex', (req, res) => {

	res.render('subs/index', {
		title: 'Completed Application',
		content: {
      club: 'Sidmouth Cricket, Tennis & Croquet Club',
      heading: 'Tennis Section - Membership renewal form, 2022/23',
      subheading: 'Subscriptions are due from 1st April 2022',
      membershipCategory: 'Single adult',
      firstname: 'George',
      fullName: 'George Buttigieg',
      oneLineAddress: '63 Newlands Road, Sidmouth, EX10 9NN',
      homephone: '',
      mobile: '0755 777 0817',
      email: 'george@buttigieg.org.uk',
      fees: {
				extras: false,
				total: 143,
				totalStr: '£143.00',
				membership: {
					label: 'Single adult',
					value: 143,
					valueStr: '£143.00',
				},
			},
      countyMemberYesno: 'No',
      childMembers: [],
      sortcode: '40-42-02',
      accountno: '71569651',
      secretary: 'george@buttigieg.org.uk',
    },
	})
})


module.exports = router;
