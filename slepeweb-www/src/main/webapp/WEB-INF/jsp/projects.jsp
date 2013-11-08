<script>
	$(function() {
		$("#tabs").tabs(
		//{active: 0}
		);
	});
</script>

<article class="first">

	<h2>Projects</h2>
	<p>Currently employed by Slepe Web Solutions, this page is about me
		and the projects that I have undertaken over the past decade.</p>

	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Personal</a></li>
			<li><a href="#tabs-2">Jaguar</a></li>
			<li><a href="#tabs-3">Welsh Assembly</a></li>
			<li><a href="#tabs-4">Simply Health</a></li>
			<li><a href="#tabs-9">Skills</a></li>
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
						<td class="heading">Email</td>
						<td>george@slepeweb.com</td>
					</tr>
					<tr>
						<td class="heading">Mobile</td>
						<td>0755 777 0817</td>
					</tr>
					<tr>
						<td class="heading">Degree</td>
						<td>Mechanical Engineering, 1st class, City University, London</td>
					</tr>
					<tr>
						<td class="heading">Interests</td>
						<td>Tennis, golf, walking, asian food</td>
					</tr>
					<tr>
						<td class="heading">Location</td>
						<td>St. Ives, Cambs.</td>
					</tr>
					<tr>
						<td class="heading">Download</td>
						<td><a href="/resources/doc/cv.doc">Latest CV</a></td>
					</tr>
				</table>
			</div>
			<img class="me-photo" src="/resources/images/gb.jpg" />
		</div>

		<div id="tabs-2" class="compact">
			<h3>Jaguar Landrover</h3>
			<p>The Jaguar site broke new ground about 5 years ago, as every
				page in the site was delivered using Flash movies at the front end.
				This was a requirement of the marketing department, but was opposed
				by IT, who eventually had to concede. The content was stored in the
				Alterian CMS, and a new delivery framework had to be designed to
				deliver the content as both XML to the Flash components, and HTML
				for browsers that didn't support Flash. There were 2 types of XML
				document being served up to the browser by home-baked Restful web
				services; one defined the page layout, and the other provided the
				page content. I was responsible for the design and implementation of
				this framework, which relied heavily on JAXB technology.</p>
			<p>Shortly after the Jaguar site launched, work was underway on
				the Landrover site. In this case, the argument for an all-Flash
				approach from the other side of the business was not so strong, and
				so a traditional HTML approach was taken, adopting jQuery UI
				components to provide lightbox effects, tabbed content, video
				players and image galleries. The results were excellent,
				demonstrating that jQuery UI components were capable of producing a
				user experience that rivalled Flash. The main challenge for me on
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
				back-end work was the use of EhCache annotations to simplify the
				cacheing of key components/objects.</p>
			<p>*** Check whether there are any other useful technology-dropping ***</p>
		</div>

		<div id="tabs-3" class="compact">
			<h3>Welsh Assembly Government</h3>
		</div>

		<div id="tabs-4" class="compact">
			<h3>Simply Health</h3>
		</div>
		
		<div id="tabs-9" class="compact">
			<h3>Skills</h3>
			<p>My skills have been organised according to how frequently they are applied in my work.</p>
			<table class="two-col-table">
				<tr>
					<td class="heading">Every day</td>
					<td>Java, JSP, Servlet, JSTL, Spring MVC, Custom tags, Windows, Linux,
						SVN, Eclipse, Alterian CMS</td>
				</tr>
				<tr>
					<td class="heading">Occasional</td>
					<td>Javascript, jQuery, Dojo, Ajax, CSS, HTML, Maven, Git, Apache, Tomcat, Websphere, Weblogic, 
						XSD, XML, XSLT, JAXB, RESTful and SOAP web services, Bash scripting, CVS, Amazon Cloud services</td>
				</tr>
				<tr>
					<td class="heading">Infrequent</td>
					<td>Ant, Perl</td>
				</tr>
				<tr>
					<td class="heading">Project-specific</td>
					<td>JDBC, JMS, Hibernate, Ibatis, Lucene, Struts</td>
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
</article>
