<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<title>Needingworth Tennis Club | ${_page.title}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<c:if test="${not empty _item.fields.metadescription}"><meta name="description" content="${_item.fields.metadescription}" /></c:if>
<c:if test="${not empty _item.fields.metatitle}"><meta name="title" content="${_item.fields.metatitle}" /></c:if>
<!--[if lte IE 8]><script src="/resources/ntc/js/html5shiv.js"></script><![endif]-->
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" type="text/css">
<link href="/resources/css/colorbox.css" rel="stylesheet" type="text/css">
<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script src="/resources/ntc/js/skel.min.js"></script>
<script src="/resources/ntc/js/skel-layers.min.js"></script>
<script src="/resources/ntc/js/init.js"></script>
<script src="/resources/js/jquery.colorbox-min.js"></script>
<script src="/resources/ntc/js/colorbox-impl.js"></script>
<%-- 
	Failed attempt to implement menu drop-downs - from apycom
	<link href="/resources/css/menu.css" rel="stylesheet" type="text/css"> 
	<script src="/resources/js/menu.js"></script> 
--%>

<gen:extraCSS />
<gen:extraJS />

<noscript>
	<link rel="stylesheet" href="/resources/ntc/css/skel.css" />
	<link rel="stylesheet" href="/resources/ntc/css/style.css" />
	<link rel="stylesheet" href="/resources/ntc/css/style-xlarge.css" />
</noscript>
