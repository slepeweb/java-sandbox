var express = require('express');
var router = express.Router();
var sess

router.get('/', (req,res) => {
    sess = req.session
    if(! sess.email) {
        return res.redirect('/login')
    }
  	res.render('index', { title: 'Express' });
});

router.post('/login',(req,res) => {
    sess = req.session;
    sess.email = req.body.email;
    res.end('done');
});

router.get('/admin',(req,res) => {
    sess = req.session;
    if(sess.email) {
        res.write(`<h1>Hello ${sess.email} </h1><br>`);
        res.end('<a href='+'/logout'+'>Logout</a>');
    }
    else {
        res.write('<h1>Please login first.</h1>');
        res.end('<a href='+'/'+'>Login</a>');
    }
});

router.get('/logout',(req,res) => {
    req.session.destroy((err) => {
        if(err) {
            return console.log(err);
        }
        res.redirect('/');
    });

});

module.exports = router;
