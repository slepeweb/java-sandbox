import React from "react";
import _data from './data.js'
import "./index.css"

function Title(props) {
  return (
    <div className="pageHeader">
      <h1>{_data.furniture.club}</h1>
      <h2>{_data.furniture.title}</h2>
      <h3>{_data.furniture.subtitle}</h3>
    </div>
  )
}

class Hideable extends React.Component {
  render() {
    if (! this.props.hide) {

      return (
        <div className={this.props.className}>
          {this.props.children}
        </div>
      )
    }

    return ''
  }
}

function Heading(props) {
  if (! props.hide) {

    return (
      <div className="row heading">
        {props.children}
      </div>
    )
  }

  return ''
}

function Guidance(props) {
  let clazz = props.className ?
    'guidance ' + props.className : 'guidance'

  return (
    <div className={clazz}>
      {props.children}
    </div>
  )
}

function NavigationButton(props) {
  return <button className={props.className}
    value={props.value} onClick={props.onSubmit}>{props.label}</button>
}

const buttonSupport = (clazz, label, page) => {
  let o = {}
  o.clazz = clazz
  o.label = label
  o.page = page
  return o
}

function Navigation(props) {
  let lastPage = 5
  let reviewPage = 4
  let childDetailsPage = 3

  let next = {}, previous = {}
  let selectedCategoryInvolvesChildren = props.juniors

  // Back button logic
  if (props.page === reviewPage) {
    previous = buttonSupport("back-button", "< Back",
      selectedCategoryInvolvesChildren ? props.page - 1 : props.page - 2)
  }
  else if (props.page > 1 && props.page !== lastPage) {
    previous = buttonSupport("back-button", "< Back",
      selectedCategoryInvolvesChildren ? props.page - 2 : props.page -1)
  }
  else {
    previous = {page: -1}
  }

  // Next button logic
  if (props.page === childDetailsPage - 1) {
    next = buttonSupport("fwd-button", "Next >",
      selectedCategoryInvolvesChildren ? props.page + 1 : props.page + 2)
  }
  else if (props.page === reviewPage) {
    next = buttonSupport("submit-button", "Submit >", props.page + 1)
  }
  else if (props.page < lastPage) {
    next = buttonSupport("fwd-button", "Next >",props.page + 1)
  }
  else {
    next = {page: -1}
  }

  let backButton = previous.page > -1 ?
    <NavigationButton className={previous.clazz}
      value={previous.page}
      label={previous.label}
      onSubmit={props.onSubmit} /> : <p />

  let nextButton = next.page > -1 ?
    <NavigationButton className={next.clazz}
      value={next.page}
      label={next.label}
      onSubmit={props.onSubmit} /> : <p />

  return (
    <div className="navigation">
      {backButton}
      {nextButton}
    </div>
  )
}

function TwoColumnTableRow(props) {
  return (
    <tr>
      <td>{props.label}</td>
      <td className="user-data">{props.value}</td>
    </tr>
  )
}

export {Title, Hideable, Heading, Guidance, Navigation, TwoColumnTableRow}
