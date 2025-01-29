<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<!-- transandschedform/ids.tag -->

<c:if test="${_formMode eq 'update'}">
	<mny:tableRow heading="Id" trclass="opaque50">
		<input id="identifier" type="text" readonly name="identifier" value="${entity.id}" />
	</mny:tableRow>
	
	<c:if test="${entity.origId gt 0}">
		<mny:tableRow heading="Original id" trclass="opaque50">
			<input id="origid" type="text" readonly name="origid" value="${entity.origId}" />
		</mny:tableRow>
	</c:if>
</c:if>
