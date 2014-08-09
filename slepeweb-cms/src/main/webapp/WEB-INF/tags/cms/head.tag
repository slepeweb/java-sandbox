<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<title>slepe web solutions | </title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link rel="stylesheet" href="/resources/css/main.css" type="text/css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

<!-- Include Fancytree skin and library -->
<link href="/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<!-- Initialize the tree when page is loaded -->
<script type="text/javascript">
  $(function(){
    $("#leftnav").fancytree({
    	source: {
    		url: "/rest/cms/lazyleftnav",
    		cache: false,
    		checkbox: true
    	},
    	lazyLoad: function(event, data) {
    		var node = data.node;
    		data.result = {
    			url: "/rest/cms/lazyleftnav",
    			data: {key: node.key}
    		}
    	},
    	activate: function(event, data) {
    		var node = data.node;
    		window.alert("Key " + node.key + " activated");
    	}
    });
  });
</script>

<cms:extraCSS />
<cms:extraJS />
