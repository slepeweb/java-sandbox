<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<h1>Test results</h1>
<c:choose><c:when test="${testResults.success}">
	<c:set var="cssClass" value="pass" /><c:set var="heading" value="All tests have been executed" /></c:when><c:otherwise>
	<c:set var="cssClass" value="fail" /><c:set var="heading" value="Not all tests could be executed" /></c:otherwise></c:choose>

<h2 class="${cssClass}">${heading}</h2>
<table border="1">
	<tr><th>Id</th><th align="left">Title</th><th>Result</th><th>Notes</th></tr>
	<c:forEach items="${testResults.results}" var="result">
		<tr><td>${result.id}</td><td>${result.title}</td>
		<td class="<c:choose><c:when test="${result.result eq 'Fail'}">fail</c:when><c:otherwise>pass</c:otherwise></c:choose>">${result.result}</td>
		<td>${result.notes}</td></tr>
	</c:forEach>
</table>
