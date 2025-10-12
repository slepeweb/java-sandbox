<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="widefield-wrapper">
	<div id="widefield-toolbar">
		<div class="left">
			<label>Insert markup:</label>
			<select id="widefield-menu">
				<option value="">Choose ...</option>
				<option value="h2">heading 2 (h2)</option>
				<option value="h3">heading 3 (h3)</option>
				<option value="h4">heading 4 (h4)</option>
				<option value="div">div</option>
				<option value="p">para (p)</option>
				<option value="aside">aside</option>
				<option value="code">code</option>
				<option value="ul">list (ul)</option>
				<option value="ximg">ximg (custom)</option>
				<option value="link">link (a)</option>
				<option value="xcomp">xcomp (custom)</option>
				<option value="table">table</option>
			</select>
		</div>
		
		<div class="right">
			<div id="widefield-close-icon" class="widefield-icon">X</div>
		</div>
	</div>
	
	<textarea id="widefield-editor"></textarea>
</div>
