<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/copy.tag --></cms:debug>
	
<c:set var="_copyDetails" value="${editingItem.copyDetails}" scope="request" />

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
	
	<div class="button-set">
		<button class="action" type="button"
			title="Produce an identical copy of this item, including it's links">Copy</button>
		<button class="reset" type="button">Reset form</button>
	</div>
</form>
	