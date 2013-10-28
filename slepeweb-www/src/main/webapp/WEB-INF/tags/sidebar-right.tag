<%@ tag%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<section>
	<h3>Technology news</h3>
	<p>Latest technology news feed from the BBC:</p>
	<ul class="link-list">
		<c:forEach items="${_rss}" var="link" end="3">
			<li class="compact"><a class="iframe cboxElement" href="${link.href}">${link.title}</a></li>
		</c:forEach>
	</ul>
</section>

<%--section class="last">
	<h3>Magna Phasellus</h3>
	<ul class="link-list">
		<li><a href="#">Sed dolore viverra</a></li>
		<li><a href="#">Ligula non varius</a></li>
		<li><a href="#">Nec sociis natoque</a></li>
		<li><a href="#">Penatibus et magnis</a></li>
		<li><a href="#">Dis parturient montes</a></li>
		<li><a href="#">Nascetur ridiculus</a></li>
	</ul>
</section> --%>
