<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- searchAndExecuteOptions.tag -->

<mny:tableRow heading="Form submission action" trclass="form-submission-options">
	<div>
		<span id="save-option" class="radio-option"><input type="radio" name="submit-option" value="save" checked="checked" /> Save</span>
		<span id="save-execute-option" class="radio-option"><input type="radio" name="submit-option" value="save-execute" /> Save then execute</span>
		<span id="execute-option" class="radio-option"><input type="radio" name="submit-option" value="execute" /> Execute</span>
	</div>
</mny:tableRow>