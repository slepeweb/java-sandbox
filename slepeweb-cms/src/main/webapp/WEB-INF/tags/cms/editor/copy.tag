<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/copy.tag --></cms:debug>
	
<c:if test="${editingItem.path ne '/'}">
	<%-- Avoid calling the getCopyDetails() method more than once per request --%>
	<c:set var="_copyDetails" value="${editingItem.copyDetails}" />
	<div id="copy-tab">
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
	</div>
</c:if>

	