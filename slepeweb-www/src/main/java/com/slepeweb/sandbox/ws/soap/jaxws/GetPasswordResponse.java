
package com.slepeweb.sandbox.ws.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getPasswordResponse", namespace = "http://soap.ws.sandbox.slepeweb.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPasswordResponse", namespace = "http://soap.ws.sandbox.slepeweb.com/")
public class GetPasswordResponse {

    @XmlElement(name = "return", namespace = "")
    private com.slepeweb.sandbox.ws.soap.PasswordBean _return;

    /**
     * 
     * @return
     *     returns PasswordBean
     */
    public com.slepeweb.sandbox.ws.soap.PasswordBean getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(com.slepeweb.sandbox.ws.soap.PasswordBean _return) {
        this._return = _return;
    }

}
