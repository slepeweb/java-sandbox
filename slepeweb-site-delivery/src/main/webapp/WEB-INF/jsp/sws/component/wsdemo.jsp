<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/wsdemo.jsp --></gen:debug>

<table class="jaxws-table">
	<tr>
		<td class="heading"><label for="org">Source</label></td>
		<td><input id="password-org" type="text" name="org" size="16" /></td>
	</tr>
	
	<%-- Only show this input field if logged-in user has the necessary credentials --%>
	<c:if test="${_isPasswordClient}">
		<tr>
			<td class="heading"><label for="key">Key</label></td>
			<td><input id="password-key" type="text" name="key" size="16" /></td>
		</tr>
	</c:if>
 
</table>

<button type="button" class="button small special" id="password-update">Encrypt</button>

<div id="password-results" class="hide">
	<table class="jaxws-table">
		<tr>
			<td class="heading"><label for="password">Encrypted</label></td>
			<td><input id="password-pwd" type="text" name="password" size="16" readonly style="background: #cccccc" /></td>
		</tr>
		<tr>
			<td class="heading"><label for="chunked">Chunked</label></td>
			<td><input id="password-chunked" type="text" name="chunked" size="16" readonly style="background: #cccccc" /></td>
		</tr>
	</table>
	
	<button type="button" class="button small special" id="password-reset">Reset</button>
</div>
