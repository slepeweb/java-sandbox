const { exec } = require('child_process');

const run = (fullCmd, options = {}) => {
	return new Promise((resolve, reject) => {
		exec(fullCmd, options, (error, stdout, stderr) => {
			if (stderr || error) {
				reject(stderr || error)
			}
			resolve(stdout)
		})
	})
}

exports.run = run