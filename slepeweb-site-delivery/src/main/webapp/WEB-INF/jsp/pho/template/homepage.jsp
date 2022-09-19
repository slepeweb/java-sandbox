<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:set var="_extraInpageJs" scope="request">
	$(function() {
		// Homepage item type is cached; this js ensures form displays most recent values,
		// as stored in cookies.
		
		$('input[name=searchtext]').val($('input[name=hiddentext]').val());
		$('input[name=from]').val($('input[name=hiddenfrom]').val());
		$('input[name=to]').val($('input[name=hiddento]').val());

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
						
					<label>Search terms</label><input name="searchtext" type="text" value="${_lastSearchText}" />
					<label>From date</label><input id="from-date" name="from" type="text" value="${_lastFromDate}" />
					<label>To date</label><input id="to-date" name="to" type="text" value="${_lastToDate}" />
					
					<input type="hidden" name="page" value="1" />

					<c:if test="${not empty _latestCookieValues}">
						<input type="hidden" name="hiddentext" value="${_latestCookieValues.text}" />
						<input type="hidden" name="hiddenfrom" value="${_latestCookieValues.from}" />
						<input type="hidden" name="hiddento" value="${_latestCookieValues.to}" />
					</c:if>
	
					<input type="submit" value="Search" />
				</form>
			</div>
		</div>
	</div>
	
</pho:pageLayout>