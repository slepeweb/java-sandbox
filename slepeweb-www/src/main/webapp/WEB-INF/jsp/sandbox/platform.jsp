<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<article class="first">
	<h2>Platform</h2>

	<p>This website is being served by an Amazon EC2 virtual server,
		running Ubuntu 12.04 LTS. This sandbox area demonstrates a number of web
		technologies that form a part of the SWS toolkit.</p>

	<p>Installed on this server are: Java 1.7, Tomcat 7, Apache 2.2 and
		MySql 5.5. The database is being used to manage login accounts and
		site configuration data.</p>

	<p>The website design was originated by n33.co, and is a responsive
		design based on the skelJS framework. jQuery coding has been added to
		provide user-friendly layouts and to handle Ajax calls.</p>

	<p>The webapp is built using Spring MVC 3.1.2, with Tiles to
		support the view mechanism. Hibernate 3.6 is used to manage user
		login accounts, but this functionality is only available to
		certain users.</p>
	
	<p>Other software components include Rome
		for RSS feeds, the JAXWS reference implementation from Sun,
		Jackson for marshalling Java POJOs to JSON, and Jasypt for
		encrypting passwords. Ehcache is used to cache selected components
		for a fixed TTL.</p>
	
</article>
