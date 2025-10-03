<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraCSS" scope="request">${applicationContextPath}/resources/css/user.css</c:set>

<cms:basicLayout loadjs="false">
	
	<div id="main-wrapper">
	
		<h1>Password reset</h1>
						
		<div class="password-reset">

			<c:if test="${not empty msg and error}">
				<div class="user-update-msg error">${msg}</div>
			</c:if>	
	
			<c:choose><c:when test="${empty msg or error}">
				<p class="x1pt2em">Your registered email address is ${_u.email}. Please answer the following security questions,
					then enter your new password (twice).</p>
			
				<form id="password-reset-form" method="post" action="${applicationContextPath}/user/password/reset">
				
					<c:forEach items="${_qandA.list}" var="_qa">
						
						<div class="ff">
							<label>${_qa.question}:</label>
							<div class="inputs"><input name="a${_qa.id}" autocomplete="off" value="" /></div>
						</div>
						
					</c:forEach>
								
					<hr />
					
					<div class="ff">
						<label>New password:</label>
						<div class="inputs"><input name="password" autocomplete="off" value="" /></div>
					</div>
			
					<div class="ff">
						<label>Repeat password:</label>
						<div class="inputs"><input name="password2" autocomplete="off" value="" /></div>
					</div>
			
					<hr />
					
					<input type="hidden" name=email value="${_u.email}" />
					<input type="hidden" name=code value="${_code}" />
					<button class="action" type="submit">Submit</button>				
				</form>
				
			</c:when><c:otherwise>
			
				<p class="x1pt2em">${msg}</p>
			
			</c:otherwise></c:choose>
		
		</div>
	</div>
</cms:basicLayout>

<script>
let _ctx = '${applicationContextPath}';

$(function() {
	$('input[name=a0]').focus();
})
</script>