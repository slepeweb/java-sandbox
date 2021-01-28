var express = require('express')
var router = express.Router()

router.get('/', function(req, res, next) {
	if (! req.session.pin) {
       return res.redirect('/users/login')
	}
	res.render('index', {title: 'Secam'})	
})

module.exports = router;
