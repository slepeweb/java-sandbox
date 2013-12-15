<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2>Delivery Unavailable</h2>
<p>The address is outside of our delivery area. You may
still place the order, but you will need to pick it up
yourself.</p>
<a href="${flowExecutionUrl}&_eventId=accept">
Continue, I'll pick up the order</a> |
<a href="${flowExecutionUrl}&_eventId=cancel">Never mind</a>
