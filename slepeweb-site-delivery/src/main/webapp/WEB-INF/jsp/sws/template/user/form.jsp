<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
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
			
	<!-- Main content -->	
	<div class="col-1-2 primary-col">	
		<sw:userForm />	
	</div>	
	
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col grey-gradient left2right pull-right-sm">
		<sw:navigation-left />
	</div>
	
</sw:standardLayout>