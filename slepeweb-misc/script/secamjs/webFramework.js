const c = require('./constants.js')

const express = require('express')
const app = express()
const http = require('http').createServer(app)

const session = require('express-session');
const bodyParser = require('body-parser');

const createError = require('http-errors')
const path = require('path')
const cookieParser = require('cookie-parser')

const indexRouter = require('./routes/index')
//const usersRouter = require('./routes/users')

app.set('views', path.join(__dirname, 'views'))
app.set('view engine', 'pug')
app.use(express.json())
app.use(express.urlencoded({ extended: false }))
app.use(cookieParser())
app.use(express.static(path.join(__dirname, 'public')))

app.use(session({secret: 'ssshhhhh',saveUninitialized: true,resave: true}));
app.use(bodyParser.json());      
app.use(bodyParser.urlencoded({extended: true}));

app.use('/', indexRouter)
//app.use('/users', usersRouter)

/*
// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404))
})

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message
  res.locals.error = req.app.get('env') === 'development' ? err : {}

  // render the error page
  res.status(err.status || 500)
  res.render('error')
});
*/

const server = http.listen(c.server.port, () => {
  console.log('listening on port ' + c.server.port)
})

exports.framework = app
exports.http = http
exports.server = server
