<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="urlPrefix" required="true" rtexprvalue="true" %><%@ 
	attribute name="pager" type="com.slepeweb.money.bean.solr.SolrPager" required="true" rtexprvalue="true" %><%@ 
	attribute name="params" required="false" rtexprvalue="true" %>

<p><strong>Found ${pager.totalHits} search results</strong></p>

<c:if test="${pager.visible}">
	<c:set var="_queryString" value="" />
	<c:if test="${not empty params}"><c:set var="_queryString">?${params}</c:set></c:if>
	<p>
		<c:if test="${pager.previous}"><span 
				class="pager arrow left"><a href="${urlPrefix}/${pager.previousBlock}${_queryString}"><i 
					class="fas fa-angle-double-left" title="Jump left"></i></a></span><span 
				class="pager arrow left"><a href="${urlPrefix}/${pager.previousSelection}${_queryString}"><i 
					class="fas fa-angle-left" title="Previous"></i>Previous</a></span></c:if>
					
		<c:forEach items="${pager.navigation}" var="_option">
			<span class="pager <c:if test="${_option.selected}">selected</c:if>"><a href="${urlPrefix}/${_option.value}${_queryString}">${_option.name}</a></span>
		</c:forEach>
		
		<c:if test="${pager.next}"><span 
			class="pager arrow right"><a href="${urlPrefix}/${pager.nextSelection}${_queryString}">Next<i 
				class="fas fa-angle-right" title="Next"></i></a></span><span 
			class="pager arrow right"><a href="${urlPrefix}/${pager.nextBlock}${_queryString}"><i 
				class="fas fa-angle-double-right" title="Jump right"></i></a></span></c:if>
	</p>
</c:if>