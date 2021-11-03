const assert = require('assert')
const pwCalculator = require('../server/pwcalc')
	
var pwd, chunked, mask
var key = 'gassword'
var list = [
	['apple', 'appl5EgassEord'],
	['halifax', 'gasswoXdhal17X'],
	['microsoft', 'Tasswordm1cr1T'],
	['telegraph', 'Hasswordtele1H'],
	['amazon', 'amaz6NgasswNrd'],
	['eastdevondistrictcouncil', 'ea5t0LgassworL'],
	['bostonseeds', '805t3SgaSsword'],
	['123reg', '123r6GgasswGrd'],
	['govuklpa', 'gassworAg0vu0A'],
	['giffgaff', 'gassworFg1ff0F']
	]

var ok = true

list.forEach((pairing, i) => {
	[pwd, chunked] = pwCalculator.calc(key, pairing[0], mask)

	try {
		assert.equal(pwd, pairing[1])
	}
	catch (err) {
		ok = false
		console.log(`*** For ${pairing[0]},\n      got: ${err.actual}\n expected: ${err.expected}`)
	}
})

if (ok) {
	console.log('No errors found')
}