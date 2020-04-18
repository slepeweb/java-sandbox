<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
	taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>

<title>slepe web solutions | </title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

<!-- Include Fancytree skin and library, and jquery file-upload -->
<link href="${applicationContextPath}/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<script src="${applicationContextPath}/resources/fancytree/jquery.fancytree.dnd.js" type="text/javascript"></script>

<!-- Initialize the tree when page is loaded -->
<script type="text/javascript">
	// Application context 
	var _ctx = "${applicationContextPath}";
	var _siteId = <c:choose><c:when test="${not empty site}">${site.id}</c:when><c:otherwise>0</c:otherwise></c:choose>;
	var _editingItemId = null;
	
	<c:if test="${not empty editingItem}">
		_editingItemId = ${editingItem.id};
		_siteId = ${editingItem.site.id};
	</c:if>
	
	// Flash messages passed through when window.location is set 
	var _flashMessage = null;
	<c:if test="${not empty _flashMessage}">
		_flashMessage = {};
		_flashMessage.error = ${_flashMessage.error};
		_flashMessage.message = "${_flashMessage.message}";
	</c:if>
	
	var _productTypeId = "${_productTypeId}";
	var _activeTab = "${param.tab}";
</script>

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css" 
	integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns" 
	crossorigin="anonymous">
	
<link rel="stylesheet" href="${applicationContextPath}/resources/css/main.css" type="text/css">
<cmsjs:main />

<cms:extraCSS />
<cms:extraJS />
