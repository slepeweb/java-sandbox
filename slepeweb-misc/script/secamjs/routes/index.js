var express = require('express')
var router = express.Router()

router.get('/', function(req, res, next) {
	var sess = req.session
	if (! sess.email) {
		console.log('No Authorisation')
       //return res.redirect('/login')
	}
	res.render('index', {title: 'Express'})	
})

module.exports = router;
