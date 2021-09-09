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
	
	// Function to define a unique key for a db record
	toKey(owner, partyid) {
		return `${owner}-${partyid}`
	}
	
	// Function to upload data from the specified spreadsheet
	upload(u, fpath) {
		var owner = u.username
		
		// Beware!! 'this' does NOT represent an instance of PwdDatabase object
		// when you are in the body of a callback function.
		var pwdb = this
		
		var promise = new Promise((resolve, reject) => {
			/* 
				First clear out existing records FOR THIS USER from the db.
				NOTE that if the user uploads an invalid file (ie an xlsx spreadsheet,
				with the data correctly laid out on the first worksheet), then this
				operation will leave the user with no records in the database. However,
				since the xlsx document is the 'master', it's up to the user to maintain
				its validity.
			*/
			this.ds.remove({owner: owner}, {multi: true}, function(err, num) {
				log.info(sc, `Removed ${num} entries from the password database for user ${owner}`)
			
				// Now load the contents from the xlsx file
				var wb = new excel.Workbook()
				var doc, i
				var count = 0
			
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
	
								if (! doc.username || doc.username == '') {
									doc.username = u.defaultlogin
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
								count++
							}
						}
					})
					
					pwdb.logCount('No. records loaded into password db')
					resolve(count)
					
				}).catch((err) => {
					var msg = `Problem loading file [${fpath}]: ${err}`
					log.error(sc, msg)
					reject(msg)
				})
				
				/*
				if (count == 0) {
					reject('No records added - check file is correct type, and formatted correctly')
				}
				*/
			})
		})
		
		return promise
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