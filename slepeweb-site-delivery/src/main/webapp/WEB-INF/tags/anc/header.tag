<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<header>
	<div id="site-identifier"><h1><a href="/${_item.language}">My Ancestry</a></h1></div>	
	<anc:languageSwitcher />
	<anc:searchBar />	
	<anc:personBreadcrumbs />
</header>