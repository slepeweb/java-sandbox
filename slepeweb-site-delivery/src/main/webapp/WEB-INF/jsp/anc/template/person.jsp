<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	.passport-photo {
		margin-right: 1em;
		margin-bottom: 0.2em;
		width: 200px;
	}
	
	#partner-options {
		margin-top: 0.8em;
		font-size: 0.8em;
	}
	
	#partner-options span {
		margin-left: 0.5em;
		margin-right: 1.0em;
		display: inline;
	}
	
	#partner-options input {
		vertical-align: middle;
	}
	<anc:personMenuStyle/>
</c:set>

<anc:pageLayout type="person">
	<gen:debug><!-- jsp/anc/person.jsp --></gen:debug>
	
	<div class="leftside">
		<div id="person-svg">
			<c:forEach items="${_svgList}" var="_support" varStatus="_stat">
				<anc:hierarchySvg svgdata="${_support}" index="${_stat.index}" />
			</c:forEach>
				
			<c:if test="${_person.multiPartnered}">
				<div id="partner-options">
					Show relationship with: <br />
					<c:forEach items="${_person.relationships}" var="_rel" varStatus="_stat">
						<span><input type="radio" name="svgindex" value="${_stat.index}" 
							<c:if test="${_stat.index eq 0}">checked</c:if>/> ${_rel.partner.name} </span>
					</c:forEach>
				</div>
			</c:if>
			
			<c:if test="${not _staticDelivery}">
				<span id="get-full-diagram" title="See full ancester tree"><i class="fas fa-sitemap fa-1g"></i></span>
			</c:if>
		</div>
	</div>
		
	<div class="menu">
		<anc:personMenu />
	</div>
	
	<div class="main">
		<anc:standardPerson />	
	</div>
	
	<div id="diagram-svg" class="hide"></div>
	
	<script>
		var _itemId = "${_item.id}";
		var _language = "${_item.language}";
		var _fullDiagramDialog = null;
		
		var switchHierarchy = function(target) {
			$(".hierarchy-diagram").each(function(idx){
				if (idx != target) {
					$(this).hide();
				}
				else {
					$(this).show();
				}
			});
		};
		
		$(function(){
			switchHierarchy(0);
			
			$("#partner-options input").change(function(){
				var index = $("#partner-options input:checked").val();
				switchHierarchy(index);
			});
			
			$("#get-full-diagram").click(function() {
				$.ajax("/" + _language + "/?view=diagram/" + _itemId, {
					type: "GET",
					cache: false,
					dataType: "html",
					mimeType: "text/html",
					
					success: function(html, status, z) {
						var div = $("#diagram-svg");
						div.empty().append(html);
						_fullDiagramDialog = div.dialog({
							autoOpen: true,
							width: $(window).width() * 0.9,
						  height: $(window).height() * 0.9,
						  modal: true,
						  title: "Full ancestry tree",
						  close: function() {
							  _fullDiagramDialog.dialog("close");
							  console.log("Dialog closed");
						  }
						});
					}
				});
			});
			
		});
	</script>
	
	<!-- 
	<span class="icon">
		<i class="fas fa-home icon-large" style="color: teal;"></i> 
		<i class="fas fa-male icon-large" style="color: #3399ff;"></i> 
		<i class="fas fa-female icon-large" style="color: #ff9999;"></i>
		<i class="fas fa-file-pdf icon-large" style="color: #ff3333;"></i> 
		<i class="fas fa-file-alt icon-large"></i>
		<i class="fas fa-image icon-large" style="color: #009900;"></i>
	</span>
	 -->

</anc:pageLayout>