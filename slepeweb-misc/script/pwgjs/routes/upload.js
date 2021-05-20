const express = require('express')
const router = express.Router()

const sc = require('path').basename(__filename)
const log = require('../slepeweb-modules/logger.js').instance

const pwdb = require('../pwdb')
const formidable = require('formidable')

router.get('/form', (req, res) => {
	var u = req.session.user
	if (u) {
		res.render('upload', {title: 'Upload', err: req.query.err, loggedIn: req.session.user})
	}
	else {
		res.redirect('/users/login')
	}
})

router.post('/action', (req, res) => {
	var form = new formidable.IncomingForm()
	form.parse(req, function (err, fields, files) {
		var filepath = files.xslx.path		
		pwdb.upload(filepath)
		res.redirect('/')
    })
})

exports.router = router
