<%@ page contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8" 
	session="false"%><%@ 
	taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ 
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
	taglib uri="http://www.springframework.org/tags" prefix="spring" %><%@
	taglib uri="http://www.springframework.org/tags/form" prefix="form"%><%@
	taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
	
<h1>${_account.name}</h1>
<h2>Balance to ${_periodEnd}: ${_balance}</h2>

<c:choose><c:when test="${not empty _transaction_list}">
<table>
	<c:forEach items="${_transaction_list}" var="_trn">
		<tr>
			<td>${_trn.enteredStr}</td>
			<td>${_trn.payee.name}</td>
			<td>${_trn.split ? 'Total' : _trn.category}</td>
			<td>${_trn.amountInPounds}</td>
			<td>${_trn.memo}</td>
			<td>${_trn.balance}</td>
			
			<c:if test="${_trn.split}">
					<c:forEach items="${_trn.splits}" var="_split">
						<tr>
							<td></td>
							<td></td>
							<td>${_split.category}</td>
							<td>${_split.amountInPounds}</td>
							<td>${_split.memo}</td>
							<td></td>
						</tr>
					</c:forEach>
			</c:if>
		</tr>
	</c:forEach>
</table>
</c:when><c:otherwise>
	<h2>No results available</h2>
</c:otherwise></c:choose>