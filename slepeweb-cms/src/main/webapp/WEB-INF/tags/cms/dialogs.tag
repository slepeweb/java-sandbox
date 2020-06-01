<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="dialog-trash-confirm" class="hide" title="Delete item?">
	<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		This will delete the current item PLUS any shortcuts to it PLUS ALL child items. Are you sure you want to do this?
	</p>
</div>

<div id="dialog-choose-linktype" class="hide" title="Provide full link data">
  <p>
    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
    Link data is incomplete. Close this dialog, and click on 'HELP' for guidance.
  </p>
</div>

<div id="dialog-move-confirm" class="hide" title="Move item?">
	<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		You are about to move an item in the content structure. Are you sure you want to do this?
	</p>
</div>

<div id="dialog-fields-confirm" class="hide" title="Update fields?">
	<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		You are about to update field data, which CANNOT BE UNDONE. Are you sure you want to do this?
	</p>
</div>

<div id="dialog-leftnav" class="hide" title="Main navigation">
	<div id="leftnav"></div>
</div>

<edit:links-addlink />

<audio id="bell" src="${applicationContextPath}/resources/pin-dropping.wav" preload="auto"></audio>
