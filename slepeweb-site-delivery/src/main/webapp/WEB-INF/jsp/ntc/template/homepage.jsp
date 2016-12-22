<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/homepage.jsp --></gen:debug>

	<!-- Main content -->	
	<div class="row uniform">
		<div class="4u 12u(3)">	
			<img src="/content/images/logo" />	
		</div>
		
		<div class="8u$ 12u$(3)">
			<div id="welcome" class="two-column">
				${_item.fields.bodytext}
			</div>	
		</div>	
	</div>

	<div class="row uniform">
		<div class="4u 12u(3)">	
			<div class="raised-box">
				<h2><a href="${_eventsIndexItem.path}">${_eventsIndexItem.fields.title}</a></h2>
				<ul>
					<c:forEach items="${_eventsIndex}" var="_link">
						<li><span><fmt:formatDate value="${_link.date}" pattern="MMM dd" />:</span>
							<a href="${_link.href}">${_link.title}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
		
		<div class="4u 12u(3)">	
			<div class="raised-box">
				<h2><a href="${_newsIndexItem.path}">${_newsIndexItem.fields.title}</a></h2>
				<ul>
					<c:forEach items="${_newsIndex}" var="_link">
						<li><span><fmt:formatDate value="${_link.date}" pattern="MMM dd" />:</span> 
							<a href="${_link.href}" class="${_link.style}">${_link.title}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
		
		<div class="4u 12u(3)">	
			<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" type="rss_feed" />
		</div>
	</div>
			
</ntc:standardLayout>