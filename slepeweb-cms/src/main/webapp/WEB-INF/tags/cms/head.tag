<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<title>slepe web solutions | </title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link rel="stylesheet" href="${applicationContextPath}/resources/css/main.css" type="text/css">

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

<!-- Include Fancytree skin and library -->
<link href="${applicationContextPath}/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.dnd.js" type="text/javascript"></script>
<!-- Initialize the tree when page is loaded -->
<script type="text/javascript">
	// Application context 
	var _ctx = "${applicationContextPath}";
	var _editingItemId = null;
	<c:if test="${not empty editingItem}">
		_editingItemId = ${editingItem.id};
	</c:if>
</script>
<script src="${applicationContextPath}/resources/js/editor.js" type="text/javascript"></script>

<cms:extraCSS />
<cms:extraJS />
