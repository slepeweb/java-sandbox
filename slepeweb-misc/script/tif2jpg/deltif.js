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
	var  childEntry = true, childPath, jpgPath, isTif
	
	while (childEntry) {
		childEntry = parentDir.readSync()
		
		if (childEntry) {
			childPath = parentPath + '/' + childEntry.name
			
			if (childEntry.isDirectory()) {
				crawl(childPath)
			}
			else {
				jpgPath = tif2jpg(childPath)
				isTif = jpgPath != null
				
				if (isTif) {
					if (! existsSync(jpgPath)) {
						console.log('JPG missing', jpgPath)
					}
					else {
						try {
							fs.rm(childPath)
							console.log('Deleted', childPath)
						}
						catch(e) {
							console.error('Failed to delete', childPath)
						}
					}
				}
			}
		}
	}
			
	parentDir.close()
}

crawl('./photos')
