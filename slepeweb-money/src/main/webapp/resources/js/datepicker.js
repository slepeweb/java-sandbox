$(function() {
		// Use jQuery datepicker widget for setting transaction dates
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
})