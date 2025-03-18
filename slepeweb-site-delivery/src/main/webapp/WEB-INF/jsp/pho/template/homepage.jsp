<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraJs" scope="request">/resources/pho/js/homepage.js</c:set>

<pho:pageLayout type="std">
	<gen:debug><!-- jsp/pho/homepage.jsp --></gen:debug>
		
	<div class="main home">
		<h2>${_item.fields.title}</h2>
		
		<div id="search-tools">
			<div id="tag-cloud-wrapper">
				<div id="tag-cloud">
					<h3>Search by tag</h3>
					<c:forEach items="${_toptags.list}" var="counter" varStatus="status" begin="0" end="8">
						<div class="tag-link t${status.count}" 
							data-value="${counter.value}" 
							data-size="${counter.size}px"
							data-color="${counter.color}">${counter.value}</div>
					</c:forEach>
				</div>
				
				<div id="top-50-tags">
					<h3>Top 50 tags</h3>
					<select id="top50-tags-selector">
						<option value="">Choose ...</option>
						<c:forEach items="${_toptags.list}" var="counter" begin="0" end="49">
							<option value="${counter.value}">${counter.value}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			
			<div id="search-area">
				<p><span class="h3-like">Advanced Search</span> <i class="fa-solid fa-magnifying-glass fa-2x"></i></p>
				<details>
					<summary>Search tips</summary>
					<ul>
						<li>Use this form to exercise greater control over your search results.</li>
						<li>Search term(s) are mandatory; dates are optional.</li>
						<li>Terms 'luca football' return all items matching EITHER 'luca' OR 'football'.</li>
						<li>Terms '+luca +football' return ONLY items matching BOTH 'luca' AND 'football'.</li>
						<li>Terms '+luca -football' return all items matching 'luca', EXCEPT for those also matching 'football'.</li>
					</ul>
				</details>
				
				<div id="search-form">
					<form action="/searchresults" method="post" 
							enctype="application/x-www-form-urlencoded" accept-charset="utf-8">
							
						<label>Search terms*</label><input name="searchtext" type="text" value="${_latestCookieValues.text}" />
						
						<label>From year</label>
						<i class="fa-solid fa-ban clear-input"></i>
						<input id="from-date" name="from" type="number" min="1700" max="${_nowYear}"
							placeholder="From the 18th century ..." value="${_latestCookieValues.from}" />
							
						<label>To year</label>
						<i class="fa-solid fa-ban clear-input"></i>
						<input id="to-date" name="to" type="number" min="1700" max="${_nowYear}" 
							placeholder="... to the current day" value="${_latestCookieValues.to}" />
						
						<input type="hidden" name="page" value="1" />
						
						<div id="search-button-container">
							<input type="submit" value="Search" />
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</pho:pageLayout>