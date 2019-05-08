<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
	
<c:set var="_outerTemplate">
   <tr class="category-group">
       <td class="width25">
       	<input id="group-[groupId]-name" type="text" name="group-[groupId]-name" value="[label]" />
       </td>
       <td>
					__innerTemplate__
	       	
					<button class="add-category-button" type="button" data-groupid="[groupId]">+ category</button>
       </td>
       <td class="trash-category-cell">
       		<div class="trash-category-group" data-groupid="[groupId]"><i class="far fa-trash-alt" title="Remove this group"></i></div>
       </td>
   </tr>
</c:set>
	
<c:set var="_innerTemplate">
	<div class="category-inputs">
		<span class="inline">[counter]</span>
	 	<input class="width25 inline" 
	 		id="major-[groupId]-[counter]" 
	 		type="text" 
	 		name="major-[groupId]-[counter]" 
	 		list="majors" 
	 		value="[major]" />
	 		
	 	<select class="width50 inline" id="minor-[groupId]-[counter]" name="minor-[groupId]-[counter]">
	 	__categoryOptionsTemplate__
	 	</select>
	 		 	
	 	<span class="trash-category" data-groupid="[groupId]"><i class="far fa-trash-alt" title="Remove this category"></i></span>
	</div>
</c:set>
	
<c:set var="_categoryOptionsTemplate">
	<option value="[minor]" [selected]>[minor]</option>
</c:set>

<mny:standardLayout>
	<h2>Enter chart properties</h2>		
	<h3>Time window</h3>	
	
	<form class="chart" method="post" action="${_ctxPath}/chart/by/categories/out">
		<table>
		    <tr>
		        <td class="heading"><label for="from">From year</label></td>
		        <td><input id="from" type="text" name="from" placeholder="Enter (for example) '2000'"
		        	value="${_chartProps.fromYear}" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="numYears">No. of years</label></td>
		        <td><input id="numYears" type="text" name="numYears" placeholder="Enter (for example) '10'"
		        	value="${_chartProps.numYears}" /></td>
		    </tr>
		</table>
		
		<h3>Category groupings</h3>	
		
		<datalist id="majors">
				<c:forEach items="${_categories}" var="_m">
						<option value="${_m.name}" />
				</c:forEach>
		</datalist>
		
		<table id="category-groupings">
			${mon:buildChartPropertyMarkup(_chartProps, _outerTemplate, _innerTemplate, _categoryOptionsTemplate)}
		</table>
		
		<button id="add-group-button" type="button">+ group</button>
		<br />
		
		<input id="counter-store" type="hidden" name="counterStore" value="" />
		<input type="submit" value="Submit" />
	</form>			

</mny:standardLayout>

<script>
var _outerTemplate='${mon:compactMarkup(_outerTemplate)}';
var _innerTemplate='${mon:compactMarkup(_innerTemplate)}';
var _categoryOptionsTemplate='${mon:compactMarkup(_categoryOptionsTemplate)}';
var _elementCounter = "_counters"

$(function() {

	/*
		Given the major category in the <input> element, update the associated <select>
		element with corresponding minor values
	*/
	var updateMinorCategories = function(majorEle) {
	  var deferred = $.Deferred();
		var minorEle = majorEle.next();
		
		if (majorEle.val().length > 0) {
			$.ajax(webContext + "/rest/category/minor/list/" + majorEle.val(), {
				type: "GET",
				cache: false,
				dataType: "json",
				success: function(obj, status, z) {
					minorEle.empty();
					$.each(obj.data, function(index, minor) {
						var markup = _categoryOptionsTemplate.replace(/\[minor\]/g, minor).replace(/\[selected\]/, "");
						minorEle.append(markup);
					});
					
					deferred.resolve("Categories updated");
				},
				error: function(x, t, m) {
					deferred.reject(x + t + m);
				}
			});
	  }
	  else {
		  minorEle.empty();
	  }
		
		return deferred.promise();
  }
  
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
	  	$(".category-group").first().parent().append(outer);
 		}
	  else {
		  $("#category-groupings").append(outer);
	  }
	  
	  var counters = retrieve();
	  var nextId = getNextGroupId(counters);
	  counters.push({groupId: nextId, categoryCount: 1, lastCategoryId: 1});
	  store(counters);
	  
	  resetCategoryChangeBehaviour();
	  resetButtonClickBehaviours();
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
		  store(counters);
	  }
	  
		var inner = _innerTemplate.
				replace(/\[groupId\]/g, groupIdStr).
				replace(/\[counter\]/g, categoryId.toString()).
				replace("[major]", "").
				replace("__categoryOptionsTemplate__", "");
  		  
		var lastDiv = button.prev();
		lastDiv.append(inner);
	  	  		  
	  resetCategoryChangeBehaviour();
	  resetButtonClickBehaviours();
}

  // Event handlers (behavious)
  var resetCategoryChangeBehaviour = function() {
	  $("input[id^='major']").off().change(function(e) {	
		  var promet = updateMinorCategories($(this));
		  promet.done(function(res){
			  //window.alert(res);
		  });
		  
		  promet.fail(function(res){
			  //window.alert(res);
		  });
		});	  
 }
  
  var resetButtonClickBehaviours = function() {
	  $("#add-group-button").off().click(function(e) {	
		  var counters = retrieve();
		  addCategoryGroup(getNextGroupId(counters));
		  e.stopPropagation();
	  });

	  $(".add-category-button").off().click(function(e) {
		  addCategory($(this));
		  e.stopPropagation();
	  });
	  
	  $(".trash-category-group").off().click(function(e) {
		  var groupIdStr = $(this).attr("data-groupid");
		  $(this).parent().parent().remove();
		  var counters = retrieve();
		  remove(counters, parseInt(groupIdStr));
		  store(counters);
		});

	  $(".trash-category").off().click(function(e) {
		  var groupIdStr = $(this).attr("data-groupid");
		  $(this).parent().remove();
		  var counters = retrieve();
		  var group = getGroup(counters, parseInt(groupIdStr));
		  if (group != null) {
			  group.categoryCount -= 1;
			  store(counters);
		  }
	  });
}
  
  resetCategoryChangeBehaviour();
  resetButtonClickBehaviours();
  
  <mny:chartFormSupport />
});
</script>
