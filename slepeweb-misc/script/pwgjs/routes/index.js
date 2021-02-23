var express = require('express')
var router = express.Router()

router.get('/', function(req, res, next) {
	var u = req.session.user
	if (! u) {
       return res.redirect('/users/login')
	}
	res.render('index', {title: 'Passwords', user: u, loggedIn: true})	
})

module.exports = router;
