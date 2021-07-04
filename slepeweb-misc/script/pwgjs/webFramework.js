const express = require('express')
const app = express()
const http = require('http').createServer(app)

const session = require('express-session')
const bodyParser = require('body-parser')

const createError = require('http-errors')
const path = require('path')
const cookieParser = require('cookie-parser')

const indexRouter = require('./routes/index')
const usersRouter = require('./routes/users')
const uploadRouter = require('./routes/upload')
const config = require('./config')

app.set('views', path.join(__dirname, 'views'))
app.set('view engine', 'pug')
app.use(express.json())
app.use(express.urlencoded({ extended: false }))
app.use(cookieParser())
app.use(express.static(path.join(__dirname, 'public')))

app.use(session({
	secret: 'coygc0yg', 
	saveUninitialized: true, 
	resave: true,
	cookie: {
		maxAge: config.sessionTimeout * 1000 // milliseconds
	}}))
	
app.use(bodyParser.json());      
app.use(bodyParser.urlencoded({extended: true}))

app.use('/', indexRouter)
app.use('/users', usersRouter.router)
app.use('/upload', uploadRouter.router)

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

const server = http.listen(8081, () => {
  console.log('listening on port 8081')
})

exports.framework = app
exports.http = http
exports.server = server
