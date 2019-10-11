<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<title>money | ${_page.title}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="${_ctxPath}/resources/css/normalize.css" />
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" type="text/css">
<link href="${_ctxPath}/resources/css/colorbox.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${_ctxPath}/resources/css/money.css" />

<script src="${_ctxPath}/resources/js/jquery-1.12.4.min.js"></script>
<script src="${_ctxPath}/resources/js/jquery-ui-1.12.1.min.js"></script>
<!-- <script src="//code.jquery.com/jquery-1.12.4.js"></script> -->
<!-- <script src="//code.jquery.com/ui/1.12.1/jquery-ui.js"></script> -->

<!-- <script src="${_ctxPath}/resources/js/jquery.colorbox-min.js"></script> -->
<!-- <script src="/resources/js/colorbox-impl.js"></script> -->

<script src="${_ctxPath}/resources/js/money.js"></script>
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css" 
	integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns" 
	crossorigin="anonymous">
	
<c:if test="${not empty _extraCss}">
	<style>
		${_extraCss}
	</style>
</c:if>

<script>
	var webContext = "${_ctxPath}";
</script>