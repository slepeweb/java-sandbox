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
				<site:p>${_item.fields.bodytext}</site:p>
			</div>	
		</div>	
	</div>

	<div class="row uniform">
		<div class="4u 12u(3)">	
			<div class="raised-box">
				<h2><a href="/news">News &amp; Events</a></h2>
				<ul>
					<c:forEach items="${_newsEventsIndex}" var="_child" end="3">
						<li><a href="${_child.path}">${_child.fields.title}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
		
		<div class="4u 12u(3)">	
			<div class="raised-box">
				<h2>Match results</h2>
				<ul>
					<li><a href="#">Senior Vets Mixed v. Histon</a></li>
					<li><a href="#">Senior Vets Mixed v. Exning</a></li>
				</ul>
			</div>
		</div>
		
		<div class="4u 12u(3)">	
			<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" type="rss_feed" />
		</div>
	</div>
			
</ntc:standardLayout>