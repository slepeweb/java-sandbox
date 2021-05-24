const excel = require('exceljs')
const Datastore = require('nedb')

const sc = require('path').basename(__filename)
const log = require('./slepeweb-modules/logger').instance


const trim = (s) => {
	var notNull = s == null ? '' : s
	if (typeof notNull == 'string') {
		return notNull.trim()
	}
	return notNull
}


const preparePartyId = (s) => {
	// Lowercase and remove spaces
	return s.trim().toLowerCase().replace(/\s/, '')
}
	
class PwdDatabase {
	constructor() {
		this.ds = new Datastore({
			filename: './data.db', 
			autoload: true 
		})
		
		this.ds.ensureIndex({
			fieldName: 'key',
			unique: true
		})
		
		this.logCount('No. records in password db')
	}
	
	logCount(msg) {
		this.ds.count({}, function(err, num) {
			log.info(sc, `${msg}: ${num}`)
		})
	}
	
	toKey(owner, partyid) {
		return `${owner}-${partyid}`
	}
	
	upload(owner, fpath) {
		var pwdb = this
		
		// First clear out existing records from the db
		this.ds.remove({}, {multi: true}, function(err, num) {
			log.info(sc, `Removed ${num} entries from the password database`)
		
			// Now load the contents from the xlsx file
			var wb = new excel.Workbook()
			var doc, i
		
			wb.xlsx.readFile(fpath).then(() => {
				// We're interested in the first worksheet
				wb.worksheets[0].eachRow((row, index) => {
					if (index > 1) {
						i = 1
						doc = {
							owner: owner,
							company: trim(row.getCell(i++).value),
							partyid: trim(row.getCell(i++).value),
							mask: trim(row.getCell(i++).value),
							username: trim(row.getCell(i++).value),
							password: trim(row.getCell(i++).value),
							maxchars: parseInt(row.getCell(i++).value),
							memorable: trim(row.getCell(i++).value),
							website: trim(row.getCell(i++).value),
							notes: trim(row.getCell(i++).value),
						}
						
						if (doc.company) {
							// This is a unique key, for indexing
							doc.key = pwdb.toKey(owner, doc.company)						
							
							if (! doc.partyid) {
								doc.partyid = doc.company
							}
							doc.partyid = preparePartyId(doc.partyid)

							if (! doc.username) {
								doc.username = 'george@buttigieg.org.uk'
							}
							
							if (doc.memorable) {
								// Combine memorables with notes
								doc.notes = doc.memorable + (doc.notes ? '\n' + doc.notes : '')
							}
							
							if (doc.website) {
								// Combine website with notes
								doc.notes = doc.website + (doc.notes ? '\n' + doc.notes : '')
							}
							
							// Noticed that the first mask value in the spreadsheet is, for some reason,
							// being interpreted as a hyperlink object!
							if (doc.mask && typeof doc.mask == 'object') {
								if (doc.mask.text) {
									doc.mask = doc.mask.text
									log.info(sc, `Converted mask data [${doc.company}/${doc.mask}]`)
								}
							}
							
							pwdb.ds.insert(doc)
						}
					}
				})
				
				pwdb.logCount('No. records loaded into password db')
			})
		})
	}
	
	findOne(owner, company) {
		var pwdb = this
		return new Promise((resolve, reject) => {		
			this.ds.findOne({key: pwdb.toKey(owner, company)}, (err, doc) => {
				err ? reject(err) : resolve(doc)
			})
		})
	}

	find(owner, company) {
		var pwdb = this
		return new Promise((resolve, reject) => {		
			this.ds.find({key: pwdb.toKey(owner, company)}).sort({company: 1}).exec((err, docs) => {
				err ? reject(err) : resolve(docs)
			})
		})
	}
	
	findAll(owner) {
		return new Promise((resolve, reject) => {		
			this.ds.find({owner: owner}).projection({company:1, _id:0}).sort({company: 1}).exec((err, docs) => {
				err ? reject(err) : resolve(docs)
			})
		})
	}
}

const db = new PwdDatabase()
module.exports = db