const puppeteer = require('puppeteer-core')
// npm install puppeteer-core@chrome-71

// puppeteer version must match chrome version ... I had to
// install puppeteer-core@chrome-71 to match the version of
// Chromium on rpi-raspbian, but for some reason,
// the versions already installed on ubuntu worked correctly!

class Html2Pdf {
  constructor(p) {
    this.params = p
  }

  convert() {
    return new Promise((resolve, reject) => {
      (async () => {
        const browser = await puppeteer.launch({
          headless: true,
          executablePath: '/usr/bin/chromium-browser',
          args: ['--no-sandbox', '--disable-setuid-sandbox'],
        });

        let postRequestIntercepted = false
        const page = await browser.newPage();

        /*
          This handler will be effective for the main page, and then
          all inlines, including images, stylesheets, etc
        */
        const requestHandler = async (request) => {
          try {
            if (postRequestIntercepted) {
              await page.setRequestInterception(false)
              return await request.continue()
            }

            postRequestIntercepted = true

            var params = {
              'method': 'POST',
              'postData': JSON.stringify(this.params),
              'headers': { ...request.headers(), "content-type": "application/json"}
            }

            await request.continue(params)
          }
          catch(error) {
            console.log('Error intercepting request', {error})
          }
        }

        await page.setRequestInterception(true)
        await page.on('request', requestHandler)
        await page.goto(this.params.url, {waitUntil: 'networkidle0'})

        await page.pdf({
          path: this.params.filePath,
          format: 'a4',
          margin: {
            top: '20mm',
            right: '20mm',
            bottom: '20mm',
            left: '20mm',
          },
          printBackground: true,
        });
        await browser.close();

        resolve(this.params.filePath)
      })();
    })
  }
}

module.exports = Html2Pdf
