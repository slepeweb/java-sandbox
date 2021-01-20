const webModule = require('./webFramework')
const io = require('socket.io')(webModule.http)
const Gpio = require('pigpio').Gpio
const filesModule = require('./files.js')
const cameraModule = require('./camera.js')

const pir = new Gpio(4, {
  mode: Gpio.INPUT, 
  pullUpDown: Gpio.PUD_DOWN, 
  edge: Gpio.RISING_EDGE, 
  alert: false
})

pir.on('alert', (value) => {
	cameraModule.record(io, pir)
})

const wipe = (filename, quietly) => {
	if (filesModule.wipe(filename)) {
		if (! quietly) {
			io.emit('flash', `File deleted [${filename}]`)
		}
	}
	else {
		io.emit('flash', `Failed to delete file [${filename}]`)
	}
}

io.on('connection', (socket) => {
	console.log('A.N. user connected')
  
	socket.on('photo', () => {
		cameraModule.snap(io)
	})  
  
	socket.on('video', () => {
		cameraModule.record(io)
	})  
  
	socket.on('toggle-surveillance', () => {	
		var status = cameraModule.toggleSurveillance()
		if (status) {
			pir.enableAlert()
		}
		else {
			pir.disableAlert()
		}
		
		io.emit('surveillance', status )
		io.emit('flash', `Surveillance is ${status ? 'active' : 'paused'}`)
	})  
  
	socket.on('delete', (filename) => {
		wipe(filename)
		
		var upl = filename + '.uploaded'
		if (filesModule.exists(upl)) {
			wipe(upl, true)
		}
		
		io.emit('table', filesModule.table())
	})  
  
	socket.on('table-request', () => {
		io.emit('table', filesModule.table())
	})

	socket.on('camera-setting-request', (obj) => {
		cameraModule.setProperty(obj.name, obj.value)
		io.emit('flash', `${obj.name} set to ${obj.value}`)
		io.emit('camera-setting', obj)
	})

	socket.on('camera-status-request', () => {
		var c = cameraModule.camera
		debugger
		
		// This message should go back to the page that gets refreshed, and
		// NOT broadcast to other connected clients
		socket.emit('camera-status', {
			width: c.get('width'),
			height: c.get('height'),
			brightness: c.get('brightness'),
			contrast: c.get('contrast'),
			mode: c.get('exposure_mode'),
			vflip: c.get('vflip'),
			//iso: c.get('iso'),
			surveillance: cameraModule.flags.surveillanceEnabled,
			timeout: c.get('timeout'),
		})
	})

	socket.on('upload', (filename) => {
    	io.emit('flash', 'Uploading file ...')
		var filepath = filesModule.toPath(filename)
		
		filesModule.upload(filepath).then(
			() => {
				io.emit('flash', `... ${filename} uploaded`)
				io.emit('table', filesModule.table())
			},
			(error) => {
				io.emit('flash', `Failed to upload ${filename}`)
			}
		)
	})
})

