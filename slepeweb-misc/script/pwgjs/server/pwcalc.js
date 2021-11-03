class PwCalculator {
	constructor() {
	}
	
	overwriteKey(secretKey, mask) {
		if (mask) {
			var k = [... secretKey]
			var m = [... mask]
			for (var i = 0; i < m.length; i++) {
				if (i >= secretKey.length) {
					break
				}
				
			    if (m[i] != '.') {
			    	k[i] = m[i]
			    }
			}
			
			return k.join('')
		}
		
		return secretKey
	}
	
	// Example parameters: accountName=hmrc, secretKey=abcdefgh, maxChars=10
	calc(secretKey, accountName, mask, maxChars) {
		// Overlay mask onto the secretKey, if mask not empty
		var maskedKey = this.overwriteKey(secretKey, mask)
		
		var accountNameLength = accountName.length
		var accountSliceLength = 4		
		var remainder = accountNameLength % maskedKey.length
		
		// Numeric value to insert into result
		var numericChar = remainder.toString()
		
		// Uppercase the last character in the account name
		var uppercasedChar = accountName.substring(accountNameLength - 1).toUpperCase()
		
		// This is the index where we overlay the uppercased character onto the account name
		var uppercasedCharIndex = remainder == 0 ? maskedKey.length - 1 : remainder - 1

		// partA is the processed account name
		var partA = null
		if (accountNameLength > accountSliceLength) {
			partA = accountName.substring(0, accountSliceLength)
		}
		else {
			partA = accountName
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
		
		partA = partA + numericChar + uppercasedChar
		
		// partB is the processed key
		var partB = maskedKey.substring(0, uppercasedCharIndex) + uppercasedChar + maskedKey.substring(uppercasedCharIndex + 1)
		
		var result = partA + partB
		if (accountName >= maskedKey) {
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
}

module.exports = new PwCalculator()