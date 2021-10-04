const sc = require('path').basename(__filename)
const Logger = require('../slepeweb-modules/logger')
Logger.instance = new Logger('pwg', __dirname + '/../logs')
const log = Logger.instance
log.info(sc, '===========================================')

var calculator = require('../server/pwcalc')
var db = require('../server/pwdb')
var company = 'Halifax (G)'
var key = 'dummykey'
var spacer = '===================================='


console.log('Ordered list of accounts follows:')
		
db.findAll('george').then((docs)=> {
	if (docs.length == 0) {
		console.log('Database is empty???')
	}
	
	docs.forEach((item, i)=> {
		console.log(item.company)
	})
}).catch((e) => {
	console.error(e)
})

db.findOne('george', company).then((doc) => {
	console.log(spacer)

	if (doc) {
		if (! doc.password) {
			[doc.password, doc.chunked] = calculator.calc(key, doc.partyid, doc.mask, doc.maxchars)
			console.log(`The password calculated for ${company} using key [${key}] is [${doc.password}]`)
		}		
	}
	else {
		console.log(`Company ${company} NOT FOUND`)
	}			
}).catch((err) => {
	console.error('Error: ', err)
})

