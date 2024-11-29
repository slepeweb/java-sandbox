<%@ tag %><%@ 
	attribute name="loadjs" required="true" rtexprvalue="true" type="java.lang.Boolean" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<title>slepe web solutions | </title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<%-- 
	Key requirement for responsive layout on mobile phone. My phone has 2280 x 1080 pixels wide,
	and without this meta element, it would attempt to fit the desktop layout in both landscape and portrait mode.
	#responsive.
 --%>
<meta name="viewport" content="width=device-width, initial-scale=1.0" /> 
<meta name="description" content="" />
<meta name="keywords" content="" />

<!-- jQuery code -->
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>


<!-- Include Fancytree skin and library, and jquery file-upload -->
<link href="${applicationContextPath}/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<script src="https://kit.fontawesome.com/130710974e.js" crossorigin="anonymous"></script>

<%-- Quill Rich Text Editor 
<script src="${applicationContextPath}/resources/js/quill-2.0.2.js"></script>
<link href="${applicationContextPath}/resources/css/quill-2.0.2-snow.css" rel="stylesheet">
<script src="${applicationContextPath}/resources/js/quill-better-table-1.2.10.js"></script>
<link href="${applicationContextPath}/resources/css/quill-better-table-1.2.10.css" rel="stylesheet">
--%>

<link rel="stylesheet" href="${applicationContextPath}/resources/css/main.css" type="text/css">

<c:if test="${loadjs}">
	<edit:init />
	<script src="${applicationContextPath}/rest/js" type="text/javascript"></script>
</c:if>

<cms:extraCSS />
<cms:extraJS />
