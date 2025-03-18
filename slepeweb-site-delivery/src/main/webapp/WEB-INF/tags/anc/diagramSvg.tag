<%@ tag %><%@ 
	attribute name="svgdata" required="true" rtexprvalue="true" type="com.slepeweb.site.anc.bean.svg.AncestryDiagram" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<svg 
	xmlns="http://www.w3.org/2000/svg"
 	xmlns:xlink="http://www.w3.org/1999/xlink" 
 	height="${svgdata.height}"
 	width="${svgdata.width}"
 	>
 			
	 <defs>
	    <!-- arrowhead marker definition -->
	    <marker id="arrow-${index}" viewBox="0 0 10 10" refX="5" refY="5" 
	    		markerWidth="10" markerHeight="10" orient="auto-start-reverse">
	      <path d="M 0 0 L 10 5 L 0 10 z" />
	    </marker>
	  </defs>
	  
	  <style>
	  	#diagram-svg text {
	  		font-size: 12px;
	  	}
	  </style>
	  
	  <c:if test="${not empty svgdata}">
		  <c:forEach items="${svgdata.rows}" var="_row" varStatus="_stat1">
		  	<c:forEach items="${_row.list}" var="_comp" varStatus="_stat2">
		  
					<c:if test="${not _comp.parentA.blank}">
						${_comp.parentA.linkTagWithYears}
					</c:if>
			
					<c:if test="${not _comp.parentB.blank}">
						${_comp.parentB.linkTagWithYears}
					</c:if>
						
					<c:forEach items="${_comp.segments}" var="_segment">
						<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
							stroke="black" <c:if test="${_segment.end.marker}">marker-end="url(#arrow-${index})"</c:if> />
					</c:forEach>
					
				</c:forEach>
			</c:forEach>
			
			${svgdata.subject.linkTagWithYears}
		</c:if>
</svg>

