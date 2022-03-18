import React from 'react';
import {Hideable} from './structural.js'
import {PricedInput, NumericInput, OverallDiscount} from './input.js'
import _data from './data.js'
import {Heading, Guidance} from './structural.js'
import './index.css'

function MembershipGuidance(props) {
    return (
      <Guidance>
        <p>Now choose the membership category that applies to you, taking into
          account the following notes:
        </p>
        <ul>
          <li>'Family' membership is for two parents, and children up to 18 years.</li>
          <li>If playing 2 sports in the club, you pay highest fee, plus £{_data.prices.tennisSecondSport} for
            second sport . In the case of children, please contact the membership secretary.</li>
        </ul>
      </Guidance>
    )
}

function MembershipOptions(props) {
  const list = _data.membershipOptions.map((k) => {
    return (
      <Category label={k[1]}
        key={k[0]} value={k[0]}
        price={_data.prices[k[0]]}
        checked={props.selectedCategory === k[0]}
        onChange={props.onChange} />
    )
  })

    return (
      <>
      <Heading>Membership options</Heading>
      <div className={props.className}>
        <div id="categories">
          {list}
        </div>
      </div>

      <Guidance className="spacer">
        Playing members living more than 50 miles from the ground
          get a 50% reduction, but only for their first sport.
      </Guidance>

      <OverallDiscount label="County member?"
        className="row checkbox" name="countyMember"
        key="countyMember"
        checked={props.countyMember}
        onChange={props.onChange} />
      </>
    )
}

class Category extends React.Component {
  render() {
    return (
      <PricedInput className="radio" type="radio" name="category" price={this.props.price}
        label={this.props.label} key={this.props.value} value={this.props.value}
        checked={this.props.checked}
        onChange={this.props.onChange} />
    )
  }
}

function NumberOfJuniors(props) {
  return (
    <Hideable className="spacer" hide={props.selectedCategory !== 'oneparent'}>
      <Heading>One playing parent</Heading>
      <Guidance>Please specify the number of juniors accompanying the parent - there is
        an additional cost of &pound;10 per junior.</Guidance>
      <NumericInput name="numJuniors" min="0" max="9" defaultValue="0" label="Number of juniors"
        className="row" onChange={props.onChange} value={props.value} />
    </Hideable>
  )
}


export {Category, MembershipOptions, MembershipGuidance, NumberOfJuniors}
