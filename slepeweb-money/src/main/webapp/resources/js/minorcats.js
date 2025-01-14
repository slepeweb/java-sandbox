class MinorCategoriesService {
	
	updateMinorCategories(majorEle) {
		var deferred = $.Deferred();
		var majorVal = majorEle.find(':selected').val();
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
			success: function(obj) {
				var select = $("select[name='minor" + (split ? "_" + index : "") + "']");
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
	
	resetMajorCategoryChangeBehaviours() {
		var fn = this.updateMinorCategories;
		$("select[name^='major']").off().change(function() {	
			var promet = fn($(this));
			promet.done(function(res) {
				//window.alert(res);
			});
			
			promet.fail(function(res) {
				//window.alert(res);
			});
		});
	}
}
		
$(function(){
	_money.service.minorcats = new MinorCategoriesService();
	_money.service.minorcats.resetMajorCategoryChangeBehaviours();
});
