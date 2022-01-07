"use strict"

const rp = require('request-promise')
const cheerio = require('cheerio')
const SIDMOUTH = 'Sidmouth'

/*
  Uses the table of data computed by scrapeDivisionTable to produce
  a list of fixtures for Sidmouth teams.
*/
const getFixtures = (tableRows, division, teamsInDivision) => {
  let fixture, fixtures = []
  let homeTeam, awayTeam
  let tableCells, cell, left, right

  for (let i = 0; i < teamsInDivision.length; i++) {
    tableCells = tableRows[i]
    homeTeam = tableCells[0]

    for (let j = 1; j < tableCells.length; j++) {
      cell = tableCells[j]
      awayTeam = teamsInDivision[j - 1]

      // Only interested in fixtures that involve Sidmouth
      if (homeTeam && awayTeam &&
        homeTeam != awayTeam  &&
        (isSidmouth(homeTeam) || isSidmouth(awayTeam))) {

        fixture = {
          date: null,
          home: homeTeam,
          away: awayTeam,
          score: null
        }

        ;[left, right] = cell.split(' ')
        if (left != 'Rain') {
          if (isNaN(right)) {
            // These tableCells contain a date
            fixture.date = left + ' ' + right
          }
          else {
            // These tableCells contain a score
            fixture.score = left + ' - ' + right
          }
        }

        // Distinguish between Mens, Ladies and Mixed teams
        fixture.home = substSidmouth(homeTeam, division.format)
        fixture.away = substSidmouth(awayTeam, division.format)

        fixtures.push(fixture)
      }
    }
  }

  return fixtures
}

/*
  Scrapes table data, and stores in an array (rows) of arrays (columns).
  This provides a convenient data format for further processing.
*/
const scrapeDivisionTable = (rows, league, division, teamsInDivision) => {
  console.log('Scraping data for', league, ',', division.format, 'division', division.division)
  console.log('Found', teamsInDivision.length, 'teams in this division')

  let tdCells, parent
  let rowNum, homeTeam, awayTeam
  let cursor, left, right
  let table = [], tableRow

  /*
    Step through the rows for this division.
    The start position for this process would have been previously determined
    by manually invoking the identifyRows function.
  */
  for (let i = 0; i < teamsInDivision.length; i++) {
    try {
      rowNum = division.start + i
      tdCells = rows[rowNum].children.filter(filterTd)
      console.log('tr', rowNum, 'has', tdCells.length, 'td elements')

      homeTeam = getText(tdCells[0])
      tableRow = []
      tableRow.push(homeTeam)

      for (let k = 0; k < teamsInDivision.length; k++) {
        try {
          awayTeam = teamsInDivision[k]
          cursor = 1 + (k * 2)

          /*
            For each fixture, there will be a pair of cells that provide
            either a) a match date, or b) the score.
          */
          left = getText(tdCells[cursor])
          right = getText(tdCells[cursor + 1])

          // Ignore cells when home and away are the same
          if (homeTeam != awayTeam) {
            tableRow.push(left + ' ' + right)
          }
          else {
            tableRow.push(null)
          }
        }
        catch(err) {
          tableRow.push('Error')
          console.log('Error scraping data for', homeTeam, 'v', awayTeam, ':', err)
        }
      }
    }
    catch(err) {
      console.log('Error scraping data:', err)
      break
    }

    table.push(tableRow)
  }

  return table
}

/*
  If the given node represents a td tag, then it's contents are
  returned, otherwise null is returned.
*/
const getText = (node) => {
  if (node && node.type == 'tag' && node.name == 'td') {
    if (node.children && node.children.length > 0) {
      let spaceSquasher = /\s{2,}/gm
      let str = node.children[0].data

      if (! str) {
        return null
      }

      return str.trim().replaceAll(spaceSquasher, ' ')
    }
  }

  return null
}

const isSidmouth = (team) => {
  return team && team.includes(SIDMOUTH)
}

const substSidmouth = (team, format) => {
  if (isSidmouth(team) && team.length > SIDMOUTH.length) {
    let suffix = team.substring(SIDMOUTH.length + 1)
    return `${SIDMOUTH} ${format} ${suffix}`
  }

  return team
}

const filterTd = (node) => {
  return node && node.type == 'tag' && node.name == 'td'
}

const parseDate = (str) => {
  let d = new Date(str)
  
}

const identifyRows = async (obj) => {
  let html = await rp(obj.url)
  let $html = cheerio.load(html)

  const rows = $html(`table tbody tr`)
  let tr, td

  for (let i = 0; i < rows.length; i++) {
    tr = rows[i]

    for (let j = 0; j < tr.children.length; j++) {
        td = tr.children[j]

        // Find the first td in the tr
        if (td.type == 'tag' && td.name == 'td') {
          console.log(`${i}: ${getText(td)}`)
          break
        }
    }
  }

  return 'Finished'
}

/*
  Scrapes fixtures from all divisions in a given league.
*/
const scrapeLeague = async (league) => {
  let html = await rp(league.url)
  let $html = cheerio.load(html)

  const rows = $html(`table tbody tr`)
  let team, division, fixture, tr, td
  let fixtures = [], teamsInDivision, table

  // for each division ...
  for (let i=0; i < league.divisions.length; i++) {
    division = league.divisions[i]
    teamsInDivision = []

    // ... identify the teams in the division
    for (let j = division.start; j <= division.end; j++) {
      teamsInDivision.push(getText(rows[j].children[1]))
    }

    table = scrapeDivisionTable(rows, league.name, division, teamsInDivision)
    fixtures.push(... getFixtures(table, division, teamsInDivision))
  }

  return fixtures
}

const scrape = async (exeterLeagues) => {
  let results = []
  let league, division

  for (let k = 0; k < exeterLeagues.length; k++) {
    results.push(... await scrapeLeague(exeterLeagues[k]))
  }

  console.log('Found total of', results.length, 'fixtures for these leagues')
  return results
}

exports.identify = identifyRows
exports.scrape = scrape
