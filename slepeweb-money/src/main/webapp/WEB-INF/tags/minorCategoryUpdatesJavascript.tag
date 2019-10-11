<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- minorCategoryUpdatesJavascript.tag -->

<script>
	var _updateMinorCategories = function(majorEle) {
		var deferred = $.Deferred();
		var majorVal = majorEle.val();
		var name = majorEle.attr("name");
		var split = name.length > 5;
		var index = -1;
		
		if (split) {
			index = name.substring("major".length + 1);
		}
		
		$.ajax(webContext + "/rest/category/minor/list/" + majorVal, {
			type: "GET",
			cache: false,
			dataType: "json",
			success: function(obj, status, z) {
				var select = $("select[name='minor" + (split ? "-" + index : "") + "']");
				select.empty();
				$.each(obj.data, function(index, minor) {
					select.append("<option value='" + minor + "'>" + minor + "</option>");
				});
				deferred.resolve("Categories updated");
			},
			error: function(x, t, m) {
				deferred.reject(x + t + m);
			}
		});
		return deferred.promise();
	}
	
	var _resetMajorCategoryChangeBehaviours = function() {
		$("input[name^='major']").off().change(function(e) {	
			var promet = _updateMinorCategories($(this));
			promet.done(function(res){
				//window.alert(res);
			});
			
			promet.fail(function(res){
				//window.alert(res);
			});
		});
	}
		
	$(function(){
		_resetMajorCategoryChangeBehaviours();
	});
</script>