<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- multiCategoryJavascript.tag -->

<script>
	var storageSelector = ".multi-category-input #counter-store";
	var _outerTemplate='${mon:compactMarkup(_outerTemplate)}';
	var _innerTemplate='${mon:compactMarkup(_innerTemplate)}';
	var _categoryOptionsTemplate='${mon:compactMarkup(_categoryOptionsTemplate)}';
	var _elementCounter = "_counters";

	var addCategoryGroup = function(groupId) {
		var inner = _innerTemplate.
				replace(/\[groupId\]/g, parseInt(groupId)).
				replace(/\[counter\]/g, "1").
				replace("[major]", "").
				replace("__categoryOptionsTemplate__", "");
    
		var outer = _outerTemplate.
			replace(/\[groupId\]/g, parseInt(groupId)).
			replace("[label]", "Group " + groupId).
			replace("__innerTemplate__", inner);
		
		if (groupId > 1) {
			$(".multi-category-group").first().parent().append(outer);
		}
		else {
			$("#multi-category-groupings").append(outer);
		}
		
		var counters = retrieve();
		var nextId = getNextGroupId(counters);
		counters.push({groupId: nextId, categoryCount: 1, lastCategoryId: 1});
		_storeCounters(counters);
		_resetCategoryChangeBehaviour();
		_resetButtonClickBehaviours();
	}
  
	var addCategory = function(button) {
		var groupIdStr = button.attr("data-groupid");
		var groupId = parseInt(groupIdStr);
		var categoryId = -1;
		
		// Identify the corresponding array element (allowing for group deletions)	  
		var counters = retrieve();
		var group = getGroup(counters, groupId);
		
		if (group) {
			group.categoryCount += 1;
			group.lastCategoryId += 1;
			categoryId = group.lastCategoryId;
			_storeCounters(counters);
		}
		
		var inner = _innerTemplate.
			replace(/\[groupId\]/g, groupIdStr).
			replace(/\[counter\]/g, categoryId.toString()).
			replace("[major]", "").
			replace("__categoryOptionsTemplate__", "");
		
		$(inner).insertBefore(button);
		_resetCategoryChangeBehaviour();
		_resetButtonClickBehaviours();
	}
	
	// Event handlers (behavious)
	var _resetCategoryChangeBehaviour = function() {
		$("input[id^='major']").off().change(function(e) {	
			var promet = _updateMinorCategories($(this));
			promet.done(function(res){
				//window.alert(res);
			});
			
			promet.fail(function(res){
				//window.alert(res);
			});
		});	  
	}
	
	var _resetButtonClickBehaviours = function() {
		$("#add-group-button").off().click(function(e) {	
			var counters = retrieve();
			addCategoryGroup(getNextGroupId(counters));
			e.stopPropagation();
		});
		
		$(".add-category-button").off().click(function(e) {
			addCategory($(this));
			e.stopPropagation();
		});
		
		$(".trash-multi-category-group").off().click(function(e) {
			var groupIdStr = $(this).attr("data-groupid");
			$(this).parent().parent().remove();
			var counters = retrieve();
			remove(counters, parseInt(groupIdStr));
			_storeCounters(counters);
		});
		
		$(".trash-category").off().click(function(e) {
			var groupIdStr = $(this).attr("data-groupid");
			$(this).parent().remove();
			var counters = retrieve();
			var group = getGroup(counters, parseInt(groupIdStr));
			if (group != null) {
				group.categoryCount -= 1;
				_storeCounters(counters);
			}
		});
	}
	
	var _storeCounters = function(counters) {
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
	
	$(function(){
		$(".multi-category-group").each(function(i) {
			var cCount = $(this).find(".category-inputs").length;
			var group = {groupId: i + 1, categoryCount: cCount, lastCategoryId: cCount};
			counters.push(group);
		});
		
		_storeCounters(counters);
		_resetCategoryChangeBehaviour();
		_resetButtonClickBehaviours();
	});

</script>