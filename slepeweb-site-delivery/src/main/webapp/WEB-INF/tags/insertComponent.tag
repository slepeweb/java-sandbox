<%@ tag %><%@ 
	attribute name="component" required="true" rtexprvalue="true" type="com.slepeweb.site.model.Component" %><%@ 
	attribute name="site" required="true" rtexprvalue="true" %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>
	
<c:set var="_comp" value="${component}" scope="request" />
<c:set var="_site" value="${site}" scope="request" />
<jsp:include page="/WEB-INF/jsp/${site}/component/${component.type}.jsp" />