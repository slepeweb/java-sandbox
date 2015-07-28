<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/logozer.jsp --></gen:debug>

<script>
var logozer = {
		numImages: ${_comp.numImages},
		numCells: ${_comp.numCells},
		fadeInterval: ${_comp.fadeInterval},
		imageReplacementInterval: ${_comp.imageReplacementInterval},

		randomCell: function(last) {
			var num;
			do {
				num = Math.floor(Math.random() * logozer.numCells) + 1;
			}
			while (num == last);
			return num;
		},
		
		render: function(previous) {
			var divId, imgId, div, img, prevImg = previous.img;
			
			divId = logozer.randomCell(previous.divId);
			imgId = previous.imgId + 1;
			if (imgId > logozer.numImages) {
				imgId = 1;
			}
			
			div = $("#div_" + divId);
			img = $("#img_" + imgId);
			div.append(img);
			
			if (prevImg) {
				prevImg.fadeOut({duration: logozer.fadeInterval, complete: function() {
					img.fadeIn(logozer.fadeInterval);
				}});
			}
			else {
				img.fadeIn(logozer.fadeInterval);
			}
			
			return {divId: divId, imgId: imgId, img: img};
		},
		
		cycle: function(prev) {
			setInterval(function(){
				prev = logozer.render(prev);
			}, logozer.imageReplacementInterval);
		}
};

$(function(){
	var divId = logozer.randomCell(-1);
	var prev = logozer.render({divId: divId, imgId: 1, img: null});
	logozer.cycle(prev);
});
</script>

<div class="row">
	<c:forEach items="${_comp.components}" var="_img" varStatus="_status">
		<div id="div_${_status.count}" class="${_comp.numUsPerCell}u$ logozer">
			<img id="img_${_status.count}" src="${_img.src}" />
		</div>
	</c:forEach>
	
	<c:forEach items="${_comp.emptyCells}" var="_cell">
		<div id="div_${_cell}" class="${_comp.numUsPerCell}u$ logozer">
		</div>
	</c:forEach>
</div>
