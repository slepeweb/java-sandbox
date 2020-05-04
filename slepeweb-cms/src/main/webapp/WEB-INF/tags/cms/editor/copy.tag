<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/copy.tag --></cms:debug>
	
<form>
	<div class="ff">
		<label for="name">Name: </label><input name="name" value="${_copyDetails[2]}" />
	</div>
	<div class="ff">
		<label for="simplename">Simple name: </label><input name="simplename" 
			value="${_copyDetails[1]}" />
	</div>
	
	<details>
		<summary>NOTE</summary>
		<p>Deep copy is NOT available.</p>
	</details>
	
	<div>
		<button id="copy-button" type="button"
			title="Produce an identical copy of this item, including it's links">Copy</button>
	</div>
</form>
	