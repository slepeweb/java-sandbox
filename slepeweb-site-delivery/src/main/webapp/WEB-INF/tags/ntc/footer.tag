<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div id="footer">		
	<div class="container">
		<div class="row">
			<div class="6u 12u(3)">
				<ntc:weather />
			</div>
			<div class="6u 12u(3)">
				<p class="sponsor">Sponsored by <img src="/content/images/sponsor" /></p>
			</div>
		</div>
	</div>
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