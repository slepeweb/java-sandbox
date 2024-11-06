let _thumbnails, _slides, _windowResizeTimer;
let _slidePadding = 45;
let _cursor;
let _rightArrowSel = '#modal a.next';
let _leftArrowSel = '#modal a.prev';

function openModal() {
	$("#modal").css("display", "grid")
	$("#thumbnail-gallery").css("display", "none")
}

function closeModal() {
	$(".slide-wrapper").css("display", "none")
	$("#thumbnail-gallery").css("display", "grid")
	$("#modal").css("display", "none")
	pauseCurrentPlayingVideo()
}

function pauseCurrentPlayingVideo() {		
	// If current slide is a video, and is not paused, then pause it
	let slide = _slides.get(_cursor.currentId)
	if (slide.tagName === 'VIDEO' && ! slide.paused) {
		slide.pause()
	}
}

function displayMedia(id) {  
	if (id) {
		_cursor.currentId = id;
	}
	
	setArrowVisibility()
		
	// Populate and display given slide
	sourceAndDisplay(_cursor.currentId, true)
  
  if (_cursor.hasPrevious()) {
		// Populate and hide previous slide
		sourceAndDisplay(_cursor.currentId - 1, false);
	}
	  
 	if (_cursor.hasNext()) {
		// Populate and hide next slide
		sourceAndDisplay(_cursor.currentId + 1, false)
	}
}

function sourceMedia(id) {
	let slide = $(_slides[id])
  
  if (slide.hasClass("image")) {
		if (! slide.attr("src")) {
			let src = $(_thumbnails[id]).attr("data-slide-src")
			slide.attr("src", src)
		}
	}
	else if (slide.hasClass("video")) {
		let sources = slide.find("source")
		
		if (sources.length == 0) {
			let src = $(_thumbnails[id]).attr("data-slide-src")
			slide.append(`<source src="${src}" type="video/mp4">`)
		}
	}
}

function sourceAndDisplay(id, display) {
	sourceMedia(id)
	let slide = $(_slides[id])
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

function setArrowVisibility() {
	if (_cursor.hasNext()) {
		$(_rightArrowSel).removeClass("disabled");
	}
	else {
		$(_rightArrowSel).addClass("disabled");
	}
	
	if (_cursor.hasPrevious()) {
		$(_leftArrowSel).removeClass("disabled");
	}
	else {
		$(_leftArrowSel).addClass("disabled");
	}
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
		
	$(".search-result").click(function() {
		var indexStr = $(this).attr("data-id")
		
		if (indexStr !== undefined) {
			displayMedia(parseInt(indexStr))
			openModal()
		}
	});
	
	$(_leftArrowSel + ', ' + _rightArrowSel).click(function() {
		pauseCurrentPlayingVideo()
		
		if (! $(this).hasClass("disabled")) {
			let inc = parseInt($(this).attr("data-inc"))
			if (_cursor.increment(inc)) {
				displayMedia()
			}
		}
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

class Cursor {
	constructor(n, size) {
		this.size = size;
		this.currentId = n;
	}
	
	hasNext() {
		return this.currentId < (this.size - 1);
	}
	
	hasPrevious() {
		return this.currentId > 0;
	}
	
	increment(inc) {
		if (inc > 0 && this.hasNext()) {
			this.currentId++;
			return true;
		}
		else if (inc < 0 && this.hasPrevious()) {
			this.currentId--;
			return true;
		}
		
		return false;
	}
}

$(function() {
	_thumbnails = $("div.search-result img")
	_slides = $("div.slide-set .slide")
	_cursor = new Cursor(0, _slides.length)
	assignUIBehaviours()
});

