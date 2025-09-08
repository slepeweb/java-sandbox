<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:basicLayout loadjs="${false}">

	<div id="main-wrapper">
		<h1>User account details</h1>
				
		<p class="x1pt2em">These are your account details, for you to update as required.</p>

		<form id="user-form" method="post" action="${applicationContextPath}/user/update/action">
		
			<div class="ff">
				<label>Alias:</label>
				<div class="inputs"><input name="alias" value="${_user.alias}" disabled /></div>
			</div>
			
			<div class="ff">
				<label>First name:</label>
				<div class="inputs"><input name="firstname" autocomplete="off" value="${_user.firstName}" /></div>
			</div>
			
			<div class="ff">
				<label>Last name:</label>
				<div class="inputs"><input name="lastname" autocomplete="off" value="${_user.lastName}" /></div>
			</div>
			
			<div class="ff">
				<label>Email:</label>
				<div class="inputs"><input name="email" autocomplete="off" value="${_user.email}" /></div>
			</div>
			
			<div class="ff">
				<label>Phone:</label>
				<div class="inputs"><input name="phone" autocomplete="off" value="${_user.phone}" /></div>
			</div>
			
			<hr />
			
			<p class="x1pt2em">OPTIONAL: Some sites will ask for additional user information, to increase security. If this applies to you,
			please supply up to 3 question/answers</p>
		
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
	</div>
	
</cms:basicLayout>

<script>
let _ctx = '${applicationContextPath}';

$(function() {
	$('input[name=q1]').focus();
	
	$('button#to-editor').click(function() {
		window.location = _ctx + '/page/editor';
	})
})
</script>