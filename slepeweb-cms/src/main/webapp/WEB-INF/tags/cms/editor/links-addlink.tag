<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/links-addlink.tag --></cms:debug>
	
<div id="addlinkdiv" class="hide">
	<details>
		<summary>GUIDE</summary>
		<ul>
			<li>This dialog will link the current item to any other item of your choice.</li> 
			<li>First, choose a link type and then a subtype.</li>
			<li>Optionally provide additional link data. The format will vary by site - refer to the documentation.</li>
			<li>Then pick the target item you wish to link to.</li>
			<li>DON'T FORGET to save your changes after clicking the 'Use' button.</li>
		</ul>
	</details>	
	
	<c:set var="_linkTypes">relation,inline,shortcut</c:set>
	<c:if test="${editingItem.shortcut}">
		<c:set var="_linkTypes">shortcut</c:set>
	</c:if>
	
	<div>
		<div class="ff">
			<label>Type: </label>
			<div class="inputs">
				<select name="linktype">
					<option value="unknown">Choose ...</option>
					<c:forTokens items="${_linkTypes}" delims="," var="type">
						<option value="${type}">${type}</option>
					</c:forTokens>
				</select>
			</div>
		</div>
		
		<div class="ff">
			<label>Subtype: </label>
			<div class="inputs">
				<select name="linkname">
					<option value="unknown">Choose ...</option>
					<option value="std">std</option>
				</select>
			</div>
		</div>
		
		<div class="ff">
			<label>Data: </label>
			<div class="inputs">
				<input name="linkdata" value="" />
				<span id="link-guidance-icon"><i class="far fa-question-circle"></i></span>
			</div>
		</div>
					
		<div class="ff">
			<label>Target: </label>
			<div class="inputs">
				<span><i class="fa-solid fa-sitemap itempicker"></i></span>
				<span id="link-target-identifier"></span>
			</div>
		</div>

		<input type="hidden" name="linkId" value="-1" />
		<input type="hidden" name="state" value="0" />
		<input type="hidden" name="childId" value="-1" />
	</div>	
</div>
	
<div id="link-guidance-list" class="hide"></div>
<div id="link-guidance"></div>

<div id="link-template" class="hide">
	<div class="sortable-link changed-link ui-state-default">
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
