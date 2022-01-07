"use strict"

const rp = require('request-promise')
const cheerio = require('cheerio')
const fs = require('fs')
const exeterMod = require('./exeter')
const eastDevonMod = require('./eastDevon')

const baseYear = 2021
const monthData = {
  sep: [0, 8],
  oct: [0, 9],
  nov: [0, 10],
  dec: [0, 11],
  jan: [1, 0],
  feb: [1, 1],
  mar: [1, 2],
  apr: [1, 3]
}

const exeterLeagues = [
  {
    name: 'Exeter & District Winter League, 2021-22',
    url: 'https://www.exetertennisleague.co.uk/winterleague/winterleague.html',
    years: {
      '2021': ['Oct', 'Nov', 'Dec'],
      '2022': ['Jan', 'Feb', 'Mar', 'Apr']
    },
    divisions: [
      {
        division: 1,
        format: 'Ladies',
        start: 3,
        end: 8
      },
      {
        division: 2,
        format: 'Ladies',
        start: 14,
        end: 19
      },
      {
        division: 4,
        format: 'Ladies',
        start: 36,
        end: 42
      },
      {
        division: 1,
        format: 'Mens',
        start: 47,
        end: 52
      },
      {
        division: 3,
        format: 'Mens',
        start: 68,
        end: 74
      }
    ]
  }
]

const eastDevonLeagues = [
  {
    name: 'East Devon Winter Mixed League, 2021-22',
    format: 'Mixed',
    url: 'https://www.eastdevontennisleague.co.uk/division-2-winter-2021-2022/',
    table: '#tablepress-127',
    division: 2
  },
  {
    name: 'East Devon Winter Mixed League, 2021-22',
    format: 'Mixed',
    url: 'https://www.eastdevontennisleague.co.uk/division-5-winter-2021-2022/',
    table: '#tablepress-124',
    division: 5
  }
]

//identifyRows(leagues[0]).then((r) => {console.log(r)})

const main = async () => {
  let results = []

  // Scrape sites
  results.push(... await exeterMod.scrape(exeterLeagues))
  results.push(... await eastDevonMod.scrape(eastDevonLeagues))

  // Parse date strings, and replace with date objects
  parseDate(results)

  // Sort fixtures by Date
  sort(results)

  // Produce html
  toHtml(results)

  console.log('Scraped', results.length, 'fixtures in total')
  //console.log('Final results: \n', results)
}

const parseDate = (results) => {
  let r, dayStr, monthStr, day, month, year, matcher, arr

  // Date format on exteter site
  let rgxA = /(\d{1,2})\s+([a-z]{3})/i

  // Date format on east devon site
  let rgxB = /[a-z]{3,5},\s+(\d{1,2})[a-z]{2}\s+([a-z]{3})/i

  for (let i = 0; i < results.length; i++) {
    r = results[i]
    if (r.date) {
      if (matcher = r.date.match(rgxA)) {
        dayStr = matcher[1]
        monthStr = matcher[2]
      }
      else if (matcher = r.date.match(rgxB)) {
        dayStr = matcher[1]
        monthStr = matcher[2]
      }
      else {
        dayStr = monthStr = null
        console.log(`Wrong date pattern [${r.date}]`)
      }

      if (dayStr && monthStr) {
        arr = monthData[monthStr.toLowerCase()]
        if (arr) {
          year = baseYear + arr[0]
          month = arr[1]
          r.date = new Date(year, month, parseInt(dayStr))
        }
      }
    }
    else {
      if (! r.score) {
        console.log('Both date and score are empty:', r.home, 'v', r.away)
      }
    }
  }
}

const sort = (results) => {
  results.sort((a, b) => {
    return a.date - b.date
  })
}

const toHtml = (results) => {
  let venue, first, second
  let now = new Date()
  let stream = fs.createWriteStream('./fixtures.html')
  stream.write('<table><tbody>')
  stream.write(`<tr>
			<td>
			<h3>Sidmouth Team&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h3>
			</td>
			<td>
			<h3>Date&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h3>
			</td>
			<td>
			<h3>Venue&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h3>
			</td>
			<td>
			<h3>Opponents</h3>
			</td>
		</tr>
    `)

  for (let i = 0; i < results.length; i++) {
    if (results[i].date && results[i].date > now) {
      if (results[i].home.includes('Sidmouth')) {
        venue = 'H'
        first = results[i].home
        second = results[i].away
      }
      else {
        venue = 'A'
        first = results[i].away
        second = results[i].home
      }

      stream.write(`<tr>
        <td><p>${first.replace('Sidmouth ', '')}</p></td>
        <td><p>${results[i].date.toLocaleDateString('en-GB')}</p></td>
        <td><p>${venue}</p></td>
        <td><p>${second}</p></td>
      </tr>`)
    }
  }

  stream.write('</tbody></table>')
  stream.end()
}

main()
