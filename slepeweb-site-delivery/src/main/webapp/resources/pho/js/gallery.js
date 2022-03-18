let _thumbnails, _slides, _currentSlideId = 0, _windowResizeTimer
let _slidePadding = 45

function openModal() {
	$("#modal").css("display", "grid")
	$("#thumbnail-gallery").css("display", "none")
}

function closeModal() {
	$(".slide-wrapper").css("display", "none")
	$("#thumbnail-gallery").css("display", "grid")
	$("#modal").css("display", "none")
}

function displayMedia(n) {
	let i = cycle(n)
  
	// Populate and display given slide
	sourceAndDisplay(i, true)
  
	// Populate and hide previous slide
	i = cycle(n - 1)
	sourceAndDisplay(i, false);
  
	// Populate and hide next slide
	i = cycle(n + 1)
	sourceAndDisplay(i, false)
}

function cycle(i) {
	let n = _slides.length
	let rem = i % n
	
	if (rem < 0) {
		return rem + n
	}
	return rem
}

function sourceMedia(i) {
	let slide = $(_slides[i])
  
  	if (slide.hasClass("image")) {
		if (! slide.attr("src")) {
			let src = $(_thumbnails[i]).attr("data-slide-src")
			slide.attr("src", src)
		}
	}
	else if (slide.hasClass("video")) {
		let sources = slide.find("source")
		
		if (sources.length == 0) {
			let src = $(_thumbnails[i]).attr("data-slide-src")
			slide.append(`<source src="${src}" type="video/mp4">`)
		}
	}
}

function sourceAndDisplay(i, display) {
	sourceMedia(i)
	let slide = $(_slides[i])
	slide.parent().css("display", display ? "grid" : "none")
}

function scaleDown(w, h) {
	let win = $(window)
	
	// These ratios will typically be less than zero
	let widthRatio = win.width() / w
	let heightRatio = (win.height() - _slidePadding) / h
	let scale = heightRatio < widthRatio ? heightRatio : widthRatio			
	return scale < 1 ? [w * scale, h * scale] : [w, h]
}

function assignUIBehaviours() {
	$("img").on("load", function() {
		let media = $(this)
		
		if (media.hasClass("slide")) {			
			console.log("Loading image", this.src, "width", this.width, "px")
			
			let w, h
			[w, h] = scaleDown(this.width, this.height)			
			media.css("width", `${w}px`)
			media.attr("data-width", this.width)
			media.attr("data-height", this.height)
			console.log("Adjusted image width for ", this.src, "to", w, "px")
		}
	})

	$("video").on("loadedmetadata", function() {
		let media = $(this)
		
		if (media.hasClass("slide")) {			
			console.log("Loading video", this.currentSrc, "width", this.videoWidth, "px")
			
			let w, h
			[w, h] = scaleDown(this.videoWidth, this.videoHeight)			
			media.css("width", `${w}px`)
			media.attr("data-width", this.videoWidth)
			media.attr("data-height", this.videoHeight)
			console.log("Adjusted video width for ", this.currentSrc)
		}
	})
	
	$("#tooltip-div").addClass("hide")
	
	$(".search-result").mouseenter(function() {
		$(this).find(".search-result-info").removeClass("hide")
	});			

	$(".search-result").mouseleave(function() {
		$(this).find(".search-result-info").addClass("hide")
	});
		
	$(".search-result img").click(function(){
		var indexStr = $(this).attr("data-id")
		
		if (indexStr) {
			_currentSlideId = parseInt(indexStr)
			displayMedia(_currentSlideId)
			openModal()
		}
	});
	
	$("#modal a.prev, #modal a.next").click(function(){
		let inc = parseInt($(this).attr("data-inc"))
		_currentSlideId = cycle(_currentSlideId + inc, _slides.length)
		displayMedia(_currentSlideId)
	})
	
	$(window).resize(function() {
		clearTimeout(_windowResizeTimer);
		_windowResizeTimer = setTimeout(updateMediaDimensions, 500);
	})	
	
	$("span.close-slide-info").click(function() {
		$("div.slide-info").css("display", "none")
		$("span.open-slide-info").css("display", "inline-block")
	})
	
	$("span.open-slide-info").click(function() {
		$("div.slide-info").css("display", "block")
		$("span.open-slide-info").css("display", "none")
	})
}

function updateMediaDimensions(){
	console.log("Window resize completed");
	let w, h
	_slides = $(".slide-set .slide")
	
	_slides.each(function(i) {
		let width = $(this).attr("data-width")
		let height = $(this).attr("data-height")
		
		if (width && height) {
			[w, h] = scaleDown(width, height)
			$(this).css("width", `${w}px`)
			console.log("Updated media display width, slide #", i)
		}
	})
}
