const webModule = require('./webFramework')
const io = require('socket.io')(webModule.http)
const Gpio = require('pigpio').Gpio

const pir = new Gpio(4, {
  mode: Gpio.INPUT, 
  pullUpDown: Gpio.PUD_DOWN, 
  edge: Gpio.RISING_EDGE, 
  alert: true
})

pir.on('alert', (value) => {
		setInterval(() => {
			console.log(pir.digitalRead())
		}, 100)
})
