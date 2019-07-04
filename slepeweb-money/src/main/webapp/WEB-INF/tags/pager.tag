<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="urlPrefix" required="true" rtexprvalue="true" %><%@ 
	attribute name="pager" type="com.slepeweb.money.bean.solr.SolrPager" required="true" rtexprvalue="true" %><%@ 
	attribute name="params" required="false" rtexprvalue="true" %>

<p><strong>Found ${pager.totalHits} matching transactions</strong></p>

<c:if test="${pager.visible}">
	<p id="advanced-search-pager">
		<c:if test="${pager.previous}"><span 
				class="pager arrow left"><a href="${urlPrefix}/${pager.previousBlock}${params}"><i 
					class="fas fa-angle-double-left" title="Jump left"></i></a></span><span 
				class="pager arrow left"><a href="${urlPrefix}/${pager.previousSelection}${params}"><i 
					class="fas fa-angle-left" title="Previous"></i>Previous</a></span></c:if>
					
		<c:forEach items="${pager.navigation}" var="_option">
			<span class="pager <c:if test="${_option.selected}">selected</c:if>"><a href="${urlPrefix}/${_option.value}${params}">${_option.name}</a></span>
		</c:forEach>
		
		<c:if test="${pager.next}"><span 
			class="pager arrow right"><a href="${urlPrefix}/${pager.nextSelection}${params}">Next<i 
				class="fas fa-angle-right" title="Next"></i></a></span><span 
			class="pager arrow right"><a href="${urlPrefix}/${pager.nextBlock}${params}"><i 
				class="fas fa-angle-double-right" title="Jump right"></i></a></span></c:if>
	</p>
</c:if>