$(function() {
	
	$("#alphaaxis-options").click(function(e) {	
		var _alphaSelector = $("#alphaaxis-options");
		var _betaSelector = $("#betaaxis-options");
		var s = '<option value="-1">Choose ...</option>';

		$.ajax("/rest/product/" + _itemKey + "/variants/" + _alphaSelector.val(), {
			type: "POST",
			cache: false,
			dataType: "json",
			success: function(obj, status, z) {				
				for (var i = 0; i < obj.options.length; i++) {
					s += '<option value="' + obj.options[i].value + '">' + obj.options[i].body + '</option>';
				}
				
				_betaSelector.empty();
				_betaSelector.html(s);
			},
			error: function(json, status, z) {
				console.log(z);
			},
		});
	});

});	
