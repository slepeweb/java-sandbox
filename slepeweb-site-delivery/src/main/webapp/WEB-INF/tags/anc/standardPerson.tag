<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/standardPerson.tag --></gen:debug>

<h2>${_person.fullName}</h2>

<ul>
	<c:if test="${not empty _person.birthSummary}">
		<li>b. ${_person.birthSummary}</li>
	</c:if>
	
	<c:forEach items="${_person.relationships}" var="rel">
		<c:if test="${not empty rel.summary}">
			<li>${rel.summary}</li>
		</c:if>
	</c:forEach>
	
	<c:if test="${not empty _person.deathSummary}">
		<li>d. ${_person.deathSummary}</li>
	</c:if>
</ul>

<anc:personMenu />

<c:if test="${not empty _person.photo}">
	<div>
		<img class="passport-photo" src="${_person.photo.url}" align="left" />
	</div>
</c:if>

<p>${_person.item.fields.overview}</p>
		