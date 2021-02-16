const bc = require('bcrypt')

class Cryptor {
	constructor() {
		this.saltRounds = 10
	}
	
	encrypt(plain) {
		return bc.hashSync(plain, this.saltRounds)
	}
	
	compare(plain, hash) {
		return bc.compareSync(plain, hash)
	}
}

module.exports = Cryptor