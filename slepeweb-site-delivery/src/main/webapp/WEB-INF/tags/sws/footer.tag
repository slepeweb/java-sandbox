<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="copyright">
	<span>&copy; Slepe Web Solutions Ltd. All rights reserved.</span><br />
	<span class="smaller"><c:set var="_now" value="<%= new java.util.Date() %>" /><fmt:formatDate 
		value="${_now}" type="both" pattern="MMMM d, HH:mm" /></span>
</div>

<c:if test="${_serverConfig.liveDelivery}">
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	
	  ga('create', 'UA-58973509-1', 'auto');
	  ga('send', 'pageview');
	</script>
</c:if>