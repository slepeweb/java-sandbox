<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/links-editbinding.tag --></cms:debug>
	
<div id="editbindingdiv" class="hide">
	<details>
		<summary>GUIDE</summary>
		<ul>
			<li>This dialog allows you to change a standard binding to a component, and vice-versa.
					It acts upon the (orthogonal) link between this item and its parent.</li> 
		</ul>
	</details>	
	
	<div>
		<div class="ff">
			<label>Type: </label>
			<div class="inputs">
				<select name="linktype"></select>
			</div>
		</div>
		
		<div class="ff">
			<label>Subtype: </label>
			<div class="inputs">
				<select name="linkname"></select>
			</div>
		</div>
		
		<input type="hidden" name="linkId" value="-1" />
		<input type="hidden" name="state" value="0" />
		<input type="hidden" name="childId" value="-1" />
	</div>	
</div>
