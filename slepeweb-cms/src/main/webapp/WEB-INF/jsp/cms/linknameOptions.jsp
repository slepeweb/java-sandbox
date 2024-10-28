<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div>
	<select>
		<option value='unknown'>Choose ...</option>
		<c:forEach items="${_linknameOptions}" var="_option">
			<option value="${_option.name}">${_option.name}</option>
		</c:forEach>
	</select>
	
	<div>
		<c:forEach items="${_linknameOptions}" var="_option">
			<c:if test="${not empty _option.guidance}">
				<div id="link-guidance-${_option.name}">
					<edit:guidance guidance="${_option.guidance}" />
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
