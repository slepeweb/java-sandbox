<%@ tag %><%@ 
	attribute name="owner" required="true" rtexprvalue="true" type="com.slepeweb.site.model.ComponentContainer" %><%@ 
	attribute name="site" required="true" rtexprvalue="true" %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>
	
<c:forEach items="${owner.components}" var="comp">
	<gen:insertComponent site="${site}" component="${comp}" />
</c:forEach>