<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<title>slepe web solutions | ${_page.title}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width">
<c:if test="${not empty _item.fields.metadescription}"><meta name="description" content="${_item.fields.metadescription}" /></c:if>
<c:if test="${not empty _item.fields.metatitle}"><meta name="title" content="${_item.fields.metatitle}" /></c:if>

<link rel="stylesheet" href="/resources/css/normalize.css" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" type="text/css">
<link href="/resources/css/colorbox.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="/resources/sws/css/style-new.css" />

<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script src="/resources/js/jquery.colorbox-min.js"></script>
<script src="/resources/sws/js/colorbox-impl.js"></script>
<script src="/resources/sws/js/site.js"></script>
<!-- <script src="/resources/sws/js/jquery.paroller.min.js"></script> -->

<gen:extraCSS />
<gen:extraJS />

<%-- _requestPath is set in ResponseHeaderFilter --%>
<c:if test="${_item.path eq '/about' and _requestPath ne _item.path}">
	<link rel="canonical" href="/about" />
</c:if>
