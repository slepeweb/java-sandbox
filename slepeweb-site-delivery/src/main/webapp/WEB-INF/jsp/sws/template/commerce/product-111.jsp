<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/commerce/product-111.jsp --></gen:debug>
	
	<div class="col-3-4 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<sw:standardBody />	
				<div><img src="${_item.image.path}" /></div>
				<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
			</div>
			
			<!-- Right sidebar -->
			<div class="col-1-3 primary-col">	
				<c:if test="${_item.alphaAxisId > -1}">
					<label>${_item.alphaAxis.label}</label>
					<select>
						<c:forEach items="${_item.alphaAxisValuesWithStock}" var="_av">
							<option value="${_av.id}">${_av.value}</option>
						</c:forEach>
					</select>
					
					<c:if test="${_item.betaAxisId > -1}">
						<label>${_item.betaAxis.label}</label>
						<select>
							<c:forEach items="${_item.betaAxisValuesWithStock}" var="_av">
								<option value="${_av.id}">${_av.value}</option>
							</c:forEach>
						</select>
					</c:if>
				</c:if>				
				
				<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
			</div>
		</div>
	</div>
	
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col">
		<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
	</div>

</sw:standardLayout>