const express = require('express')
const router = express.Router()

const userdb = require('../userdb')

const Cryptor = require('../crypt')
const cryptor = new Cryptor()

const sc = require('path').basename(__filename)
const log = require('../logger.js')

router.get('/login', (req, res) => {
    res.render('login', {title: 'Login', err: req.query.err, loggedIn: req.session.user})
})

router.get('/logout', (req, res) => {
	req.session.user = null
	res.redirect('/')
})

router.post('/login', (req, res) => {
	var username = req.body.username
	var password = req.body.password
	var key = req.body.key
	
	if (username && password && key) {
		userdb.find(username).then(
			(u) => {
				if (u) {
					if (cryptor.compare(password, u.password)) {
						log.info(sc, `User ${username} logged in`)
				    	req.session.user = u
				    	u.key = key
				    	res.redirect('/')
			    	}
					else {
						log.warn(sc, `Failed login attempt - password mis-match [${username}]`)
						res.redirect('/users/login?err=Invalid%20user%20credentials (A)')
					}
				}
				else {
					log.warn(sc, `Failed login attempt - no such user [${username}]`)
					res.redirect('/users/login?err=Invalid%20user%20credentials (B)')
				}
		}).catch(
			(err) => {
				log.error(sc, err)
				res.redirect('/users/login?err=Invalid%20user%20credentials (C)')
			})
	}
	else {
		log.error(sc, 'Incomplete login details')
		res.redirect('/users/login?err=Please%20complete%20all%20fields')
	}
})

router.get('/add', (req, res) => {
	var u = req.session.user
	if (u) {
		userdb.save({
			username: req.query.username, 
			password: req.query.password
		})
	}
	res.redirect('/')
})

module.exports = router;
