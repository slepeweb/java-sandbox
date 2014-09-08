<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<c:if test="${not empty _rss and fn:length(_rss) > 0}">
<script>
	$(document).ready(function(){
		//Examples of how to assign the Colorbox event to elements
		//$(".group1").colorbox({rel:'group1'});
		$(".group2").colorbox({rel:'group2', transition:"none", current:'Story {current} of {total}'});
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

<section>
	<h3>Technology news</h3>
	<p>Latest stories from the BBC:</p>
	<ul class="link-list">
		<c:forEach items="${_rss}" var="link" end="3">
			<li class="compact"><a class="iframe group2" href="${link.href}">${link.title}</a></li>
		</c:forEach>
	</ul>
</section>
</c:if>

<gen:insertComponentSet site="${_item.site.shortname}" owner="${_page.rightSidebar}" />
