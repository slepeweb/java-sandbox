<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<geo:pageLayout type="std">
	<gen:debug><!-- jsp/geo/standard.jsp --></gen:debug>
		
	<div class="main standard-wide">
		<h2>${_item.fields.title}</h2>
		<div>${_item.fields.bodytext}</div>
		
		<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" />
	</div>
</geo:pageLayout>