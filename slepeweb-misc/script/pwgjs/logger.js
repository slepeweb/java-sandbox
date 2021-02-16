const snl = require('simple-node-logger')

class Logger {
	constructor(filenamePrefix) {
		this.logger = snl.createRollingFileLogger( 
			{
				logDirectory: __dirname + '/logs',
				fileNamePattern: filenamePrefix + '-<date>.log',
				dateFormat:'YYYY.MM.DD-HHa'
			}
		)
	}
	
	format(category, msg) {
		return `(${category}) ${msg}`
	}
	
	trace(category, msg) {this.logger.trace(this.format(category, msg))}
	debug(category, msg) {this.logger.debug(this.format(category, msg))}
	info(category, msg) {this.logger.info(this.format(category, msg))}
	warn(category, msg) {this.logger.warn(this.format(category, msg))}
	error(category, msg) {this.logger.error(this.format(category, msg))}
	fatal(category, msg) {this.logger.fatal(this.format(category, msg))}
}

module.exports = new Logger('pwg')
