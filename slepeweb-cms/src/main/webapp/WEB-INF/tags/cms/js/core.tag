<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/core.tag */</cms:debug>

// Add behaviour to submit core item updates 
$("#core-button").click(function () {
	var isProduct = $("#itemIsProductFlag").val() == "true";
	var args = {
		name: $("#core-tab input[name='name']").val(),
		simplename: $("#core-tab input[name='simplename']").val(),
		template: $("#core-tab select[name='template']").val(),
		searchable: $("#core-tab input[name='searchable']").is(':checked'),
		published: $("#core-tab input[name='published']").is(':checked'),
		tags: $("#core-tab input[name='tags']").val()
	};
	
	if (isProduct) {
			args.partNum = $("#core-tab input[name='partNum']").val();
			args.price = $("#core-tab input[name='price']").val() * 100;
			args.stock = $("#core-tab input[name='stock']").val();
			args.alphaaxis = $("#core-tab select[name='alphaaxis']").val();
			args.betaaxis = $("#core-tab select[name='betaaxis']").val();
	}
	
	$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/core", {
		type: "POST",
		cache: false,
		data: args, 
		dataType: "json",
		success: function(obj, status, z) {
			flashMessage(obj);
		},
		error: function(json, status, z) {
			serverError();
		},
	});
});

