<%@ tag %><%@ 
	attribute name="svgdata" required="true" rtexprvalue="true" type="com.slepeweb.site.anc.bean.svg.SvgSupport" %><%@ 
	attribute name="index" required="true" rtexprvalue="true" type="java.lang.Integer" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<div class="hierarchy-diagram">
	<svg 
		xmlns="http://www.w3.org/2000/svg"
	 	xmlns:xlink="http://www.w3.org/1999/xlink" 
	 	width="100%" height="${svgdata.frameHeight}">
	 			
		 <defs>
		    <!-- arrowhead marker definition -->
		    <marker id="arrow-${index}" viewBox="0 0 10 10" refX="5" refY="5" 
		    		markerWidth="10" markerHeight="10" orient="auto-start-reverse">
		      <path d="M 0 0 L 10 5 L 0 10 z" />
		    </marker>
		  </defs>
		  
		  <style>
		  	.subject {
		  		font-weight: bold;
		  		fill: #16688a;
		  	}
		  	
		  	text.data {
		  		font-size: 0.7em;
		  	}
		  </style>
		   
			<c:if test="${not empty _person.father}">
				${svgdata.father.linkTag}
			</c:if>
	
			<c:if test="${not empty _person.mother}">
				${svgdata.mother.linkTag}
			</c:if>
	
			${svgdata.subject.subjectText}
	
			<c:if test="${not empty svgdata.partner}">
				${svgdata.partner.linkTag}
			</c:if>
	
			<c:if test="${not empty svgdata.children}">
				<c:forEach items="${svgdata.children}" var="_child">
					${_child.singleLineTextLinkTag}
				</c:forEach>
			</c:if>
	
			<c:forEach items="${svgdata.lineA.segments}" var="_segment">
				<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
					stroke="black" <c:if test="${_segment.end.marker}">marker-end="url(#arrow-${index})"</c:if> />
			</c:forEach>
			
			<c:if test="${not empty svgdata.lineA.tooltipPos}">
				<image class="info-icon" x="${svgdata.lineA.tooltipPos.x}" y="${svgdata.lineA.tooltipPos.y - 20}" 
					width="16" height="16" href="/resources/anc/info2.png" title="${svgdata.lineA.tooltip}" />
			</c:if>
			
			<c:forEach items="${svgdata.lineB.segments}" var="_segment">
				<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
					stroke="black" <c:if test="${_segment.end.marker}">marker-end="url(#arrow-${index})"</c:if> />
			</c:forEach>
											
			<c:if test="${not empty svgdata.lineB.tooltipPos}">
				<image class="info-icon" x="${svgdata.lineB.tooltipPos.x}" y="${svgdata.lineB.tooltipPos.y - 20}" 
					width="16" height="16" href="/resources/anc/info2.png" title="${svgdata.lineB.tooltip}" />
			</c:if>
			
			<c:forEach items="${svgdata.lineC}" var="_segment">
				<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
					stroke="black" />
			</c:forEach>
			
			<%--
			<image x="300" y="${svgdata.grandparentsIcon.y}" width="70" height="70" href="/content/images/icons/grandparents" />
			<image x="300" y="${svgdata.parentsIcon.y}" width="70" height="70" href="/content/images/icons/parents" />
			<image x="300" y="${svgdata.childrenIcon.y}" width="70" height="70" href="/content/images/icons/children" />
			 --%>
	</svg>
</div>

<script>
	$(function() {
		$("image.info-icon").tooltip();
	});
</script>