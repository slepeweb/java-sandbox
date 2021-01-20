const PiCamera = require('pi-camera')
const filesModule = require('./files.js')
const systemModule = require('./system.js')
const mailerModule = require('./mailer.js')

const flags = {
	recordingInProgress: false,
	surveillanceEnabled: false,
}

const camera = new PiCamera({
  mode: 'photo',
  output: `${ __dirname }/test.jpg`,
  width: 1280,
  height: 720,
  brightness: 70,
  contrast: 70,
  exposure_mode: 'auto',
  
  // vflip is set to a string value on purpose; boolean doesn't work with pi-camera module
  // The actual value isn't important to pi-camera, as it is a FLAG, and NOT and OPTION.
  // Possible values are 'true' and 'false'.
  vflip: 'true', 
  
  //iso: 0,
  timeout: 6000,
  nopreview: true,
})

const snap = (io) => {
	if (flags.recordingInProgress) {
		io.emit('flash', msg = 'Video recording in progress - please wait', true)
	}
	else {
		var filename = filesModule.setName('P', 'jpg')
		var filepath = filesModule.toPath(filename)
		var msg
		
		camera.mode = 'photo'
		camera.set('output', filepath)
		
		io.emit('flash', msg = 'Photo in progress ...')
		console.log(msg)
		
		camera.snap().then(
			// Successful photo capture
			() => {
				console.log(`Photo saved [filename]`)
				io.emit('flash', '... photo complete')
				io.emit('table', filesModule.table())	
			},
		
			// Failed to capture photo
			(error) => {
				console.log(msg = `... photo production error ${error}`)
				io.emit('flash', msg, true)
			}
		)
	}
}

const record = (io, pir) => {
	if (flags.surveillanceEnabled && ! flags.recordingInProgress) {
		flags.recordingInProgress = true
		
		mailerModule.alert('Security alarm has alerted. Please examine the video recordings @ http://music.buttigieg.org.uk')
		
		var filename = filesModule.setName('V', 'h264')
		var filepath = filesModule.toPath(filename)
		var mp4Path = filepath.replace('.h264', '.mp4')
		var msg
		
		camera.mode = 'video'
		camera.set('output', filepath)
		
		console.log(msg = 'Video in progress ...')
		io.emit('flash', msg)
		
		camera.record().then(
			// Successful recording
			() => {
				// Is the pir still on alert?
				if (pir.digitalRead() == 1) {
					// Record some more
					record(io, pir)
				}
			
				console.log(msg = '... video completed')
				io.emit('flash', msg)		
				flags.recordingInProgress = false
				
				systemModule.run(`/usr/bin/MP4Box -quiet -add ${filepath} ${mp4Path} >/dev/null 2>&1`).then(
					// Successful file conversion
					() => {
						console.log(msg = `... video converted to MP4`)
						io.emit('flash', msg)
						io.emit('table', filesModule.table())
						
						// Delete .h264 file
						filesModule.wipe(filename)
						
						filesModule.upload(mp4Path).then(
							// Successful dropbox upload
							() => {
								console.log(msg = '... file uploaded')
								io.emit('flash', msg)        
								io.emit('table', filesModule.table())
							},
							// Failed to upload file to dropbox
							(error) => {
								console.log(msg = `... file upload error ${error}`)
								io.emit('flash', msg, true)
								io.emit('table', filesModule.table())
							}
						)						
					},
					// Failed to convert to mp4
					(error) => {
						console.log(msg = `... video conversion error ${error}`)
						io.emit('flash', msg, true)
					}
				)
			},
			// Failed to produce the video
			(error) => {
				console.log(msg = `... video production error ${error}`)
				io.emit('flash', msg, true)
				flags.recordingInProgress = false		
			}
		)
	}
	/*
	else {
		io.emit('flash', 'Movement detected, but surveillance is disabled')
	}	
	*/	
}

const setProperty = (name, value) => {
	camera.set(name, value)
}

const toggleSurveillance = () => {
	flags.surveillanceEnabled = ! flags.surveillanceEnabled
	return flags.surveillanceEnabled
}

exports.camera = camera
exports.snap = snap
exports.record = record
exports.toggleSurveillance = toggleSurveillance
exports.flags = flags
exports.setProperty = setProperty
