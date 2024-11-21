<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<geo:pageLayout type="std">
	<gen:debug><!-- jsp/geo/standard3col.jsp --></gen:debug>
		
	<div class="main standard-3col">
		<div class="leftside">
			<geo:inThisSection />
		</div>
		
		<c:set var="sidebarIsPresent" value="${fn:length(_item.relatedItems) > 0}" />
		<c:set var="mainBodyClass" value="full-width" />
		<c:if test="${sidebarIsPresent}"><c:set var="mainBodyClass" value="nearly-full-width" /></c:if>
		
		<div class="rightside">
			<div class="mainbody ${mainBodyClass}">
				<h2>${_item.fields.title}</h2>
				<div>${_item.fields.bodytext}</div>
				
				<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" />
			</div>
		
			<c:if test="${sidebarIsPresent}">
				<geo:relatedItems />
			</c:if>
		</div>
	</div>
</geo:pageLayout>