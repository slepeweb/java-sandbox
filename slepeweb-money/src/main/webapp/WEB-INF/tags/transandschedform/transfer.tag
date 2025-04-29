<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="istransfer" required="true" rtexprvalue="true" type="java.lang.Boolean" %><%@ 
	attribute name="mirror" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Account" %>

<!-- transandschedform/transfer.tag -->

<mny:tableRow heading="Transfer a/c" trclass="transfer">
	<div class="${_hiddenDivClass}">
		<select id="xferaccount" name="xferaccount">
	   	<option value="">Choose ...</option>
	   	<c:forEach items="${_allAccounts}" var="_a">
	   		<option value="${_a.id}" <c:if test="${istransfer and _a.id eq mirror.id}">selected</c:if>>${_a.name}</option>
	   	</c:forEach>
	  </select>
	</div>
	
	<c:if test="${_transaction.partReconciled}">
		<input type="text" class="readonly" name="xferaccountname" value="${mirror.name}" readonly />
		<input type="hidden" name="xferaccount" value="${mirror.id}" />			
	</c:if>
</mny:tableRow>