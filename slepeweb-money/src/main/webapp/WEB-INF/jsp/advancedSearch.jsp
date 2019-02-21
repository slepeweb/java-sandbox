<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />

<mny:standardLayout>

	<h2>Advanced search	<c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>
			
	<mny:advancedSearchForm />
	<mny:advancedSearchResults />
	
</mny:standardLayout>
