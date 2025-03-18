<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

var _simpleDialogCreator = function(selector, title) {
	var _dialog = $(selector).dialog({
		  autoOpen: false,
		  height: 250,
		  width: 400,
		  modal: true,
		  title: title,
		  buttons: {
			  Ok: function() {
				  $(this).dialog("close");
			  }
		  }
	});
	
	return _dialog;
}