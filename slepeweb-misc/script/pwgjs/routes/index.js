var express = require('express')
var router = express.Router()

router.get('/', function(req, res, next) {
	var u = req.session.user
	if (! u) {
       return res.redirect('/users/login')
	}
		
	// Session timeout is 300000 millis
	var maxAge = req.session.cookie.maxAge
	res.render('index', {user: u, progress: Math.floor((maxAge * 100) / 300000)})	
})

module.exports = router;
