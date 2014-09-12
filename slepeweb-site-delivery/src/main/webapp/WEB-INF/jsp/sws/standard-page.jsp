<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>
	
<article class="first">
	<h2>${_page.title}</h2>
	<div>${_page.body}</div>		
	<gen:insertComponentSet site="${_site.shortname}" list="${_page.components}" />
</article>
