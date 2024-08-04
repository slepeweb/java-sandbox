<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/copy.tag --></cms:debug>
	
<%-- <c:set var="_copyDetails" value="${editingItem.copyDetails}" scope="request" /> --%>

<form>
	<div class="ff">
		<label for="name">Name: </label><div class="inputs"><input name="name" value="${editingItem.name}-COPY" /></div>
	</div>
	
	<div class="ff">
		<label for="simplename">Simple name: </label><div class="inputs"><input name="simplename" value="" /></div>
	</div>
	
	<details>
		<summary>NOTE</summary>
		<p>This copies the current item only, NOT including any ancestors.</p>
	</details>
	
	<div class="button-set">
		<button class="action" type="button"
			title="Produce an identical copy of this item, including it's links">Copy</button>
		<button class="reset" type="button"  disabled="disabled" ${_resetHelp}>Reset form</button>
	</div>
</form>
	