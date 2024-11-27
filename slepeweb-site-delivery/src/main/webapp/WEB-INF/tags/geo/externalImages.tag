<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<script>
let _passkey = null;

$(function() {
	let images$ = $('img.xsite');
	
	if (images$.length > 0) {
		images$.each(function() {
			let img$ = $(this);
			let datasrc = img$.attr('data-src');
			
			$.ajax('/rest/passkey?targeturl=' + datasrc, {
				type: 'GET',
				cache: false,
				dataType: "json",
				success: function(resp, status, z) {
					if (! resp.error) {
						let src = resp.data ? datasrc + '?passkey=' + encodeURI(resp.data) : datasrc;
						img$.attr('src', src);
						img$.removeAttr('data-src');
					}
					else {
						img$.attr('data-error', resp.message);
						img$.attr('title', 'Not authorized');
					}
				},
				error: function(resp, status, z) {
					console.log(resp.message);
				}
			})
		});
	}
});
</script>