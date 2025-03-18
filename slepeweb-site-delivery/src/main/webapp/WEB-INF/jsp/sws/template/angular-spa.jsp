<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request">http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js</c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/angular-spa.jsp --></gen:debug>
	
		<!-- Left Sidebar -->
		<div class="3u 12u(3)">
			<sw:navigation-left />
		</div>
		
		<!-- Main content -->	
		<div class="9u 12u(3) important(3)">	
			<sw:standardBody />	
			<sw:angular-spa /> 
		</div>					
	
</sw:standardLayout>