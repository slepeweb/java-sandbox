import React from 'react';
import {ChildDetailInputs} from './input.js'
import {Heading, Guidance} from './structural.js'
import './index.css'

function Children(props) {
  const list = [1, 2, 3, 4].map((n) => {
    let key = 'child' + n

    return (
      <ChildDetailInputs label={'Child #' + n}
        type="text" name={key} key={key}
        nameValue={props.state[key]}
        dobValue={props.state[key + 'dob']}
        onChange={props.onChange} />
      )
  })

  return (
    <>
      <Heading>Junior Members</Heading>
      <Guidance>Please provide names and ages of children who
        are part of this <span className="user-data">{props.membership} </span>
        membership application.</Guidance>

      {list}

      <Guidance className="spacer">
        Please inform the coach(es) of any relevant medical condition that
        your child/children may have. A copy of the Club Safeguarding,
        Privacy and Club Policies and Club Rules are available on
        the <a href="https://clubspark.lta.org.uk/sidmouth">Sidmouth Tennis Club Website</a>.
      </Guidance>
    </>
  )
}

export {Children}
