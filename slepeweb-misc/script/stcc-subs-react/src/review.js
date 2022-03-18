import React from "react";
import {Heading, Guidance, TwoColumnTableRow} from './structural.js'
import _data from './data.js'
import "./index.css"

function Review(props) {

  const contactDetails = _data.contactFormElements.map((a) => {
    return (
      <TwoColumnTableRow key={a[0]} label={a[1]} value={props.state[a[0]]} />
    )
  })

  let juniorRows = null

  if (props.membership[3]) {
    let juniors = []
    let id

    for (let i = 1; i <= 4; i++) {
      id = 'child' + i
      if (props.state[id]) {
        juniors.push([i, props.state[id], props.state[id + 'dob']])
      }
    }

    juniorRows = juniors.map((a) => {
        return (
          <TwoColumnTableRow key={a[0]} label={'Child #' + a[0]} value={a[1] + ', dob ' + a[2]} />
        )
    })
  }

  return (
    <>
      <Heading>Review</Heading>
      <Guidance>
        Please check the information you have provided, and once satisfied,
        submit the form.
      </Guidance>

      <table id="review"><tbody>

        {contactDetails}

        <TwoColumnTableRow key="membershiptype" label="Membership category"
          value={props.membership[1]} />
        <TwoColumnTableRow key="countymember" label="County member?"
          value={props.state.countyMember ? "Yes" : "No"} />

        {juniorRows}

        <TwoColumnTableRow key="totalfee" label="Total fee payable"
          value={props.fees.totalStr} />
      </tbody></table>

      <FeeBreakdown className="fee-breakdown" fees={props.fees} />
    </>
  )
}

function FeeBreakdown(props) {
  if (! props.fees.extraJuniors && ! props.fees.countyMemberDiscount) {
    return ''
  }

  let rows = [[props.fees.membership.label, props.fees.membership.valueStr]]

  if (props.fees.countyMemberDiscount) {
    rows.push(['County member discount', props.fees.countyMemberDiscount.valueStr])
  }

  if (props.fees.extraJuniors) {
    rows.push([props.fees.extraJuniors.label, props.fees.extraJuniors.valueStr])
  }

  rows.push(['Total', props.fees.totalStr])

  let tableRows = rows.map((val, index) => {
    return <TwoColumnTableRow key={val[0]} label={val[0]} value={val[1]} />
  })

  return (
    <>
    <Heading>Fee breakdown:</Heading>
    <table>
      <tbody>
        {tableRows}
      </tbody>
    </table>
    </>
  )
}

export default Review
