<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/support.tag */</cms:debug>

var toStatus = function(err, msg) {
	var obj = {};
	obj.error = err;
	obj.message = msg;
	return obj;
};

/*
 * Standard error message.
 */
var serverError = function() {
	flashMessage(toStatus(true, "Server error"));
};

/*
 * Displays a flash message, in red for errors, and green for info messages.
 */
var flashMessage = function(status) {
	var clazz, msg;
	if (status) {
		clazz = status.error ? "red" : "green";
		msg = status.message;
	}
	else {
		clazz = "red";
		msg = "";
	}
	
	$("#status-block").removeClass("red").removeClass("green").addClass(clazz).append(msg);
	$("#bell").get(0).play();
};

var pageEditorUrlPrefix = _ctx + "/page/editor/";

var displayCommerceElements = function(target) {
	var ele = target.find("option:selected");
	var flag = ele.attr("data-isproduct");

	if (flag == 1) {
		$("#core-commerce").css("display", "block");
	}
	else {
		$("#core-commerce").css("display", "none");
	}
};

/*
 * Tells the browser to get the item editor page for a given item.
 */
var fetchItemEditor = function(nodeKey, status) {
	var url = pageEditorUrlPrefix + nodeKey;
	var param = status.error ? "error" : "success";
	url += ("?status=" + param + "&msg=" + status.messageEncoded);	
	window.location = url; 
};
	
// Get form field names and values for forms on item-editor 
var getFieldsFormInputData = function() {
	var result = {};
	var language = $("#field-language-selector").val();
	if (! language) {
		language = _siteDefaultLanguage;
	}
	
	result["language"] = language;
	var selector = "#form-fields-lang input, #form-fields-lang textarea, #form-fields-lang select";
	selector = selector.replace(/lang/g, language);
	
	$(selector).each(function(i, obj) {
		var ctrl = $(obj);
		var type = ctrl.attr("type");
		var param = ctrl.attr("name");
		var str;
		if (type == "radio") {
			if (ctrl.is(':checked')) {
				result[param] = ctrl.val();
			}
		}
		else if (type == "checkbox") {
			if (ctrl.is(':checked')) {
				str = result[param];
				if (! str) {
					str = "";
				}
				if (str.length > 0) {
					str += "|";
				}
				str += ctrl.val();
				result[param] = str;
			}
		}
		else {
			result[param] = ctrl.val();
		}
	});
	return result;
};

/*
 * Shows a modal giving the user a chance to confirm his selection.
 */
var showDialog = function(id, relocate) {
	$("#" + id).dialog({
		modal: true,
		buttons: {
			Ok: function() {
				$(this).dialog("close");
				if (relocate) {
					window.location = relocate;
				}
			}
		}
	});
};

/* Nodes representing shortcuts in the FancyTree have '.s' appended to their standard key value,
 * so that they are distinguishable from the node representing the 'real' item. This method
 * identifies the numeric part preceding the '.s' suffix.
*/
var removeShortcutMarker = function(key) {
	var cursor = key.indexOf(".s");
	if (cursor > -1) {
		return key.substring(0, cursor);
	}
	return key;
};
