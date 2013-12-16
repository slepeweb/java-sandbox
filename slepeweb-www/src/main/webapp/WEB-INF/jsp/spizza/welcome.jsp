<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2>Welcome to Spizza!!!</h2>
<p>This is a Spring Webflow demonstration, based upon an example in the book by Craig Walls, titled 'Spring in Action'.
	A reasonably complex series of forms and user interactions are tied together in the Spring Webflow framework, to
	simulate an online pizza ordering service. It still needs some work ...</p>
	
<form:form>
	<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}" />
	<label for="phoneNumber">Your phone number: </label>
	<input type="text" name="phoneNumber" />
	<input type="submit" name="_eventId_phoneEntered" class="button" value="Submit" />
</form:form>
