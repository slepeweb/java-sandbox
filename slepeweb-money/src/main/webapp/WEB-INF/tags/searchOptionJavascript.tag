<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- searchOptionJavascript.tag -->	

<script>
	var _formMode = '${_formMode}';
	var _disableExecuteOption = function() {
		$('#execute-option input').prop('disabled', true);
		$('#execute-option').css('opacity', 0.5);
		$('#save-option input').prop('checked', true);
	}
	
	$(function() {	
		if (_formMode == 'create') {
			_disableExecuteOption();
		}
		
		$('input, select').change(function() {
			if (_formMode == 'update' && $(this).attr('name') != 'submit-option') {
				_disableExecuteOption();
			}
		});
	});
</script>
