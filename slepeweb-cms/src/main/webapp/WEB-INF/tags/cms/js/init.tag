<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/init.tag */</cms:debug>

// nodeKey is an argument in renderItemForms function call
_editingItemId = nodeKey;

var tabsdiv = $("#item-editor");
tabsdiv.empty().append(html);

// Re-build tabs 
if (tabsdiv.hasClass("ui-tabs")) {
	$("#item-editor").tabs("destroy");
}

// Focus on same tab as previous render
var tabName;
var tabNum = 0;
var activeTabId = 0;

if (activeTab) {
	$("#editor-tabs a").each(function() {
		tabName = $(this).attr("href").substring(1);
		if (activeTab == tabName) {
			activeTabId = tabNum;
		}
		tabNum++;
	});
}

$("#item-editor").tabs({active: activeTabId});

// Add behaviour to template & itemtype selectors 
$("#add-tab select[name='template']").change(function (e) {
	var typeSelector = $("#add-tab select[name='itemtype']");
	if ($(e.target).val() != "0") {
		typeSelector.val("0");
		typeSelector.attr("disabled", "true");
	}
	else {
		typeSelector.removeAttr("disabled");
	}
});
