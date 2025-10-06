<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraCSS" scope="request">${applicationContextPath}/resources/css/forgotPassword.css</c:set>

<cms:basicLayout loadjs="false">
	
	<div id="main-wrapper">
	
		<h1>Password reset</h1>
						
		<div class="password-reset">

			<c:if test="${not empty msg}">
				<div class="user-update-msg error">${msg}</div>
			</c:if>	
	
			<c:if test="${not error and not completed}">
				<p class="x1pt2em">Your registered email address is ${_u.email}. Please answer the following security questions,
					then enter your new password (twice).</p>
			
				<form id="password-reset-form" method="post" action="${applicationContextPath}/user/password/reset">
				
					<c:forEach items="${_qandA.list}" var="_qa">
						<div class="ff">
							<label>${_qa.question}:</label>
							<div class="inputs"><input name="a${_qa.id}" autocomplete="off" 
								value="${not empty _qandASubmitted ? _qandASubmitted.getList().get(_qa.id).getAnswer() : ''}" /></div>
						</div>
					</c:forEach>
								
					<hr />
					
					<div class="ff hidden">
						<label>New password:</label>
						<div class="inputs"><input name="password" autocomplete="off" 
							value="${not empty _passwordSubmitted ? _passwordSubmitted : ''}" /></div>
					</div>
			
					<input type="hidden" name=email value="${_u.email}" />
					<input type="hidden" name=code value="${_code}" />
					<button class="action" type="submit" style="margin-top: 2.0em;">Submit</button>				
				</form>
				
			</c:if>
		
		</div>
	</div>
</cms:basicLayout>

<script>
let _ctx = '${applicationContextPath}';

$(function() {
	$('input[name=a0]').focus();
	
	$('input').click(function() {
		$('div.user-update-msg').addClass('hidden')
	})
})
</script>