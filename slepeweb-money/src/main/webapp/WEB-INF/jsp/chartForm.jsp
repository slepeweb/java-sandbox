<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- chartForm.jsp -->

<c:set var="_extraJs" scope="request" value="chart.js,search.js,minorcats.js" />

<mny:flash />

<c:choose><c:when test="${_formMode eq 'create'}">
	<c:set var="_pageHeading" value="Create new chart" scope="request" />
	<c:set var="_formActionUrl" scope="request">/chart/save</c:set>
</c:when><c:when test="${_formMode eq 'update'}">
	<c:set var="_pageHeading" value="Update chart" scope="request" />
	<c:set var="_formActionUrl" scope="request">/chart/save/${_ss.id }</c:set>
</c:when></c:choose>

<mny:standardLayout>
	
	<h2 class="inline-block">${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
	<mny:chartForm />	

	<mny:entityDeletionDialog entity="chart" mode="${_formMode}" id="${_ss.id}"/>

</mny:standardLayout>