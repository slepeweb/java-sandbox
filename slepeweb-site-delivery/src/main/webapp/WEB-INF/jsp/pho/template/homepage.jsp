<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:set var="_extraInpageJs" scope="request">
	$(function() {
		$('#from-date, #to-date').datepicker({
			dateFormat: 'yy/mm/dd',
			changeMonth: true,
			changeYear: true,
		});
	});
</c:set>

<pho:pageLayout type="std">
	<gen:debug><!-- jsp/pho/homepage.jsp --></gen:debug>
		
	<div class="main">
		<h2>${_item.fields.title}</h2>
		<div id="search-area">
			<p><i class="fa-solid fa-magnifying-glass fa-2x"></i> <br />Please enter
			your search criteria to find matching photos &amp; videos.</p>
		
			<div id="search-form">
				<form action="/searchresults" method="post" 
						enctype="application/x-www-form-urlencoded" accept-charset="utf-8">
						
					<label>Search terms</label><input name="searchtext" type="text" value="${_latestCookieValues.text}" />
					<label>From date</label><input id="from-date" name="from" type="text" value="${_latestCookieValues.from}" />
					<label>To date</label><input id="to-date" name="to" type="text" value="${_latestCookieValues.to}" />
					
					<input type="hidden" name="page" value="1" />
					<input type="submit" value="Search" />
				</form>
			</div>
		</div>
	</div>
	
</pho:pageLayout>