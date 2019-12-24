<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/standardPerson.tag --></gen:debug>

<h2>${_person.fullName}</h2>

<ul>
	<c:if test="${not _person.blankBirthDetails}">
		<li>b. ${_person.birthDetails}</li>
	</c:if>
	<c:if test="${not _person.blankMarriageDetails}">
		<li>m. ${_person.marriageDetails}</li>
	</c:if>
	<c:if test="${not _person.blankDeathDetails}">
		<li>d. ${_person.deathDetails}</li>
	</c:if>
</ul>

<anc:personMenu />

<c:if test="${not empty _person.photo}">
	<div>
		<img class="passport-photo" src="${_person.photo.path}" align="left" />
	</div>
</c:if>

<p>${_person.item.fields.overview}</p>
		