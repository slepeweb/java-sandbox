<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request">https://www.gstatic.com/charts/loader.js</c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-funds.jsp --></gen:debug>

	<!-- Main content -->	
	<div class="col-1-1">	
		<sw:standardBody />	
		<script type="text/javascript">
			${_graphData}
		</script>
		
    <div id="curve_chart" style="width: 900px; height: 500px"></div>
	</div>					

</sw:standardLayout>