var express = require('express')
var router = express.Router()
var sessionService = require('../server/session').sessionService

// Main page (modes A and B)
router.get('/', function(req, res) {
	var name = req.session.username
	// Check this user's login session is still live
	
	if (name) {
		var sesh = sessionService.find(name)
		if (! (sesh && sesh.isLive())) {
			req.session.username = null
			name = null
		}
	}
	
	res.render('index', {
		title: 'Password Manager', 
		username: name
	})	
})

// Stores username in http session
router.get('/session', function(req, res) {
	req.session.username = req.query.username
	res.json('dummy')	
})

const pwdb = require('../server/pwdb')
const formidable = require('formidable')

// This route deals with requests to upload the xlsx spreadsheet.
// Can only do this with an http post, otherwise local files would not be accessible.
router.post('/upload', (req, res) => {
	var name = req.session.username
	
	if (name) {
		var sesh = sessionService.find(name)
		if (sesh && sesh.isLive()) {
			var form = new formidable.IncomingForm()
	
			form.parse(req, function (err, fields, files) {
				// The name of the input field specifying the spreadsheet file path is 'xlsx'.
				// filepath on Linux is a file in /tmp, which might be empty if the user
				// submitted the form withouth chosing a file.
				var filepath = files.xlsx.path
				
				pwdb.upload(sesh.user, filepath).then((numLoaded) => {
					res.json({msg: `Successfully uploaded ${numLoaded} records`, err: false})
				}).catch((s) => {
					res.json({msg: s, err: true})
				})
		    })
	    }
	    else {
			res.json({msg: 'User session expired', err: true})
	    }
	}
	else {
		res.json({msg: 'User not identified', err: true})
	}
})

module.exports = router;
