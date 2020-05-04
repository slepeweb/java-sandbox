<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/links-addlink.tag --></cms:debug>
	
<div id="addlinkdiv">
	<div>
		<div class="ff">
			<label>Type: </label>
			<select name="linktype">
				<option value="unknown">Choose ...</option>
				<c:forTokens items="inline,relation,component,shortcut" delims="," var="type">
					<option value="${type}">${type}</option>
				</c:forTokens>
			</select>	
		</div>
		
		<div class="ff">
			<label>Subtype: </label>
			<select name="linkname">
				<option value="unknown">Choose ...</option>
			</select>	
		</div>
		
		<div class="ff">
			<label>Data: </label>
			<input name="linkdata" value="" />
		</div>
					
		<input type="hidden" name="linkId" value="-1" />
		<input type="hidden" name="state" value="0" />
	</div>	
		
	<div id="linknav-container">
		<details>
			<summary>TIP</summary>
			<p>Choose a link type and subtype, and optionally provide any data that is relevant to this site. 
			Then pick the item you wish to link to, and finally click the 'Use' button. You'll be able to save your changes on
			the underlying form.</p>
		</details>
		
		<div id="linknav"></div>
	</div>
</div>

<div id="link-template" class="hide">
	<div class="sortable-link ui-state-default">
		<div class="left">
			<span class="ui-icon ui-icon-arrowthick-2-n-s"></span>
			<span class="link-identifier">*</span>
		</div>
		
		<div class="right">
			<button class="link-linker" data-id="*" title="Navigate to this item"><i class="fas fa-location-arrow"></i></button>
			<button class="edit-link" title="Edit link"><i class="far fa-edit"></i></button>
			<button class="remove-link" title="Remove link"><i class="far fa-trash-alt"></i></button>
			<span class="hide">*</span>
		</div>
	</div>
</div>