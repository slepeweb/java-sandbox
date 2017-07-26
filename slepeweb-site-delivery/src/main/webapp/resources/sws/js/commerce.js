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

	$(".thumbnail").click(function(e) {
		$(".thumbnail").removeClass("border");
		var thumbSrc = $(this).addClass("border").attr("src");
		var c = thumbSrc.indexOf("?");
		if (c > -1) {
			thumbSrc = thumbSrc.substring(0, c);
		}
		
		$(".main-image").attr("src", thumbSrc);
		
		$.ajax("/rest/product/" + _itemKey + "/has-hifi", {
			type: "POST",
			data: {
				baseImagePath: thumbSrc
			},
			cache: false,
			dataType: "text",
			success: function(response, status, z) {	
				if (response) {
					$(".main-image").attr("data-magnify-src", response).addClass("zoom");
					_zoomer = $(".zoom").magnify();
				}
				else {
					_zoomer.destroy();					
					$(".main-image").removeAttr("data-magnify-src").removeClass("zoom");
				}
			},
			error: function(json, status, z) {
				console.log(z);
			},
		});
	});
	
	_zoomer = $('.zoom').magnify();
});	
