import React from 'react';
import ContactDetails from './contactDetails.js'
import {MembershipOptions, MembershipGuidance, NumberOfJuniors} from './membershipOptions.js'
import {Title, Hideable, Heading, Guidance, Navigation} from './structural.js'
import {Children} from './childDetails.js'
import _data from './data.js'
import Review from './review.js'
import {Payment} from './payment.js'
import './index.css'


class Form  extends React.Component {
  constructor(props) {
    super(props)

    /*
      Whenever state changes, the root element and all its
      descendants are re-rendered.
    */
    this.state = {
      selectedCategory: 'single',
      numJuniors: 0,
      currentPage: 1,
      countyMember: false,

      // Contact details
      firstname: '', lastname: '',
      address: '', town: '', postcode: '',
      homephone: '', mobile: '',
      email: '',

      // Validation failure message
      vfirstname: '',
      vlastname: '',
      vaddress: '',
      vpostcode: '',
      vhomephone: '',
      vemail: '',

      // Child childDetails
      child1: '', child1dob: '',
      child2: '', child2dob: '',
      child3: '', child3dob: '',
      child4: '', child4dob: '',
    }

    /*
      I think binding is about specifying what 'this' refers to
      within the body of a function, typically callback functions,
      which get passed around as arguments to other functions.
    */
    this.handlePriceChange = this.handlePriceChange.bind(this)
    this.handleNumJuniorsChange = this.handleNumJuniorsChange.bind(this)
    this.handleInputChange = this.handleInputChange.bind(this)
    this.handlePageChange = this.handlePageChange.bind(this)
    this.handleContactValidationAndPageChange = this.handleContactValidationAndPageChange.bind(this)
    this.populateTestData = this.populateTestData.bind(this)
    this.handleSubmission = this.handleSubmission.bind(this)

    /*
      This timer (returned by setTimeout) helps to identify the last
      update in a series, and then act upon it, ignoring all previous updates.
    */
    this.timer = null
  }

  handlePriceChange(e) {
    if (e.target.type !== 'checkbox') {
      this.setState({selectedCategory: e.target.value})
    }
    else {
      // console.log('Checkbox', e.target.name, 'is', e.target.checked)
      this.setState({[e.target.name]: e.target.checked})
    }
  }

  handleNumJuniorsChange(e) {
    this.setState({numJuniors: e.target.value})
  }

  handlePageChange(e) {
    this.setState({currentPage: parseInt(e.target.value)})
  }

  handleSubmission(e) {
    // Call web service to create the pdf
    // Clone the state object, and add some extra data
    const body = {
      url: _data.system.pdfContentUrl,
      club: _data.furniture.club,
      heading: _data.furniture.title,
      subheading: _data.furniture.subtitle,
      membershipCategory: this.identifySelectedCategory()[1],
      firstname: this.state.firstname,
      fullName: this.state.firstname + ' ' + this.state.lastname,
      oneLineAddress: this.state.address + (this.state.town ? ', ' + this.state.town : '') + ', ' + this.state.postcode,
      homephone: this.state.homephone,
      mobile: this.state.mobile,
      email: this.state.email,
      fees: this.calculateTotalPrice(),
      countyMemberYesno: this.state.countyMember ? 'Yes' : 'No',
      childMembers: (() => {
        let a = []
        let n
        for (let i = 1; i <= 4; i++) {
          n = 'child' + i
          if (this.state[n]) {
            a.push(this.state[n] + ', dob ' + this.state[n + 'dob'])
          }
        }
        return a
      })(),
      sortcode: '40-42-02',
      accountno: '71569651',
      secretary: 'george@buttigieg.org.uk',
    }

    fetch(_data.system.pdfMakerUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      mode: 'cors',
      body: JSON.stringify(body),
    })

