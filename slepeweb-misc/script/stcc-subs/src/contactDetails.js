import React from 'react';
import {Input} from './input.js'
import _data from './data.js'
import './index.css'

class ContactDetails extends React.Component {

  render() {
    const inputs = _data.contactFormElements.map((a) => {
      return (
        <Input type="text" name={a[0]} key={a[0]} label={a[1]} className="row"
          value={this.props.state[a[0]]} onChange={this.props.onChange}
          mandatory={a[2]}
          invalid={this.props.state['v' + a[0]]} />
      )
    })

    return (
      <div id="contactDetails">
        {inputs}
      </div>
    )
  }
}

export default ContactDetails
