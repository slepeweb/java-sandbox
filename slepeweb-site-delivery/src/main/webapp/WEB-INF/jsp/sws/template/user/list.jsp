<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/template/user/list.jsp --></gen:debug>
 
	<!-- Main content -->	
	<div class="col-1-2 primary-col">	
		<sw:userList />	
	</div>	
				
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col grey-gradient left2right pull-right-sm">
		<sw:navigation-left />
	</div>
	
</sw:standardLayout>