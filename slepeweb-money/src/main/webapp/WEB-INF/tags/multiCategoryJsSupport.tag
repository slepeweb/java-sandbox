<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

var storageSelector = ".multi-category-input #counter-store";

var store = function(counters) {
	$(storageSelector).val(JSON.stringify(counters));
}

var retrieve = function() {
	var objStr = $(storageSelector).val();
	if (objStr) {
		return JSON.parse(objStr);
	}
	
	return [];
}

var getNextGroupId = function(counters) {
	return (counters[counters.length - 1].groupId + 1);
}

var getNextCategoryId = function(counters, groupId) {
	var group = getGroup(counters, groupId);
	return group.lastCategoryId + 1;
}

var getGroup = function(counters, groupId) {
	for (var i = 0; i < counters.length; i++) {
		if (counters[i].groupId == groupId) {
			return counters[i];
		}
	}
	return null;
}

var remove = function(counters, groupId) {
	for (var i = 0; i < counters.length; i++) {
		if (counters[i].groupId == groupId) {
			counters.splice(i, 1);
		}
	}
}

var counters = [];

$(".multi-category-group").each(function(i){
	var cCount = $(this).find(".category-inputs").length;
	var group = {groupId: i + 1, categoryCount: cCount, lastCategoryId: cCount};
	counters.push(group);
});

store(counters);

