<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request"></c:set>
<c:set var="_extraCss" scope="request"></c:set>

<%-- Assume we're adding a new user --%>
<c:set var="submitAction" value="/spring/user/add" scope="request" />
<c:set var="submitLabel" value="Add user" scope="request" />
<c:set var="pageHeading" value="Add a new user" scope="request" />

<c:if test="${not empty userForm.id}">
	<%-- Oh, we have an id, so we must be updating an existing user --%>
	<c:set var="submitAction" value="/spring/user/upd" scope="request" />
	<c:set var="submitLabel" value="Update user" scope="request" />
	<c:set var="pageHeading" value="Update an existing user" scope="request" />
</c:if>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/template/user/form.jsp --></gen:debug>
			
	<!-- Left Sidebar -->
	<div class="3u">
		<sw:navigation-left />
	</div>
	
	<!-- Main content -->	
	<div class="9u skel-cell-mainContent">	
		<sw:userForm />	
	</div>	
	
</sw:standardLayout>