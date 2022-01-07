"use strict"

const rp = require('request-promise')
const cheerio = require('cheerio')
const SIDMOUTH = 'Sidmouth'

const getCellValue = (row, clazz) => {
  let cell = null

  for (let i = 0; i < row.children.length; i++) {
    cell = row.children[i]

    if (cell.type == 'tag' && cell.name == 'td' && cell.attribs.class == clazz) {
      if (cell.children && cell.children.length > 0) {
        return cell.children[0].data
      }
    }
  }

  return null
}

const getFixture = (row) => {
  let fixture = {
    date: getCellValue(row, 'column-1'),
    home: getCellValue(row, 'column-2'),
    away: getCellValue(row, 'column-3'),
    score: getCellValue(row, 'column-4'),
  }

  return fixture.date && fixture.home && fixture.away ? fixture : null
}

const substSidmouth = (team, format) => {
  if (team.includes(SIDMOUTH) && team.length > SIDMOUTH.length) {
    let suffix = team.substring(SIDMOUTH.length + 1)
    return `${SIDMOUTH} ${format} ${suffix}`
  }

  return team
}

const scrapeDivision = async (division) => {
  let html = await rp(division.url)
  let $html = cheerio.load(html)

  // 'rows' is an array of Node objects.
  // Its 'children' property yields a list of child Node objects, of type 'th' or 'td'.
  const rows = $html(`${division.table} tbody tr`)
  let fixtures = []
  let fixture

  for (let i=0; i < rows.length; i++) {
    fixture = getFixture(rows[i])
    if (fixture && (fixture.home.includes(SIDMOUTH) || fixture.away.includes(SIDMOUTH))) {
      fixture.home = substSidmouth(fixture.home, division.format)
      fixture.away = substSidmouth(fixture.away, division.format)
      fixtures.push(fixture)
    }
  }

  return fixtures
}

const scrape = async (eastDevonLeagues) => {
  let results = []
  let division

  for (let k = 0; k < eastDevonLeagues.length; k++) {
    division = eastDevonLeagues[k]
    console.log('Scraping data for', division.name, ', division', division.division)
    results.push(... await scrapeDivision(eastDevonLeagues[k]))
  }
  console.log('Found total of', results.length, 'fixtures for this league')
  return results
}


exports.scrape = scrape
