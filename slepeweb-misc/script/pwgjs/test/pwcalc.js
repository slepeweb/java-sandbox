const pwCalculator = require('../server/pwcalc')
	
var pwd, chunked, mask
var key = 'Password1'
var list = ['apple', 'halifax', 'microsoft', 'telegraph', 'amazon', 
	'eastdevondistrictcouncil', 'bostonseeds', '123reg']

list.forEach((company, i) => {
	[pwd, chunked] = pwCalculator.calc(key, company, mask)
	console.log(`${company}: ${pwd} (${chunked})`)
})
