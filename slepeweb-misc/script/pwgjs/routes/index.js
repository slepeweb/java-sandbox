var express = require('express')
var router = express.Router()
const config = require('../config')

router.get('/', function(req, res, next) {
	var u = req.session.user
	if (! u) {
       return res.redirect('/users/login')
	}
		
	var now = new Date()
	var sessionUsedMillis = now.getTime() - u.sessionStart	
	var percentSpent = Math.floor((sessionUsedMillis * 100) / (u.sessionTimeout * 1000))
	
	var percentRemaining = 100 - percentSpent
	if (percentRemaining < 0) {
		percentRemaining = 0
	}
	
	var decrement = 1 // percent
	var interval = Math.floor((u.sessionTimeout * 1000) / (decrement * 100)) // milliseconds
	var params = {
		user: u, 
		progress: percentRemaining, 
		progressDecrement: decrement, 
		progressInterval: interval
	}
	
	if (req.query.flash) {
		params.flash = req.query.flash
		params.clazz = req.query.clazz
	}
	else {
		params.flash = ''
		params.clazz = 'none'
	}
	
	res.render('index', params)		
})

module.exports = router;
