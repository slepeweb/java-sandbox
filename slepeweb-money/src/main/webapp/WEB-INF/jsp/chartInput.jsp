<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
	
<c:set var="_outerTemplate">
   <tr class="category-group">
       <td class="width25">
       	<input id="group-[groupId]-name" type="text" name="group-[groupId]-name" value="[label]" />
       	<div class="trash-category-group"><i class="far fa-trash-alt" title="Remove this category group"></i></div>
       </td>
       <td>
       	__innerTemplate__
       </td>
   </tr>
</c:set>
	
<c:set var="_innerTemplate">
	<div>
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
	 	
	 	<span class="trash-category"><i class="far fa-trash-alt" title="Remove this category"></i></span>
	</div>
</c:set>
	
<c:set var="_categoryOptionsTemplate">
	<option value="[minor]" [selected]>[minor]</option>
</c:set>

<mny:standardLayout>
	<h2>Enter chart properties</h2>		
	<h3>Time window</h3>	
	
	<form method="post" action="${_ctxPath}/chart/by/categories/out">
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
		
		<input type="submit" value="Submit" />
	</form>			

</mny:standardLayout>

<script>
var _outerTemplate='${mon:compactMarkup(_outerTemplate)}';
var _innerTemplate='${mon:compactMarkup(_innerTemplate)}';
var _categoryOptionsTemplate='${mon:compactMarkup(_categoryOptionsTemplate)}';

$(function() {

	var _updateMinorCategories = function(majorEle) {
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
  
  var _addCategoryGroup = function(c) {
		var inner = "";
    for (var i = 1; i <= 3; i++) {
			inner += _innerTemplate.
				replace(/\[groupId\]/g, parseInt(c)).
				replace(/\[counter\]/g, parseInt(i)).
				replace("[major]", "").
				replace("__categoryOptionsTemplate__", "");
    }
    
		var outer = _outerTemplate.
			replace(/\[groupId\]/g, parseInt(c)).
			replace("[label]", "Group " + c).
			replace("__innerTemplate__", inner);
	    
	  if (c > 1) {
	  	$(".category-group").first().parent().append(outer);
 		}
	  else {
		  $("#category-groupings").append(outer);
	  }
	  
	  _applyCategoryChangeBehaviour();
	  _applyTrashingBehaviour();
  }
  
  var _applyCategoryChangeBehaviour = function() {
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
  
  var _applyTrashingBehaviour = function() {
	  $(".trash-category-group").off().click(function(e) {
		  $(this).parent().parent().remove();
	  });
	  
	  $(".trash-category").off().click(function(e) {
		  var input = $(this).prev().prev();
		  input.val("");
		  input.next().val("");
	  });
 }
  
  _applyCategoryChangeBehaviour();
    
  $("#add-group-button").click(function(e) {	
	  var numGroups = $(".category-group").length;
	  _addCategoryGroup(numGroups + 1);
	  e.stopPropagation();
	  _applyTrashingBehaviour();
  });
  
  _applyCategoryChangeBehaviour();
  _applyTrashingBehaviour();
});
</script>
