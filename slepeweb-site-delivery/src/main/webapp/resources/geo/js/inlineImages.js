function _chooseBestCaption(data) {
	let caption = data.fieldValues.caption;

	if (! caption) {
		caption = data.fieldValues.teaser;
	}

	if (! caption) {
		caption = data.name;
	}
	
	return caption;
}

function _processFigures(div$) {
		let origId = div$.attr('data-id');
		let width = div$.attr('data-width');
		let body = div$.html();

		$.ajax('/rest/passkey/' + origId, {
			type: 'GET',
			cache: false,
			dataType: "json",
			success: function(resp) {
				if (! resp.error && resp.data.image) {
					// resp.data is an Item4Json object
					// Append img and caption tags to the div
					let w = body ? '' : (width ? `style="width: ${width}"` : '');
					div$.html(`<figure><a href="${resp.data.url}" target="_blank" title="Click to see image in new tab"><img src="${resp.data.url}" ${w} /></a>
						<figcaption ${w}>${_chooseBestCaption(resp.data)}</figcaption></figure>`);
						
					if (body) {
						div$.append(`<p>${body}</p>`);
					}
				}
				else {
					div$.attr('data-error', resp.message);
				}
			},
			error: function(msg) {
				console.log(msg);
			}
		})
}

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
	$('div.ximg').each(function() {
		_processFigures($(this));
	})
	
});
