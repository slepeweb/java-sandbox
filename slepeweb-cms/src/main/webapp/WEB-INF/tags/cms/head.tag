<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<title>slepe web solutions | </title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link rel="stylesheet" href="/resources/css/main.css" type="text/css">

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

<!-- Include Fancytree skin and library -->
<link href="/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<!-- Initialize the tree when page is loaded -->
<script type="text/javascript">
  $(function(){
	  var getFieldValues = function() {
		  var result = {};
		  $("#field-form input, #field-form textarea").each(function(i, obj) {
			  result[$(obj).attr("name")] = $(obj).val();
		  });
		  return result;
	  };
	  
		// Left navigation
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
    		$.ajax("/rest/cms/item-editor", {
    			cache: false,
    			data: {key: node.key}, 
    			dataType: "html",
    			mimeType: "text/html",
    			success: function(html, status, z) {
    				var tabsdiv = $("#item-editor");
    				tabsdiv.empty().append(html);
    				if (tabsdiv.hasClass("ui-tabs")) {
        				$("#item-editor").tabs("destroy");
    				}
    				$("#item-editor").tabs();
    				$("#core-button").click(function () {
    					$.ajax("/rest/cms/item/" + node.key + "/update/core", {
    							type: "POST",
    		    			cache: false,
    		    			data: {
    		    				name: $("input[name='name']").val(),
    		    				simplename: $("input[name='simplename']").val()
    		    			}, 
    		    			dataType: "json",
    		    			success: function(json, status, z) {
    		    				window.alert("Success");
    		    			}
    					});
    				});
    				$("#field-button").click(function () {
    					$.ajax("/rest/cms/item/" + node.key + "/update/fields", {
    							type: "POST",
    		    			cache: false,
    		    			data: getFieldValues(), 
    		    			dataType: "json",
    		    			success: function(json, status, z) {
    		    				window.alert("Success");
    		    			}
    					});
    				});
    			}
    		});
    	}
    });
  });
</script>

<cms:extraCSS />
<cms:extraJS />
