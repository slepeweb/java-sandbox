const express = require('express')
const app = express()
const http = require('http').createServer(app)

const bodyParser = require('body-parser')

const createError = require('http-errors')
const path = require('path')

const subsRouter = require('../routes/subs')

/*
  Using morgan, we get messages in the console each time an http request is made.
const morgan = require('morgan')
app.use(morgan('combined'))
*/

app.set('views', path.join(__dirname, '../views'))
app.set('view engine', 'pug')

app.use(express.json())
app.use(express.urlencoded({ extended: false }))
app.use(express.static(path.join(__dirname, '../public')))
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}))
app.use('/subs', subsRouter)

const server = http.listen(3010, () => {
  console.log('listening on port 3010')
})

exports.framework = app
exports.http = http
exports.server = server
