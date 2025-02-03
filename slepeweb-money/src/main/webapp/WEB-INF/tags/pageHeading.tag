<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %>

<mny:flash />

<div class="page-heading">
	<div>
		<h2>${heading}</h2>
		<c:if test="${not empty param.flash}">
			<div class="flash ${_flashType}">${_flashMessage}</div>
		</c:if>
	</div>
	
	<div class="menu">
		<jsp:doBody />
	</div>
</div>
	