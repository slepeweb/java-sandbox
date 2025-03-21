<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="/resources/css/normalize.css" type="text/css" />
<link rel="stylesheet" href="/resources/css/site.css" type="text/css" />

<script src="//ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.14.1/themes/smoothness/jquery-ui.css">
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.14.1/jquery-ui.min.js"></script>

<script src="https://kit.fontawesome.com/130710974e.js" crossorigin="anonymous"></script>

<script>
	let _site = {
		support: {},
		origId: ${_item.origId},
		isSecured: ${_item.site.secured ? 'true' : 'false'},
	}
</script>

<script src="/resources/js/site.js"></script>

<gen:extraCSS />
<gen:extraInpageCSS />
<gen:extraJS />
<gen:extraInpageJS />

<jsp:doBody />
