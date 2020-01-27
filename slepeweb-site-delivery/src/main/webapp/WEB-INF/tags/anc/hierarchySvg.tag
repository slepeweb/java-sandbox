<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<svg 
	xmlns="http://www.w3.org/2000/svg"
 	xmlns:xlink="http://www.w3.org/1999/xlink" 
 	width="100%" height="${_support.frameHeight}">
 			
	 <defs>
	    <!-- arrowhead marker definition -->
	    <marker id="arrow" viewBox="0 0 10 10" refX="5" refY="5" 
	    		markerWidth="10" markerHeight="10" orient="auto-start-reverse">
	      <path d="M 0 0 L 10 5 L 0 10 z" />
	    </marker>
	
	    <!-- simple dot marker definition -->
	    <marker id="dot" viewBox="0 0 10 10" refX="5" refY="5"
					markerWidth="5" markerHeight="5">
	      <circle cx="5" cy="5" r="5" fill="red" />
	    </marker>
	  </defs>
	  
	  <style>
	  	.subject {
	  		font-weight: bold;
	  		fill: #16688a;
	  	}
	  </style>
	   
<!-- 	<g> -->
		<c:if test="${not empty _person.father}">
			${_support.father.linkTag}
		</c:if>

		<c:if test="${not empty _person.mother}">
			${_support.mother.linkTag}
		</c:if>

		${_support.subject.subjectText}

		<c:if test="${not empty _support.partner}">
			${_support.partner.linkTag}
		</c:if>

		<c:if test="${not empty _support.children}">
			<c:forEach items="${_support.children}" var="_child">
				${_child.singleLineTextLinkTag}
			</c:forEach>
		</c:if>

		<c:forEach items="${_support.lineA}" var="_segment">
			<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
				stroke="black" <c:if test="${_segment.end.marker}">marker-end="url(#arrow)"</c:if> />
		</c:forEach>

		<c:forEach items="${_support.lineB}" var="_segment">
			<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
				stroke="black" <c:if test="${_segment.end.marker}">marker-end="url(#arrow)"</c:if> />
		</c:forEach>

		<c:forEach items="${_support.lineC}" var="_segment">
			<line x1="${_segment.start.x}" y1="${_segment.start.y}" x2="${_segment.end.x}" y2="${_segment.end.y}" 
				stroke="black" />
		</c:forEach>
		
		<%--
		<image x="300" y="${_support.grandparentsIcon.y}" width="70" height="70" href="/content/images/icons/grandparents" />
		<image x="300" y="${_support.parentsIcon.y}" width="70" height="70" href="/content/images/icons/parents" />
		<image x="300" y="${_support.childrenIcon.y}" width="70" height="70" href="/content/images/icons/children" />
		 --%>
<!-- 	</g> -->
</svg>
