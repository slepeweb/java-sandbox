<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2>Welcome to Spizza!!!</h2>
<form:form>
	<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}" />
	<input type="text" name="phoneNumber" />
	<br />
	<input type="submit" name="_eventId_phoneEntered" value="Lookup Customer" />
</form:form>
