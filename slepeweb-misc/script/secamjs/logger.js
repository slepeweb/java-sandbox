const logger = require('simple-node-logger').createRollingFileLogger( 
	{
		logDirectory: __dirname + '/logs',
		fileNamePattern: 'secam-<date>.log',
		dateFormat:'YYYY.MM.DD-HHa'
	}
)

const format = (category, msg) => {
	return `(${category}) ${msg}`
}

module.exports = {
	trace: (category, msg) => {logger.trace(format(category, msg))},
	debug: (category, msg) => {logger.debug(format(category, msg))},
	info: (category, msg) => {logger.info(format(category, msg))},
	warn: (category, msg) => {logger.warn(format(category, msg))},
	error: (category, msg) => {logger.error(format(category, msg))},
	fatal: (category, msg) => {logger.fatal(format(category, msg))},
}