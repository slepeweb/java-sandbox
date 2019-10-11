<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- multiSplitJavascript.tag -->

<script>
	var storageSelector = "#counter-store";
	var _innerTemplate='${mon:compactMarkup(_innerTemplate)}';
	var _minorCategoryOptionsTemplate='${mon:compactMarkup(_minorCategoryOptionsTemplate)}';
	var _elementCounter = "_splitCounters";

	var addSplit = function(button) {
		var counters = retrieve();
		counters.splitCount += 1;
		counters.lastSplitId += 1;
		var splitId = counters.lastSplitId;
		_storeSplitCounters(counters);
		
		var inner = _innerTemplate.
			replace(/\[counter\]/g, splitId.toString()).
			replace(/\[major\]/, "").
			replace(/\[memo\]/, "").
			replace(/\[amount\]/, "");
		
		$(inner).insertBefore(button);
		_resetSplitButtonClickBehaviours();
		_resetMajorCategoryChangeBehaviours();
	}
	
	var _resetSplitButtonClickBehaviours = function() {
		$("#add-split-button").off().click(function(e) {
			addSplit($(this));
		});
		
		$(".trash-split").off().click(function(e) {
			$(this).parent().remove();
			var counters = retrieve();
			counters.splitCount -= 1;
			_storeSplitCounters(counters);
		});
	}
	
	var _storeSplitCounters = function(counters) {
		$(storageSelector).val(JSON.stringify(counters));
	}
	
	var retrieve = function() {
		var objStr = $(storageSelector).val();
		if (objStr) {
			return JSON.parse(objStr);
		}
		
		return {};
	}
	
	var getNextSplitId = function(counters, groupId) {
		return counters.lastSplitId + 1;
	}
	
	var _splitCounters = null;
	
	$(function(){
		var num = $(".split-inputs").length;
		_splitCounters = {splitCount: num, lastSplitId: num};
		
		_storeSplitCounters(_splitCounters);
		_resetSplitButtonClickBehaviours();
	});

</script>