<%@
	tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%--@
  taglib uri="http://www.springframework.org/tags" prefix="spring"--%><%--@
	attribute name="list" required="true" type="java.lang.Object" description="the list of models" --%><%--@
	attribute name="modelNumber" required="true" type="java.lang.Integer" description="Model number"--%>

<title>slepe web solutions</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link href='http://fonts.googleapis.com/css?family=Noto+Sans:400,700,400italic' rel='stylesheet' type='text/css'>
<link href='/resources/css/colorbox.css' rel='stylesheet' type='text/css'>
<script src="/resources/js/jquery.min.js"></script>
<script src="/resources/js/config.js"></script>
<script src="/resources/js/skel.min.js"></script>
<script src="/resources/js/skel-panels.min.js"></script>
<script src="/resources/js/jquery.colorbox-min.js"></script>
<script>
	$(document).ready(function(){
		//Examples of how to assign the Colorbox event to elements
		//$(".group1").colorbox({rel:'group1'});
		//$(".group2").colorbox({rel:'group2', transition:"fade"});
		//$(".group3").colorbox({rel:'group3', transition:"none", width:"75%", height:"75%"});
		//$(".group4").colorbox({rel:'group4', slideshow:true});
		//$(".ajax").colorbox();
		//$(".youtube").colorbox({iframe:true, innerWidth:640, innerHeight:390});
		//$(".vimeo").colorbox({iframe:true, innerWidth:500, innerHeight:409});
		$(".iframe").colorbox({iframe:true, opacity:0.5, closeButton:true, width:"90%", height:"90%"});
		//$(".inline").colorbox({inline:true, width:"50%"});
		//$(".callbacks").colorbox({
		//	onOpen:function(){ alert('onOpen: colorbox is about to open'); },
		//	onLoad:function(){ alert('onLoad: colorbox has started to load the targeted content'); },
		//	onComplete:function(){ alert('onComplete: colorbox has displayed the loaded content'); },
		//	onCleanup:function(){ alert('onCleanup: colorbox has begun the close process'); },
		//	onClosed:function(){ alert('onClosed: colorbox has completely closed'); }
		//});

		//$('.non-retina').colorbox({rel:'group5', transition:'none'})
		//$('.retina').colorbox({rel:'group5', transition:'none', retinaImage:true, retinaUrl:true});
		
		//Example of preserving a JavaScript event for inline calls.
		//$("#click").click(function(){ 
		//	$('#click').css({"background-color":"#f00", "color":"#fff", "cursor":"inherit"}).text("Open this window again and this message will still be here.");
		//	return false;
		//});
	});
</script>
<noscript>
	<link rel="stylesheet" href="/resources/css/skel-noscript.css" />
	<link rel="stylesheet" href="/resources/css/style.css" />
	<link rel="stylesheet" href="/resources/css/style-desktop.css" />
</noscript>
<!--[if lte IE 9]><link rel="stylesheet" href="/resources/css/style-ie9.css" /><![endif]-->
<!--[if lte IE 8]><script src="/resources/js/html5shiv.js"></script><![endif]-->
