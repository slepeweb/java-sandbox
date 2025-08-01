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
		let figwidth = width ? `style="width: ${width}"` : '';
		let title = 'Click to see image in new tab';
		let body = div$.html();
		let caption = div$.attr('data-caption');
		let floatAttr = div$.attr('data-float');

		$.ajax('/rest/item/' + origId, {
			type: 'GET',
			cache: false,
			dataType: "json",
			success: function(resp) {
				if (! resp.error) {
					if (resp.data.image) {
						// resp.data is an Item4Json object
						// Append img and caption tags to the div
						if (! caption) {
							caption = _chooseBestCaption(resp.data);
						}
						
						let path = '/$_' + origId;
						let figclass = floatAttr ? 'class="right"' : '';
						
						div$.removeAttr('data-width');
						div$.removeAttr('data-caption');
						div$.removeAttr('data-float');
						
						div$.html(`<figure ${figclass} ${figwidth}>
								<a href="${path}" target="_blank" title="${title}"><img src="${path}"></a>
								<figcaption>${caption}</figcaption>
							</figure>
							${body}
							<p class="clearfix"></p>`);
					}
					else {
						div$.attr('data-error', 'Not an image item');
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
		User must add to content, eg: 
		
		<div class="ximg" data-id="4230" data-caption="Newington Green U11s" />
		
		This will get converted to :
			
		<div class="ximg" data-id="3324">
			<figure>
				<a href="/$_3324" target="_blank" title="Click to see image in new tab"><img src="/$_3324"></a>
				<figcaption>Newington Green U11s</figcaption>
			</figure>									
			<p class="clearfix"></p>
		</div>	
	
	$('div.ximg').each(function() {
		_processFigures($(this));
	})
	*/
	
});
