module.exports = {
	authorisation: {
		pin: '5ecam',
	},
	server: {
		port: 8080,
		url: {
			media: '/media',
			resource: '/resource',
		},
	},
	camera: {
		width: 1280,
		height: 720,
		brightness: 70,
		contrast: 70,
		exposure_mode: 'auto',
  
		// vflip is set to a string value on purpose; boolean doesn't work with pi-camera module
		// The actual value isn't important to pi-camera, as it is a FLAG, and NOT and OPTION.
		// Possible values are 'true' and 'false'.
		vflip: 'true', 
  
		//ISO: 0,
		timeout: 8000,
		nopreview: true,
	},
	email: {
		service: 'gmail',
		username: 'george.buttigieg56',
		password: 'g1gaL0ftgma15L',
		from: 'george.buttigieg56@gmail.com',
		to: 'george@buttigieg.org.uk',
		subject: 'Security camera alert',
	},
}
