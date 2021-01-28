const express = require('express')
const router = express.Router()
const sc = require('path').basename(__filename)
const {debug, info, warn, error} = require('../logger.js')

router.get('/login', (req, res) => {
    res.render('login', {title: 'Login', err: req.query.err})
})

router.post('/login', (req, res) => {
	var pin = req.body.pin
	if (['5ecam', '8uttybear', 'tr1gger'].includes(pin)) {
		info(sc, `User ${pin} logged in`)
    	req.session.pin = pin
    	res.redirect('/')
	}
	else {
		warn(sc, `Failed login attempt [${pin}]`)
		res.redirect('/users/login?err=Invalid%20pin')
	}
})

module.exports = router;
