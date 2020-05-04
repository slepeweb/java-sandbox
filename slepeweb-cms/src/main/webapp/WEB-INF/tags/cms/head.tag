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

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

<!-- Include Fancytree skin and library, and jquery file-upload -->
<link href="${applicationContextPath}/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.dnd.js" type="text/javascript"></script>

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css" 
	integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns" 
	crossorigin="anonymous">
	
<link rel="stylesheet" href="${applicationContextPath}/resources/css/main.css" type="text/css">

<c:if test="${loadjs}">
	<script src="${applicationContextPath}/rest/js" type="text/javascript"></script>
	<edit:init />
</c:if>

<cms:extraCSS />
<cms:extraJS />
