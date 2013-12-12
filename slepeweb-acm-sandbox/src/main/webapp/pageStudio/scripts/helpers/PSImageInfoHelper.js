var PSImageInfoHelper = {	
	setImageDimensions: function(imageJSON, e) {
		Event.stopObserving(this);
		imageJSON.width = this.width;
		imageJSON.height = this.height;
	},
	
	loadInfo: function(imageJSON) {
		var image = new Image();
		Event.observe(image, "load", PSImageInfoHelper.setImageDimensions.bind(image, imageJSON));
		image.src = imageJSON.src;
	}
};