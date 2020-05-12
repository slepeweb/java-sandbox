<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/move.tag --></cms:debug>

<div id="move-wrapper">
	<details>
		<summary>HELP</summary>
		<p>You need to set two things before the move can happen. 
		You must identify whether you want the item to be moved to a position before/below/after the target item,
		and the location in the content structure of the target item. These two things can be set in any order, and then
		you can action the move by clicking the corresponding button.</p>
	</details>	

	<div>
		<span>Move current item '${editingItem.name}', placing it </span>
		<span>
		<select name="position">
			<option value="none">Choose ...</option>
			<option value="before">Before</option>
			<option value="over">Below</option>
			<option value="after">After</option>
		</select>	
		</span>
		<i class="fas fa-bars itempicker" title="Pick a target from the content structure"></i>
		<span id="move-target-identifier">your chosen target</span> 
	</div>
	
	<div class="spacer20">
		<button id="move-item-button" disabled="disabled" type="button">Move item</button>
	</div>
</div>