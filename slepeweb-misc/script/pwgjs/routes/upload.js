/*
	These routes deal with requests to
	a) Display upload form
	b) Action the upload
*/

const express = require('express')
const router = express.Router()

const sc = require('path').basename(__filename)
const log = require('../slepeweb-modules/logger.js').instance

const pwdb = require('../pwdb')
const formidable = require('formidable')

/*
 * Not required any more, but retain as example of accessing the io object
 *
const flashMessage = (req, message, isError) => {
	var io = req.app.get('socketio')
	io.emit('flash', message, isError)
}
*/

const closeFd = (fd) => {
  fs.close(fd, (err) => {
    if (err) throw err
  })
}

const redirect = (res, path, msg, isErr) => {
	res.redirect(`${path}?flash=${msg.replace(/\s/g, '%20')}&clazz=${isErr ? 'error' : 'none'}`)
}

router.post('/action', (req, res) => {
	var u = req.session.user
	if (u) {
		var form = new formidable.IncomingForm()

		form.parse(req, function (err, fields, files) {
			// The name of the input field specifying the spreadsheet file path is 'xlsx'.
			// filepath on Linux is a file in /tmp, which might be empty if the user
			// submitted the form withouth chosing a file.
			var filepath = files.xlsx.path
			
			pwdb.upload(u, filepath).then((numLoaded) => {
				redirect(res, '/', `Successfully uploaded ${numLoaded} records`)
			}).catch((msg) => {
				redirect(res, '/', msg, true)
			})
	    })
	}
	else {
		redirect(res, '/users/login', 'User session timed out', true)
	}
})

exports.router = router
