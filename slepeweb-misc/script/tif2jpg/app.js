const Jimp = require('jimp')
const fs = require('fs/promises')
const {existsSync} = require('fs')

const tif2jpg = (path) => {
	const ext = '.tif'
	if (path.endsWith(ext)) {
		return path.substring(0, path.length - ext.length) + '.jpg'
	}
	
	return null
}

const crawl = async (parentPath) => {
	let parentDir = await fs.opendir(parentPath)
	var  childEntry = true, childPath, convertPath
	
	while (childEntry) {
		childEntry = parentDir.readSync()
		
		if (childEntry) {
			childPath = parentPath + '/' + childEntry.name
			
			if (childEntry.isDirectory()) {
				crawl(childPath)
			}
			else {
				convertPath = tif2jpg(childPath)
				
				if (convertPath) {
					if (existsSync(convertPath)) {
						console.log('Exists', convertPath)
					}
					else {
						try {
							let img = await Jimp.read(childPath)
							img.write(convertPath)
							console.log('Written', convertPath)
						}
						catch(e) {
							console.error('Failed to convert', childPath)
						}
					}
				}
			}
		}
	}
			
	parentDir.close()
}

crawl('./photos')
