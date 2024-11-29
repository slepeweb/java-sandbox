$(function() {
	/*
		Insert img + caption given an item origId that corresponds to an image item
		User must add to content, eg: <div class="ximg" data-id="4230" />
		This will get converted to :
			<div class="ximg" data-id="4230">
				<img src="[//optional hostname]/path[?optional-passskey]"
				<caption>Text content from image item</caption>
			</div>
	*/
	let shortUrlImages$ = $('div.ximg');
	
	if (shortUrlImages$.length > 0) {
		shortUrlImages$.each(function() {
			let div$ = $(this);
			let origId = div$.attr('data-id');
			
			$.ajax('/rest/passkey/' + origId, {
				type: 'GET',
				cache: false,
				dataType: "json",
				success: function(resp, status, z) {
					if (! resp.error && resp.data.image) {
						// resp.data is an Item4Json object
						// Append img and caption tags to the div
						div$.append(`<img src="${resp.data.url}" />`);
						let caption = resp.data.fieldValues.caption;
						if (! caption) {
							caption = resp.data.fieldValues.teaser;
						}
						if (! caption) {
							caption = resp.data.name;
						}
						div$.append(`<caption>${caption}</caption>`);
					}
					else {
						img$.attr('data-error', resp.message);
					}
				},
				error: function(resp, status, z) {
					console.log(resp.message);
				}
			});
		});	
	}
});
