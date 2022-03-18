const _data = {
  system: {
    deployment: 'dev',
  },
  furniture: {
    club: 'Sidmouth Cricket, Tennis & Croquet Club',
    title: 'Tennis Section - Membership renewal form, 2022/23',
    subtitle: 'Subscriptions are due from 1st April 2022'
  },
  prices: {
    single: 146,
    under21w: 65,
    under21s: 42,
    junior: 29,
    mini: 6,
    family: 294,
    oneparent: 143,
    social: 30,
    tennisSecondSport: 43,
    extraChildren: 10,
    countyDiscountPercent: 50,
  },
  countyMemberDiscount: 0.5,
  contactFormElements: [
    ['firstname', 'First name(s)', true],
    ['lastname', 'Last name', true],
    ['address', 'Address', true],
    ['town', 'Town'],
    ['postcode', 'Postcode', true],
    ['homephone', 'Home phone'],
    ['mobile', 'Mobile'],
    ['email', 'Email', true]
  ],
}

_data.membershipOptions = [
  ['single', 'Single adult', _data.prices.single],
  ['under21w', 'Under 21 (working adult)', _data.prices.under21w],
  ['under21s', 'Under 21 (student)', _data.prices.under21s],
  ['junior', 'Junior (9-18)', _data.prices.junior, true],
  ['mini', 'Mini (up to 8 years)', _data.prices.mini, true],
  ['family', 'Family', _data.prices.family, true],
  ['oneparent', 'One playing parent', _data.prices.oneparent, true],
  ['social', 'Social', _data.prices.social],
  ['tennisSecondSport', 'Tennis is second sport', _data.prices.tennisSecondSport]
]

if (_data.system.deployment === 'dev') {
  // Development urls
  _data.system.pdfContentUrl = 'http://localhost:3010/subs/page'
  _data.system.pdfMakerUrl = 'http://localhost:3010/subs/pdf'
}
else {
  // Production urls
  _data.system.pdfContentUrl = 'http://subs.buttigieg.org.uk/subs/page'
  _data.system.pdfMakerUrl = 'http://subs.buttigieg.org.uk/subs/pdf'
}

export default _data
