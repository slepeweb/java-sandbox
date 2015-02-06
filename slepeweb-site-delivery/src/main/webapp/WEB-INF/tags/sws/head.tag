<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<title>slepe web solutions | ${_page.metaTitle}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<c:if test="${not empty _item.fields.metadescription}"><meta name="description" content="${_item.fields.metadescription}" /></c:if>
<!--[if lte IE 8]><script src="/resources/sws/js/html5shiv.js"></script><![endif]-->
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" type="text/css">
<link href="http://fonts.googleapis.com/css?family=Noto+Sans:400,700,400italic" rel="stylesheet" type="text/css">
<link href="/resources/sws/css/colorbox.css" rel="stylesheet" type="text/css">
<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script src="/resources/sws/js/skel.min.js"></script>
<script src="/resources/sws/js/skel-layers.min.js"></script>
<script src="/resources/sws/js/init.js"></script>
<script src="/resources/sws/js/jquery.colorbox-min.js"></script>

<sw:extraCSS />
<sw:extraJS />

<noscript>
	<link rel="stylesheet" href="/resources/sws/css/skel.css" />
	<link rel="stylesheet" href="/resources/sws/css/style.css" />
	<link rel="stylesheet" href="/resources/sws/css/style-xlarge.css" />
</noscript>

<%-- _requestPath is set in ResponseHeaderFilter --%>
<c:if test="${_item.path eq '/about' and _requestPath ne _item.path}">
	<link rel="canonical" href="/about" />
</c:if>