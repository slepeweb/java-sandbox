import React from "react";
import {Heading, Guidance} from './structural.js'
import "./index.css"

function Payment(props) {
  return (
    <>
      <Heading>Application Complete</Heading>
      <Guidance>
        <p>Thank you for submitting your renewal application. A copy has been
          emailed to <span className="user-data">{props.state.email}</span>, for reference.</p>

        <p>This total fee is
          <span className="emphasize"> {props.total}</span>,
            and is now payable online or by cheque if preferred, to:</p>

        <table id="bank-details">
          <tbody>
            <tr><td>Payee</td><td className="emphasize">Sidmouth Cricket, Tennis & Croquet Club</td></tr>
            <tr><td>sort code</td><td className="emphasize">40-42-02</td></tr>
            <tr><td>account no.</td><td className="emphasize">71569651 (HSBC)</td></tr>
          </tbody>
        </table>

        <p>Cheques should be handed to Marlene Maynard, the membership secretary.</p>
      </Guidance>
    </>
  )
}

export {Payment}
