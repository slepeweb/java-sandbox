class Util {
	static httpSessionTimeout = 3600 // seconds
	
	static respond(p, msg, err = false) {
		return {
			payload: p,
			message: msg,
			error: err,
		}
	}
	
	constructor() {
	} 
}

module.exports = Util