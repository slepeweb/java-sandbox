<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/links-addlink.tag --></cms:debug>
	
<div id="addlinkdiv">
	<details>
		<summary>HELP</summary>
		<p>This will link '${editingItem.name}' to any other item of your choice. 
		First, choose a link type and then a subtype, and optionally provide any data that is relevant to this site. 
		Then pick the target item you wish to link to in the content structure. DON'T FORGET to save your changes.</p>
	</details>	
		
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
					
		<div class="ff">
			<label>Target: </label>
			<span><i class="fas fa-bars itempicker"></i></span>
			<span id="link-target-identifier"></span>
		</div>
					
		<div id="uselink-buttons">
			<button id="use-link-button" type="button" disabled="disabled">Use</button>
			<button id="cancel-use-link-button" type="button">Cancel</button>
		</div>
		
		<input type="hidden" name="linkId" value="-1" />
		<input type="hidden" name="state" value="0" />
		<input type="hidden" name="childId" value="-1" />
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