    this.handlePageChange(e)
  }

  handleContactValidationAndPageChange(e) {
    let valid = true

    if (this.isBlank(this.state.firstname)) {
      this.setState({vfirstname: 'Please enter your first name'})
      valid = false
    }

    if (this.isBlank(this.state.lastname)) {
      this.setState({vlastname: 'Please enter your surname'})
      valid = false
    }

    if (this.isBlank(this.state.address)) {
      this.setState({vaddress: 'Address field is blank'})
      valid = false
    }

    if (this.isBlank(this.state.postcode)) {
      this.setState({vpostcode: 'Postcode field is blank'})
      valid = false
    }

    if (this.isBlank(this.state.homephone) && this.isBlank(this.state.mobile)) {
      this.setState({vhomephone: 'Please provide at least one telephone contact number'})
      valid = false
    }

    if (this.isBlank(this.state.email)) {
      this.setState({vemail: 'An email address is required to send a copy of the completed application'})
      valid = false
    }

    if (valid) {
      this.setState({currentPage: parseInt(e.target.value)})
    }
  }

  handleInputChange(e) {
    // console.log('Setting', e.target.name, 'to', e.target.value)
    this.updateState(e.target.name, e.target.value)
  }

  updateState(key, value) {
    this.setState({[key]: value})
    let v = 'v' + key
    if (this.state[v]) {
      this.setState({[v]: ''})
    }
  }

  isBlank(s) {
    return s == null || typeof s != 'string' || s.trim().length === 0
  }

  identifySelectedCategory() {
    let arr = _data.membershipOptions.filter((a) => {
      return a[0] === this.state.selectedCategory
    })

    return arr[0]
  }

  toPoundsStr(val) {
    return val > 0 ? '£' + val.toFixed(2) : '-£' + (-val).toFixed(2)
  }

  calculateTotalPrice() {
    let priceData = {extras: false}
    let adj

    let a = this.identifySelectedCategory()
    priceData.total = a[2]

    priceData.membership = {
      label: a[1],
      value: a[2],
      valueStr: this.toPoundsStr(a[2]),
    }

    if (this.state.countyMember) {
      adj = -((100 - _data.prices.countyDiscountPercent)/100) * a[2]
      priceData.countyMemberDiscount = {
        label: 'County member discount',
        value: adj,
        valueStr: this.toPoundsStr(adj),
      }
      priceData.total += adj
      priceData.extras = true
    }

    if (a[0] === 'oneparent' && this.state.numJuniors > 0) {
      adj = this.state.numJuniors * _data.prices.extraChildren
      priceData.extraJuniors = {
        label: this.state.numJuniors + ' Juniors',
        number: this.state.numJuniors,
        value: adj,
        valueStr: this.toPoundsStr(adj),
      }
      priceData.total += adj
      priceData.extras = true
    }

    priceData.totalStr = this.toPoundsStr(priceData.total)
    return priceData
  }

  updateTotalPrice() {
    if (this.timer) {
      window.clearTimeout(this.timer)
      this.timer = null
    }

    this.timer = window.setTimeout(() => {
      this.setState({totalPrice: this.calculateTotalPrice().total})
      window.clearTimeout(this.timer)
      this.timer = null
    }, 500)
  }

  componentDidMount() {
    this.updateTotalPrice()
  }

  componentDidUpdate() {
    this.updateTotalPrice()
  }

  populateTestData() {
    if (this.state.currentPage === 1) {
      this.updateState('firstname','George');
      this.updateState('lastname','Buttigieg');
      this.updateState('address','63 Newlands Road');
      this.updateState('town','Sidmouth');
      this.updateState('postcode','EX10 9NN');
      this.updateState('homephone','');
      this.updateState('mobile','0755 777 0817');
      this.updateState('email','george@buttigieg.org.uk');
    }
  }

  render() {
    return (
      <>
      <div id="form">
        {/* Page title and headings */}
        <Title />

        {/* Page 1: Contact details */}
        <Hideable hide={this.state.currentPage !== 1}>
          <Heading>Contact details</Heading>
          <Guidance>Please provide your contact details; fields marked '*' are mandatory.</Guidance>
          <ContactDetails state={this.state} onChange={this.handleInputChange} />
          <Navigation page={1} onSubmit={this.handleContactValidationAndPageChange} />
        </Hideable>

        {/* Page 2: Membership categories */}
        <Hideable hide={this.state.currentPage !== 2}>
          <Heading>Membership types</Heading>

          <MembershipGuidance />

          <MembershipOptions label="Choose type of membership" className="row radio-group"
            onChange={this.handlePriceChange}
            selectedCategory={this.state.selectedCategory}
            countyMember={this.state.countyMember} />

          <NumberOfJuniors selectedCategory={this.state.selectedCategory}
            value={this.state.numJuniors} onChange={this.handleNumJuniorsChange} />

          <Heading>Total payable is {this.calculateTotalPrice().totalStr}</Heading>
          <Navigation page={2} onSubmit={this.handlePageChange}
            juniors={this.identifySelectedCategory()[3]} />
        </Hideable>

        {/* Page 3: Child details */}
        <Hideable className="spacer child-details-wrapper" hide={this.state.currentPage !== 3}>
          <Children state={this.state} onChange={this.handleInputChange}
            membership={this.identifySelectedCategory()[1]} />
          <Navigation page={3} onSubmit={this.handlePageChange} />
        </Hideable>

        {/* Page 4: Review */}
        <Hideable className="spacer" hide={this.state.currentPage !== 4}>
          <Review state={this.state}
            membership={this.identifySelectedCategory()}
            fees={this.calculateTotalPrice()} />

          <Guidance>
            In submitting this membership renewal form, you agree to take part in the activities of SCT&CC, you confirm that you
            have read, or have been made aware of, the Club Rules, the Club Privacy Policy and Sidmouth Tennis Club’s
            policies, and you consent to the Club photographing or videoing of your involvement in tennis in line with the club’s
            policy.
          </Guidance>

          <Navigation page={4} onSubmit={this.handleSubmission}
            juniors={this.identifySelectedCategory()[3]} />
        </Hideable>

        {/* Page 5: Payment */}
        <Hideable className="spacer signature-wrapper" hide={this.state.currentPage !== 5}>
          <Payment state={this.state} total={this.calculateTotalPrice().totalStr} />
          <Navigation page={5} onSubmit={this.handlePageChange} />
        </Hideable>

      </div>

      <p id="backdoor" onClick={this.populateTestData}>x</p>
      </>
    )
  }
}

export default Form
