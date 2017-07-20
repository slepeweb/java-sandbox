<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/fields.tag */</cms:debug>

// Add behaviour to submit item field updates 
$("#field-button").click(function () {
	var theDialog = $("#dialog-fields-confirm");
	theDialog.dialog({
		resizable: false,
		height:200,
		modal: true,
		buttons: {
			"Update field values": function() {
				$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/fields", {
					type: "POST",
					cache: false,
					data: getFieldsFormInputData(), 
					dataType: "json",
					success: function(obj, status, z) {
						theDialog.dialog("close");
						flashMessage(obj);
					},
					error: function(obj, status, z) {
						theDialog.dialog("close");
						serverError();
					},
				});
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	});
});
