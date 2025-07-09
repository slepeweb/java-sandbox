$(function() {
	
	$('a.xlink').click(function(e) {
		e.preventDefault()
		let link$ = $(this)

		$.ajax('/rest/passkey', {
			type: 'GET',
			cache: false,
			dataType: "json",
			success: function(resp) {
				if (! resp.error) {
					let href = link$.attr('href')
					let updatedHref = `${href}?_passkey=${resp.data[0]}`
					window.open(updatedHref, '_blank')
				}
			},
			error: function(msg) {
				console.log(msg);
			}
		})
	})	
});
