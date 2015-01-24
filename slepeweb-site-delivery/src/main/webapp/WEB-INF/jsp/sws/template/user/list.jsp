<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/template/user/list.jsp --></gen:debug>
 
	<!-- Left Sidebar -->
	<div class="3u 6u(3)">
		<sw:navigation-left />
	</div>
	
	<!-- Main content -->	
	<div class="6u 9u(2) 12u(3) important(3)">	
		<sw:userList />	
	</div>	
				
</sw:standardLayout>