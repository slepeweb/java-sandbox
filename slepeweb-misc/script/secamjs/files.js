const fs = require('fs')
const dropboxApi = require('dropbox-v2-api')

const UPLOAD_FILE_SIZE_LIMIT = 150 * 1024 * 1024
const ACCESS_TOKEN = '4wPGw33d4lcAAAAAAAAE2yEVxIuEqa8tJugyLWewvArmg79Hhd-9e9DUrDU4hKXj'
const UPLOADED_EXT = '.uploaded'
const PHOTO_EXT = '.jpg'
const VIDEO_EXT = '.mp4'

const mediaDirPath = `${__dirname}/public/media`

const toPath = (filename) => {
  return `${__dirname}/public/media/${filename}`
}

const setName = (prefix, ext) => {
  var d = new Date()
  var dateStr = [
    [d.getFullYear(), pad2(d.getMonth() + 1), pad2(d.getDate())].join(''),
    [pad2(d.getHours()), pad2(d.getMinutes()), pad2(d.getSeconds())].join('')].join('-')
    
  return `${prefix}-${dateStr}.${ext}`
}

const pad2 = (num) => {
  return num < 10 ? '0' + num : num.toString()
}

const formatSize = (value) => {
	if (value < 1000) {
		return `${value} bytes`
	}
	else if (value < 999999) {
		return `${Math.ceil(value / 1024)} kb`
	}
	else {
		return `${Math.ceil(value / (1024 * 1024))} Mb`
	}
}

const parseFilename = (filename) => {
	var type = filename.substring(0, 1) == 'V' ? 'Video' : 'Photo'
	var year = filename.substring(2, 6)
	var month = filename.substring(6, 8)
	var day = filename.substring(8, 10)
	var hour = filename.substring(11, 13)
	var minute = filename.substring(13, 15)
	var second = filename.substring(15, 17)
	var sortKey = filename.substring(2, 10) + filename.substring(11, 17)
	return [sortKey, `${day}/${month}/${year}`, `${hour}:${minute}:${second}`, type]
}

const table = () => {
  const mediaDir = fs.opendirSync(mediaDirPath)
  var dirEntry = undefined
  var list = []
  var stats = undefined
  var filename = undefined, filepath = undefined
  var date, time, size, sortKey, type

  while ((dirEntry = mediaDir.readSync()) != null) {
    filename = dirEntry.name
    
    if (filename.endsWith(PHOTO_EXT) || filename.endsWith(VIDEO_EXT)) {
	    filepath = `${mediaDirPath}/${filename}`
	    
	    // IMPORTANT semi-colon to terminate the next line, clearly separating it
	    // from the open square bracket that follows
	    stats = fs.statSync(filepath);
	    
	    [sortKey, date, time, type] = parseFilename(filename)
	    
	    list.push({
	      type: type,
	      sortKey: sortKey,
	      date: date,
	      time: time,
	      filename: filename,
	      filepath: filepath,
	      url: `/media/${filename}`,
	      size: formatSize(stats.size),
	      uploaded: exists(filename + UPLOADED_EXT),
	    })
    }
  }
  
  mediaDir.closeSync()
  
  return list.sort((a, b) => {
  	if (a.sortKey < b.sortKey) {
  		return 1
  	}
  	else if (a.sortKey > b.sortKey) {
  		return -1
  	}
  	return 0
  });
}

const wipe = (filename) => {
  const mediaDir = fs.opendirSync(mediaDirPath)
  var dirEntry = undefined
  var filepath = undefined
  var result = false

  while ((dirEntry = mediaDir.readSync()) != null) {
    if (filename == dirEntry.name) {
	    filepath = `${mediaDirPath}/${filename}`
	    try {
		    fs.rmSync(filepath)
		    result = true
	    	console.log(`Deleted file [${filepath}]`)
	    }
	    catch(err) {
	    	console.log(`Error deleting file [${filepath}]: ${err.message}`)
	    }
    }
  }
  
  mediaDir.close()
  return result
}

const dbx = dropboxApi.authenticate({
    token: ACCESS_TOKEN
})

const upload = (filepath) => {
	var promise = new Promise(function(resolve, reject) {	
		var cursor = filepath.lastIndexOf('/')
		var filename = filepath.substring(cursor + 1)
		var params = {
		    resource: 'files/upload',
		    parameters: {path: `/${filename}`},
		    readStream: fs.createReadStream(filepath)
		}
		
		dbx(params, (err, result, response) => {
		    if (err) {
		    	reject(err)
		    }
		    else {
		    	flagUploaded(filepath)
		    	resolve('ok')
		    }
		})
	})
	
	return promise
}

const flagUploaded = (filepath) => {
	// Create an empty file to indicate upload was successful
	fs.open(filepath + UPLOADED_EXT, 'w', (err, fd) => {
		fs.close(fd, (e) => {
		})
	})
}

const exists = (filename) => {
	return fs.existsSync(toPath(filename))
}

exports.table = table
exports.toPath = toPath
exports.setName = setName
exports.wipe = wipe
exports.upload = upload
exports.exists = exists
exports.mediaDirPath = mediaDirPath
