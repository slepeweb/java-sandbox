<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:userLayout>
	<div id="user-update-header">
		<h1>User account details</h1>
		
		<c:if test="${_isAdmin}">
			<p>Selected user:
				<select id="user-selector">
					<c:forEach items="${_allUsers}" var="_u">
						<option value="${_u.id}" <c:if test="${_u.id eq _nominatedUser.id}">selected</c:if>>${_u.fullName}</option>
					</c:forEach>
				</select>
			</p>
		</c:if>
	</div>
	
	<div class="user-update-msg">
		<c:if test="${not empty param.flash}">
			<p>${param.flash}</p>
		</c:if>
	</div>
			
	<p class="x1pt2em">These are your account details, for you to update as required.</p>

	<form id="user-form" method="post" action="${applicationContextPath}/user/update/action/${_nominatedUser.id}">
	
		<div class="ff">
			<label>Alias:</label>
			<div class="inputs"><input name="alias" value="${_nominatedUser.alias}" disabled /></div>
		</div>
		
		<div class="ff">
			<label>First name:</label>
			<div class="inputs"><input name="firstname" autocomplete="off" value="${_nominatedUser.firstName}" /></div>
		</div>
		
		<div class="ff">
			<label>Last name:</label>
			<div class="inputs"><input name="lastname" autocomplete="off" value="${_nominatedUser.lastName}" /></div>
		</div>
		
		<div class="ff">
			<label>Email:</label>
			<div class="inputs"><input name="email" autocomplete="off" value="${_nominatedUser.email}" /></div>
		</div>
		
		<div class="ff">
			<label>Phone:</label>
			<div class="inputs"><input name="phone" autocomplete="off" value="${_nominatedUser.phone}" /></div>
		</div>
		
		<div class="ff">
			<label>Password:</label>
			<div class="inputs"><input name="password" autocomplete="off" value="" /></div>
		</div>

		<hr />
		
		<p>Please complete at least 2 questions and answers. This is essential should you forget your password and try to
			create a new one.</p>
		
		<c:forEach begin="0" end="2" var="_i">
			<c:set var="_j" value="${_i + 1}" />
			<c:set var="_qa" value="${_qandA.list[_i]}" />
			
			<div class="ff">
				<label>Question #${_j}:</label>
				<div class="inputs"><input name="q${_i}" autocomplete="off" value="${not empty _qa.question ? _qa.question : ''}" /></div>
			</div>
			
			<div class="ff">
				<label>Answer #${_j}:</label>
				<div class="inputs"><input name="a${_i}" autocomplete="off" value="${not empty _qa.answer ? _qa.answer : ''}" /></div>
			</div>
			
			<hr />
		</c:forEach>
					
		<div class="button-set">
				<button class="action" type="submit">Submit</button>
				<button id="to-editor" class="reset" type="button">Back to editor</button>
		</div>
		
		
	</form>	
	
</cms:userLayout>

<script>
let _ctx = '${applicationContextPath}';

$(function() {
	$('input[name=q1]').focus();
	
	$('button#to-editor').click(function() {
		window.location = _ctx + '/page/editor';
	})
	
	$('select#user-selector').change(function() {
		let userId = $(this).val();
		window.location = _ctx + '/user/update/form/' + userId;
	})
})
</script>