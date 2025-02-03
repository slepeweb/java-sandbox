<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" %><%@ 
	attribute name="many" required="false" rtexprvalue="true" %>

<!-- standardFormPageHeading.tag -->

<mny:standardFormLabels entity="${entity}" />

<mny:pageHeading heading="${_pageHeading}">
	<mny:standardFormMenu entity="${entity}" many="${many} "/>
</mny:pageHeading>
	