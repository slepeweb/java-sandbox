<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/links.tag --></cms:debug>
	
<div id="links-tab">
	<h2>Linked to</h2>
	
	<div id="sortable-links">
		<c:forEach items="${editingItem.allLinksShortcutsLast}" var="link" varStatus="_stat">
			<div class="sortable-link ui-state-default">
				<div class="left">
					<span class="ui-icon ui-icon-arrowthick-2-n-s"></span>
					<span class="link-identifier">${link}</span>
				</div>
				
				<div class="right">
					<button class="link-linker" data-id="${link.child.id}" title="Navigate to this item"><i class="fas fa-location-arrow"></i></button>
					<button class="edit-link" title="Edit link"><i class="far fa-edit"></i></button>
					<button class="remove-link" title="Remove link"><i class="far fa-trash-alt"></i></button>
					<span class="hide">${link.child.id}|${link.type}|${link.name}|${link.data}|${_stat.index}|1</span>
				</div>
			</div>
		</c:forEach>
	</div>
	
	<c:if test="${empty editingItem.allLinksShortcutsLast}">
		<p>None</p>
	</c:if>
	
	<div class="spacer20">
		<button id="addlink-button" type="button">Add link</button>
		<button id="savelinks-button" type="button">Save changes</button>
	</div>
	
	<div class="spacer20"></div>
	<div id="addlinkdiv">
		<div>
			<div>
				<label>Type: </label>
				<select name="linktype">
					<option value="unknown">Choose ...</option>
					<c:forTokens items="inline,relation,component,shortcut" delims="," var="type">
						<option value="${type}">${type}</option>
					</c:forTokens>
				</select>	
			</div>
			
			<div>
				<label>Subtype: </label>
				<select name="linkname">
					<option value="unknown">Choose ...</option>
				</select>	
			</div>
			
			<div>
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
	
	<div class="spacer20">&nbsp;</div>
	
	<h2>Linked from</h2>
	<c:choose><c:when test="${not empty editingItem.parentLinks}">
		<table>
			<tr>
				<th align="left">Link type</th>
				<th align="left">Link name</th>
				<th align="left">From</th>
			</tr>
			<c:forEach items="${editingItem.parentLinks}" var="_link">
				<tr>
					<td width="20%">${_link.type}</td>
					<td width="20%">${_link.name}</td>
					<td><span class="link-linker" data-id="${_link.child.id}">${_link.child.name}</span></td>
				</tr>
			</c:forEach>
		</table>
	</c:when><c:otherwise>
		<p>None</p>
	</c:otherwise></c:choose>
</div>

<script>
	var linkDialog;
</script>
