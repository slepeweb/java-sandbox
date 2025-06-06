<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<%-- Assume we're adding a new user --%>
<c:set var="submitAction" value="/spring/user/add" scope="request" />
<c:set var="submitLabel" value="Add user" scope="request" />
<c:set var="pageHeading" value="Add a new user" scope="request" />

<c:if test="${not empty user.id}">
	<%-- Oh, we have an id, so we must be updating an existing user --%>
	<c:set var="submitAction" value="/spring/user/upd" scope="request" />
	<c:set var="submitLabel" value="Update user" scope="request" />
	<c:set var="pageHeading" value="Update an existing user" scope="request" />
</c:if>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/template/user/form.jsp --></gen:debug>
			
	<div class="col-3-4 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<sw:userForm />	
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