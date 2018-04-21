var _tinyNavStatus = false;

$(function() {
	
	$("#tiny-nav").click(function(e) {	
		var status = _tinyNavStatus;
		_tinyNavStatus = ! status;
		if (! status) {
			$('#primary-nav').addClass("data-toggle-on");
		}
		else {
			$('#primary-nav').removeClass("data-toggle-on");
		}
	});
	
	/*
	$(".parallax").paroller({
		factor: '0.5',
		type: 'foreground',
		direction: 'vertical'
	});
	*/
});	
