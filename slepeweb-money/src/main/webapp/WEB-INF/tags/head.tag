<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<title>money | ${_page.title}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="${_ctxPath}/resources/css/normalize.css" />
<!-- <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" type="text/css"> -->
<!-- <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" type="text/css"> -->

<%-- <script src="${_ctxPath}/resources/js/jquery-1.12.4.min.js"></script> --%>
<%-- <script src="${_ctxPath}/resources/js/jquery-ui-1.12.1.min.js"></script> --%>
<script src="//ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.14.1/themes/smoothness/jquery-ui.css">
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.14.1/jquery-ui.min.js"></script>
<script src="https://kit.fontawesome.com/130710974e.js" crossorigin="anonymous"></script>

<link rel="stylesheet" href="${_ctxPath}/resources/css/money.css" />

<script>
	var webContext = '${_ctxPath}';
</script>

<script src="${_ctxPath}/resources/js/money.js"></script>

<c:if test="${not empty _extraJs}">
	<c:forTokens items="${_extraJs}" delims="," var="filename">
		<script src="${_ctxPath}/resources/js/${filename}"></script>
	</c:forTokens>
</c:if>

<c:if test="${not empty _extraCss}">
	<c:forTokens items="${_extraCss}" delims="," var="filename">
		<link rel="stylesheet" href="${_ctxPath}/resources/css/${filename}" />
	</c:forTokens>
</c:if>

<c:if test="${not empty _extraInPageCss}">
	<style>
		${_extraInPageCss}
	</style>
</c:if>

<c:if test="${not empty _extraInPageJs}">
	<script>
		${_extraInPageJs}
	</script>
</c:if>
