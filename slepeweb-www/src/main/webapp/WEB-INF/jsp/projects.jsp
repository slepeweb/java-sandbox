<%@page import="java.util.Enumeration"%>
<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<article class="first">

	<h2>Profile</h2>
	
<c:choose><c:when test="${userHasAgentRole}">
	<script>
		$(function() {
			$("#tabs").tabs(
			//{active: 0}
			);
		});
	</script>

	<p>This page describes a small selection of projects in which George Buttigieg (a director of 
		SWS) played a lead role.</p>

	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Personal</a></li>
			<li><a href="#tabs-9">Skills</a></li>
			<li><a href="#tabs-2">Jaguar Landrover</a></li>
			<li><a href="#tabs-3">Welsh Assembly</a></li>
			<li><a href="#tabs-4">Simply Health</a></li>
			<li><a href="#tabs-5">Finance Calculator</a></li>
		</ul>

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
			<img class="me-photo" src="/resources/images/gb-300x261.jpg" />
		</div>

		<div id="tabs-2" class="compact">
			<h3>Jaguar Landrover</h3>
			<p>About 5 years ago, Jaguar decided to revamp their main
				website. The marketing team took a bold decision to serve up every
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
				marshall/un-marshall the XML.</p>

			<p>Shortly after the Jaguar site launched, work was underway on
				the Landrover site. In this case, the argument for an all-Flash
				approach from the Landrover side of the business was not so strong, and
				so a traditional HTML approach was taken, adopting jQuery UI
				components to provide special UI effects and an equally compelling user
				experience. The main challenge for me on
				this project was the design of the Alterian content model, which had
				to provide much higher levels of flexibility regarding page layouts,
				without making life too complicated for the content authors.
				Integrating the JSP templates with the weighty jQuery components was
				also a good learning opportunity.</p>

			<p>In 2012, the Jaguar site was overhauled completely in terms of
				content and presentation, although still using Alterian CMS for
				content management. The main purpose of the revamp was to produce a
				responsive front-end web design, so that desktop, tablet and mobile devices
				could utilise the same HTML, but rendered it differently on each
				device. At the backend, the opportunity was taken to use the Spring
				MVC framework, and to design a new object model to simplify code
				production and maintenance. Another interesting aspect of the
				back-end work was the use of Ehcache annotations to simplify the
				cacheing of key components/objects. Maven was used to handle dependencies
				and to build war files for the Dev, QA, and Production servers.</p>
				
			<p>One of my tasks on this project was to rationalize the 'dealer locator' functionality, 
				mainly to fit into the Spring MVC framework. This called a RESTful service
				provided by Bing maps, who had integrated the dealer data with the map
				data. JAXB was used once again in our webapp to unmarshall the Bing response.</p>
			
		</div>

		<div id="tabs-3" class="compact">
			<h3>Welsh Assembly Government</h3>
			<p>In this project, I guided our implementation partners
				(Siemens) in the solution design, and provided them with a
				foundation layer of code to support them. The interesting point
				about this site was its bi-lingual functionality - on any page, you
				could change language from English to Welsh, and vice versa. This
				required care in designing the CMS, and the content delivery
				software needed to tackle the bi-lingual nature of the site at a
				lower level, so as not to burden the people building the JSP 
				templates.</p>

			<p>A follow-on project for this client involved the use of JMS to
				distribute visitor-submitted form data to a server on the government
				GSi network, from where it could be distributed by email
				without contravening government security rules.</p>
		</div>

		<div id="tabs-4" class="compact">
			<h3>Simply Health</h3>
			<p>One this relatively small project, the client required an
				integration between Mediasurface (the predecessor to Alterian CMS)
				and ATG Dynamo. Content from the Mediasurface repository had to be
				synchronised with the ATG database, and delivered to site visitors
				using the ATG webapp. SimplyHealth were responsible
				for building the ATG delivery code, and I was responsible for
				building the synchronisation software. This comprised a 'listener'
				application that responded to content change events, and propagated
				these to the ATG database tables. At the same time, JMS messages
				containing the same basic information were broadcast to a JMS
				server. The data access layer used Hibernate, and everything was
				wired together using Spring.</p>
		</div>

		<div id="tabs-5" class="compact">
			<h3>Jaguar Finance Calculator</h3>
			<p>The finance calculator was a small add-on to the main
				Jaguar webapp. As its name suggests, it provided site visitors
				with alternative finance quotes for their intended purchase. The
				visitor was able to either pick a car off the shelf, or
				personalise one using 3rd party car configuration software.</p>

			<p>The webapp pulled the car configuration options from the 3rd
				party system using RESTful services, and presented these to the
				end user. It then calculated the total cost of the selected model,
				bodystyle and engine, plus any optional extras. It then used all
				this data to pull available finance options from another 3rd part
				system, again using REST-style services. The visitor could add up
				to 3 quotes to his basket, and could also download these as PDF
				documents for presentation to dealers.
		</div>

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
					<td class="heading">By project</td>
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
	</div>
</c:when><c:otherwise>
	<div class="short-profile">
		<p>George Buttigieg is a director of SWS, and has played a lead role in the back-end design/build
			of web sites for some major brands. These were dynamic CMS-driven websites (Alterian CMS), with
			various integrations to 3rd party systems.</p>
		
		<ul class="project-list">
			<li>Jaguar Landrover</li>
			<li>Foreign and Commonwealth Office</li>
			<li>Department for Transport</li>
			<li>Welsh Assembly Government</li>
			<li>Home Office</li>
			<li>Oxford University Press</li>
			<li>Croydon Council</li>
			<li>Cancer Research UK</li>
			<li>Plan International</li>
		</ul>
		
		<p>
			<br />
			Please <a href="/login?nextPath=%2Fprofile">login</a> if you would like access to more detailed 
			content in this section.</p>
	</div>
	<div class="short-profile">
		<img src="/resources/images/profile.jpg" alt="Anon" />
	</div>
</c:otherwise></c:choose>
</article>
