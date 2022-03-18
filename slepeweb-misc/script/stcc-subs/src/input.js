import React from "react";
import "./index.css"

function Wrapper(props) {
  return (
    <div className={props.className}>
      <label>{props.label}</label>
      {props.children}
    </div>
  )
}

function Input(props) {
  let clazz = props.invalid ? 'error' : ''
  let label = props.mandatory ? props.label + ' *' : props.label

  return (
    <Wrapper className={props.className} label={label}>
      <input className={clazz} type={props.type} name={props.name} value={props.value}
        onChange={props.onChange} />
      <span className="error">{props.invalid}</span>
    </Wrapper>
  )
}

function PricedInput(props) {
  return (
    <Wrapper className={props.className} label={props.label}>
      <input type={props.type} name={props.name} value={props.value}
        onChange={props.onChange} checked={props.checked} />
      <span className="price">&pound;{props.price}</span>
    </Wrapper>
  )
}

function OverallDiscount(props) {
  return (
    <Wrapper className={props.className} label={props.label}>
      <input type="checkbox" name={props.name}
        onChange={props.onChange} checked={props.checked} />
    </Wrapper>
  )
}

function NumericInput(props) {
  return (
    <Wrapper className={props.className} label={props.label}>
      <input type="number" min={props.min} max={props.max} name={props.name}
        value={props.value}
        onChange={props.onChange} />
    </Wrapper>
  )
}

function ChildDetailInputs(props) {
  return (
    <div className="row">
      <h3>{props.label}</h3>
      <div className="child-details">
        <label className="first">Name</label>
        <input type="text" name={props.name} value={props.nameValue}
          onChange={props.onChange} className="first" />
        <label className="second">DOB</label>
        <input type="text" name={props.name + 'dob'} value={props.dobValue}
          onChange={props.onChange} className="second" />
      </div>
    </div>
  )
}


export {Input, PricedInput, NumericInput, OverallDiscount, ChildDetailInputs}
