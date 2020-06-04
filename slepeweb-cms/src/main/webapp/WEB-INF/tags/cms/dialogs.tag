<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="dialog-trash-confirm" class="hide" title="Delete item?">
	<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		This will delete the current item PLUS any shortcuts to it PLUS ALL child items. Are you sure you want to do this?
	</p>
</div>

<div id="link-errors-dialog" class="hide cms-dialog">
  <p>
    <span class="ui-icon ui-icon-circle-check cms-icon"></span>
    <span class="message message-a hide">Link data is incomplete. Close this dialog, and click on 'HELP' for guidance.</span>
    <span class="message message-b hide">A link from the current item to this target already exists.</span>
    <span class="message message-c hide">You cannot link to a child (binding) of the current item.</span>
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
