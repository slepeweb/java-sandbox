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
	
	$("#accordion").accordion({
		active: false,
		collapsible: true
	});
	
	$("#add2basket > button").click(function(e) {
		/*
		 * TODO: Must check axes have been selected
		 */
		var _alphaSelector = $("#alphaaxis-options");
		var _betaSelector = $("#betaaxis-options");
		var _data = {};
		
		if (_alphaSelector) {
			if (_alphaSelector.val() == "-1") {
				window.alert("Please select an alpha value");
				return;
			}
			else {
				_data.alphavalueid = _alphaSelector.val();
			}
		}
		
		if (_betaSelector) {
			if (_betaSelector.val() == "-1") {
				window.alert("Please select an beta value");
				return;
			}
			else {
				_data.betavalueid = _betaSelector.val();
			}
		}
		
		$.ajax("/rest/product/basket/add/" + _itemKey, {
			type: "POST",
			cache: false,
			data: _data,
			dataType: "text",
			success: function(response, status, z) {	
				$("#add2basket > span").html(response);
			},
			error: function(json, status, z) {
				console.log(z);
			},
		});
	});
	
});	
