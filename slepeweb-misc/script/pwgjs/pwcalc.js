class PwCalculator {
	constructor() {
	}
	
	overwriteKey(key, mask) {
		if (mask) {
			var k = [... key]
			var m = [... mask]
			for (var i = 0; i < m.length; i++) {
				if (i >= key.length) {
					break
				}
				
			    if (m[i] != '.') {
			    	k[i] = m[i]
			    }
			}
			
			return k.join('')
		}
		
		return key
	}
	
	// Example parameters: partyid=hmrc, key=abcdefgh, maxChars=10
	calc(key, partyid, mask, maxChars) {
		// Apply mask to key, if mask present
		var maskedKey = this.overwriteKey(key, mask)
		
		var strlen = partyid.length
		var lastChar = partyid.substring(strlen - 1).toUpperCase()
		var alen = 4
		
		var rem = strlen % maskedKey.length
		
		// index is the position where we substitute the uppercased character
		var index
		
		// Is the partyid length string longer than the maskedKey ...?
		if (strlen < maskedKey.length) {
		    // ... No - it is less
			index = strlen
		}
		else if (strlen > maskedKey.length) {
			index = rem
		}
		else {
			index = 0
		}
		
		var partA = null
		if (strlen > alen) {
			partA = partyid.substring(0, alen)
		}
		else {
			partA = partyid
		}
		
		var charmap = {
			i: '1',
			o: '0',
			s: '5',
			b: '8',
		}
		
		var regex
		['i', 'o', 's', 'b'].forEach((item, i) => {
			regex = new RegExp(item, 'g')
			partA = partA.replace(regex, charmap[item])
		})
		
		partA = partA + rem.toString() + lastChar
		var cursor = index == 0 ? 1 : index
		var partB = maskedKey.substring(0, cursor - 1) + lastChar + maskedKey.substring(cursor)
		
		var result = partA + partB
		if (partyid >= maskedKey) {
			result = partB + partA
		}
		
		if (maxChars && result.length > maxChars) {
			result = result.substring(0, maxChars)
		}
		
		return [result, this.chunk(result)]
	}
	
	chunk(s) {
		var i = 0, len = 4, chunk
		var result = ''
		
		do {
			chunk = s.substring(i, i + len)
			if (result.length > 0) {result += ' | '}
			result += chunk
			i += len
		}
		while (chunk.length == 4)
		
		return result
	}
	
	test(mask) {
		var pwd, chunked
		var key = 'Password1'
		var list = ['apple', 'halifax', 'microsoft', 'telegraph', 'amazon', 
			'eastdevondistrictcouncil', 'bostonseeds', '123reg']
		
		list.forEach((company, i) => {
			[pwd, chunked] = this.calc(key, company, mask)
			console.log(`${company}: ${pwd} (${chunked})`)
		})
	}
}

module.exports = PwCalculator