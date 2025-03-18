<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/template/user/list.jsp --></gen:debug>
 
	<div class="col-3-4 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<sw:userList />	
			</div>
			
			<!-- *Empty* Right sidebar -->
			<div class="col-1-3"></div>
		</div>
	</div>
	
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col grey-gradient left2right">
		<sw:navigation-left />
	</div>
	
</sw:standardLayout>