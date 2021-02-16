var PwCalculator = require('./pwcalc')
var calc = new PwCalculator()
var PwDatabase = require('./pwdb')
var db = new PwDatabase()
var company = 'Halifax (G)'

db.findAll().then(
	(docs)=> {
		if (docs.length == 0) {
			console.log('Database is empty???')
		}
		
		docs.forEach((item, i)=> {
			console.log(item)
		})
	}).catch(
		(e) => {
			console.error(e)
		})


db.findOne(company).then((doc) => {
	if (doc) {
		if (! doc.password) {
			[doc.password, doc.chunked] = calculator.calc(obj.key, doc.partyid, doc.mask, doc.maxchars)
			console.log(doc.password)
		}		
	}
	else {
		console.log(`Company ${company} not found`)
	}			
}).catch((err) => {
	console.error('Error', err)
})
