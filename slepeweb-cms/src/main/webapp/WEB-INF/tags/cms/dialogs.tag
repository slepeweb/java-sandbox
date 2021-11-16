<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="link-not-defined-dialog" class="hide cms-dialog">
  <p>
    <span class="ui-icon ui-icon-circle-check cms-icon"></span>
    Link data is incomplete. Close this dialog, and click on 'HELP' for guidance.
  </p>
</div>

<div id="duplicate-link-target-dialog" class="hide cms-dialog">
  <p>
    <span class="ui-icon ui-icon-circle-check cms-icon"></span>
    A link from the current item to this target already exists.
  </p>
</div>

<div id="illegal-link-target-dialog" class="hide cms-dialog">
  <p>
    <span class="ui-icon ui-icon-circle-check cms-icon"></span>
    You cannot link to a child (binding) of the current item.
  </p>
</div>

<div id="link-data-format-dialog" class="hide cms-dialog">
  <p>
    <span class="ui-icon ui-icon-circle-check cms-icon"></span>
    The link data format is incorrect - please click on help icon for guidance.
  </p>
</div>

<div id="confirm-field-update-dialog" class="hide cms-dialog">
	<p>
		<span class="ui-icon ui-icon-alert cms-icon"></span>
		You are about to update field data, which CANNOT BE UNDONE. Are you sure you want to do this?
	</p>
</div>

<div id="confirm-trash-dialog" class="hide cms-dialog">
	<p>
		<span class="ui-icon ui-icon-alert cms-icon"></span>
		This will delete the current item <strong>(<span class="current-item-name">${editingItem.name}</span>)</strong> PLUS any shortcuts 
		to it <strong>PLUS ALL</strong> descendant items <strong>(total=<span class="num-descendants">_</span>)</strong>. Are you sure you want to do this?
	</p>
</div>

<div id="dialog-leftnav" class="hide">
	<div id="leftnav"></div>
</div>

<%-- This more complicated dialog deserves its own tag --%>
<edit:links-addlink />

<div id="searchresults-container" class="hide">
	<p>Pick an item in the list</p>
	<div id="searchresults"></div>
</div>

<audio id="bell" src="${applicationContextPath}/resources/pin-dropping.wav" preload="auto"></audio>

