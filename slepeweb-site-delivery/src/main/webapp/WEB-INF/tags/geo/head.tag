<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<title>buttigieg stuff</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width">
<c:if test="${not empty _item.fields.metadescription}"><meta name="description" content="${_item.fields.metadescription}" /></c:if>
<c:if test="${not empty _item.fields.metatitle}"><meta name="title" content="${_item.fields.metatitle}" /></c:if>

<link rel="stylesheet" href="/resources/pho/css/jquery-ui.min.css" type="text/css">
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css" type="text/css">
<link rel="stylesheet" href="/resources/geo/css/main.css" type="text/css">
<link rel="stylesheet" href="/resources/geo/css/nav.css" type="text/css">

<script 
	src="https://code.jquery.com/jquery-3.6.0.min.js" 
	integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" 
	crossorigin="anonymous"></script>

<script
	src="https://code.jquery.com/ui/1.13.1/jquery-ui.min.js"
	integrity="sha256-eTyxS0rkjpLEo16uXTS0uVCS4815lc40K2iVpWDvdSY="
	crossorigin="anonymous"></script>
	
<script src="https://kit.fontawesome.com/130710974e.js" crossorigin="anonymous"></script>

<script src="/resources/geo/js/nav.js"></script>
	
<gen:extraCSS />
<gen:extraInpageCSS />
<gen:extraJS />
<gen:extraInpageJS />
