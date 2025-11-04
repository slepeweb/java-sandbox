<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/geo/standard3col.jsp --></gen:debug>

<geo:pageLayout type="std">
		
	<div class="main standard-3col">
		<div class="leftside">
			<geo:inThisSection />
			<geo:relatedItems />
		</div>
		
		<%-- Sidebar is out of action, for the moment at least --%>
		<c:set var="sidebarIsPresent" value="${0 gt 1}" />
		<c:set var="mainBodyClass" value="full-width" />
		<c:if test="${sidebarIsPresent}"><c:set var="mainBodyClass" value="nearly-full-width" /></c:if>
		
		<div class="rightside">
			<div class="mainbody ${mainBodyClass}">
				<geo:title />
				<gen:bodyFieldMagic />
			</div>
		
			<c:if test="${sidebarIsPresent}">
				<%-- Sidebar is out of action, for the moment at least --%>			
			</c:if>
		</div>
	</div>
	
</geo:pageLayout>