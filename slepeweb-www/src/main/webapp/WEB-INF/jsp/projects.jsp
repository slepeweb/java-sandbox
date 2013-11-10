<%@page import="java.util.Enumeration"%>
<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
	$(function() {
		$("#tabs").tabs(
		//{active: 0}
		);
	});
</script>

<article class="first">

	<h2>Projects</h2>
	<p>This page describes a small selection of projects in which 
		<c:if test="${userHasAgentRole}">George Buttigieg,</c:if> the director of SWS 
		played a lead role.</p>

	<div id="tabs">
		<ul>
			<c:if test="${userHasAgentRole}">
				<li><a href="#tabs-1">Personal</a></li>
				<li><a href="#tabs-9">Skills</a></li>
			</c:if>
			<li><a href="#tabs-2">Jaguar Landrover</a></li>
			<li><a href="#tabs-3">Welsh Assembly</a></li>
			<li><a href="#tabs-4">Simply Health</a></li>
		</ul>

		<c:if test="${userHasAgentRole}">
			<div id="tabs-1" class="compact">
				<div class="inline width-50pc">
					<h3>Personal</h3>
					<table class="two-col-table">
						<tr>
							<td class="heading">Name</td>
							<td>George Buttigieg</td>
						</tr>
						<tr>
							<td class="heading">Job title</td>
							<td>Consultant, and SWS Director</td>
						</tr>
						<tr>
							<td class="heading">Email</td>
							<td>george@slepeweb.com</td>
						</tr>
						<tr>
							<td class="heading">Mobile</td>
							<td>0755 777 0817</td>
						</tr>
						<tr>
							<td class="heading">Degree</td>
							<td>Mechanical Engineering (1st), City University, London</td>
						</tr>
						<tr>
							<td class="heading">Interests</td>
							<td>Tennis, golf, walking, asian food</td>
						</tr>
						<tr>
							<td class="heading">Location</td>
							<td>St. Ives, Cambridgeshire</td>
						</tr>
						<tr>
							<td class="heading">Full CV</td>
							<td><a href="/resources/doc/cv.pdf">Latest CV</a></td>
						</tr>
					</table>
				</div>
				<img class="me-photo" src="/resources/images/gb.jpg" />
			</div>
		</c:if>

		<div id="tabs-2" class="compact">
			<h3>Jaguar Landrover</h3>
			<p>About 5 years ago, Jaguar decided to revamp there company
				website. The marketing team made a bold decisidocon to serve up every
				page on the site using flash movies, which at the time offered an
				exciting user experience.</p>

			<p>The content was stored in the Alterian CMS, and a new delivery
				framework had to be designed to deliver the content as both XML to
				the Flash components, and HTML for browsers that didn't support
				Flash. There were 2 types of XML document being served up to the
				browser by home-baked RESTful web services; one type defined the
				page layout, and the other provided the content for each component
				on the page. I was responsible for the design and implementation of
				this framework, which relied heavily on JAXB technology to 
				marchall/un-marshall the XML.</p>

			<p>Shortly after the Jaguar site launched, work was underway on
				the Landrover site. In this case, the argument for an all-Flash
				approach from the Landrover side of the business was not so strong, and
				so a traditional HTML approach was taken, adopting jQuery UI
				components to provide special effects and an equally compelling user
				experience. The main challenge for me on
				this project was the design of the Alterian content model, which had
				to provide much higher levels of flexibility regarding page layouts,
				without making life too complicated for the content authors.
				Integrating the JSP templates with the weighty jQuery components was
				also a good learning opportunity.</p>

			<p>In 2012, the Jaguar site was overhauled completely in terms of
				content and presentation, although still using Alterian CMS for
				content management. The main purpose of the revamp was to produce a
				responsive web design, so that desktop, tablet and mobile devices
				could utilise the same HTML, but rendered differently on each
				device. At the backend, the opportunity was taken to use the Spring
				MVC framework, and to design a new object model to simplify code
				production and maintenance. Another interesting aspect of the
				back-end work was the use of Ehcache annotations to simplify the
				cacheing of key components/objects. Maven was used to handle dependencies
				and to build war files for the Dev, QA, and Production servers.</p>
				
			<p>One of my tasks was to rationalize the 'dealer locator' functionality, 
				mainly to fit into the Spring MVC framework. This called a RESTful service
				provided by Bing maps, where the dealer data was integrated with the map
				data. JAXB was used once again in our webapp to unmarshall the Bing response.</p>
			
		</div>

		<div id="tabs-3" class="compact">
			<h3>Welsh Assembly Government</h3>
			<p>In this project, I guided our implementation partners
				(Siemens) in the solution design, and provided them with a
				foundation layer of code to support them. The interesting point
				about this site was its bi-lingual functionality - on any page, you
				could change language from English to Welsh, and vice versa. This
				required care in designing both the CMS, and the content delivery
				software.</p>

			<p>A follow-on project for this client involved the use of JMS to
				distribute visitor-submitted form data to a server on the government
				GSi network, from where it could be further distributed using email
				without compromising government security rules.</p>
		</div>

		<div id="tabs-4" class="compact">
			<h3>Simply Health</h3>
			<p>This project was quite unusual - the client required an
				integration between Mediasurface (the predecessor to Alterian CMS)
				and ATG Dynamo. Content from the Mediasurface repository had to be
				synchronised with the ATG database. SimplyHealth were responsible
				for building the ATG web application, and I was responsible for
				building the synchronisation software. This comprised a 'listener'
				application that responded to content change events, and propagated
				these to the ATG database tables. At the same time, JMS messages
				containing the same basic information were broadcast to a JMS
				server. The data access layer used Hibernate, and everything was
				wired together using Spring.</p>
		</div>

		<c:if test="${userHasAgentRole}">
			<div id="tabs-9" class="compact">
				<h3>Skills</h3>
				<p>My skills have been organised according to how frequently they are applied in my work.</p>
				<table class="two-col-table">
					<tr>
						<td class="heading">Every day</td>
						<td>Java, JSP, Servlet, JSTL, Spring MVC, Tiles, Custom tags, Windows, Linux,
							SVN, Eclipse, Alterian CMS</td>
					</tr>
					<tr>
						<td class="heading">Occasional</td>
						<td>Javascript, jQuery, Dojo, Ajax, CSS, HTML, Maven, Git, Apache, Tomcat, Websphere, Weblogic, 
							XSD, XML, XSLT, JAXB, RESTful and SOAP web services, Quartz, Bash scripting, CVS, 
							Amazon Cloud services</td>
					</tr>
					<tr>
						<td class="heading">Project-specific</td>
						<td>JDBC, JMS, Hibernate, Ibatis, Lucene, Struts</td>
					</tr>
					<tr>
						<td class="heading">Infrequent</td>
						<td>Ant, Ruby, Perl</td>
					</tr>
					<tr>
						<td class="heading">Historical</td>
						<td>C</td>
					</tr>
					<tr>
						<td class="heading">Educational</td>
						<td>EJB, Ruby on Rails, Groovy, MongoDB, JSF</td>
					</tr>
				</table>
			</div>
		</c:if>
	</div>
</article>
