<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/logozer.jsp --></gen:debug>

<script>
var logozer = {
		numImages: ${_comp.numImages},
		numCells: ${_comp.numCells},
		fadeInterval: ${_comp.fadeInterval},
		imageReplacementInterval: ${_comp.imageReplacementInterval},
		nextCellOffset: ${_comp.nextCellOffset},

		randomCell: function(last) {
			var num;
			do {
				num = logozer.random(logozer.numCells);
			}
			while (Math.abs(num - last) < logozer.nextCellOffset);
			return num;
		},
		
		random: function(range) {
			return Math.floor(Math.random() * range) + 1;
		},
		
		render: function(previous) {
			var divId, imgId, div, img;
			divId = previous.divId == -1 ? 1 : logozer.randomCell(previous.divId);
			imgId = previous.imgId + 1;
			if (imgId > logozer.numImages) {
				imgId = 1;
			}
			
			div = $("#div_" + divId);
			img = $("#img_" + imgId);
			div.append(img);
			
			if (previous.img) {
				previous.img.fadeOut({duration: logozer.fadeInterval, always: function() {
					logozer.log("Faded-out img " + previous.imgId + " [" + previous.img.attr("id") + "]");
					img.fadeIn(logozer.fadeInterval);
					logozer.log("Faded img " + imgId + " [" + img.attr("id") + "] into div " + divId + "[" + div.attr("id") + "]");
				}});
			}
			else {
				img.fadeIn(logozer.fadeInterval);
				logozer.log("Faded img " + imgId + " into div " + divId);
			}
			
			return {divId: divId, imgId: imgId, img: img};
		},
		
		cycle: function(prev) {
			setInterval(function(){
				prev = logozer.render(prev);
			}, logozer.imageReplacementInterval);
		},
		
		log: function(s) {
			/*
			if (console) {
				console.log(s);
			}
			*/
		}
};

$(function(){
	var prev = logozer.render({
		divId: logozer.randomCell(-1), 
		imgId: logozer.random(logozer.numImages), 
		img: null
	});
	
	logozer.cycle(prev);
});
</script>

<div class="row">
	<c:forEach items="${_comp.cellIds}" var="_cellId" varStatus="_status">
		<div id="div_${_cellId}" class="${_comp.cellClass} logozer">
			<c:if test="${_cellId le _comp.numImages}">
				<img id="img_${_cellId}" src="${_comp.components[_cellId - 1].src}" />
			</c:if>
			<c:if test="${_status.last and _cellId < _comp.numImages}">
				<c:forEach items="${_comp.imageIds}" var="_imgId" begin="${_cellId}">
					<img id="img_${_imgId}" src="${_comp.components[_imgId - 1].src}" />
				</c:forEach>
			</c:if>
		</div>
	</c:forEach>
</div>
