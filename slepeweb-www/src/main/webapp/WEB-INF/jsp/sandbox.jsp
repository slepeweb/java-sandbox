<h2>Sandbox</h2>
<p>This is a sandbox area for demonstration and testing purposes.</p>

<div id="tabs">
	<ul>
    <li><a href="#tabs-0">Platform</a></li>
    <li><a href="#tabs-1">RESTful</a></li>
    <li><a href="#tabs-2">JAX-WS</a></li>
  </ul>

	<div id="tabs-0" class="compact">
		<article class="first">
			<h3>The computing environment</h3>
			<p>This website is being served by an Amazon EC2 virtual server,
				running Ubuntu 12.04 LTS. Installed on this server are: Java 1.6,
				Tomcat 7, Apache 2.2 and MongoDB. The database is being used
				initially to store login account details, with the Jasypt library
				being used to encrypt passwords.</p>

			<p>The website design was originated by n33.co, and is a
				responsive design based on skelJS. jQuery coding has been added to
				provide user-friendly layouts and to handle Ajax calls.</p>

			<p>The webapp is built using Spring MVC 3.1.2, integrated with
				Tiles to manage JSP layouts. Other software components include Rome
				for RSS feeds, the JAXWS reference implementation from Sun, and
				Jackson for marshalling Java POJOs to JSON.</p>

		</article>
	</div>
	
	<div id="tabs-1" class="compact">
		<article class="first">
			<h3>Lottery numbers service</h3>
			<p>
				The lottery numbers below are generated by a RESTful web service that
				is hosted on this server using Spring MVC. An ajax request is made to retrieve the JSON
				representation of this resource. The web service URL is: 
				<a href="/ws/lotterynumbers/3">/ws/lotterynumbers/3</a>. If the 'Accept' header on the
				request is set to 'text/xml', then the service will return the data as XML.
			</p>
			<div id="lotterynumbers"></div>
			
			<br />
			<button type="button" class="button" id="lotteryrefresh">More numbers</button>
			<button type="button" class="button" id="lotteryclear">Clear</button>
		</article>
	</div>
	
	<div id="tabs-2" class="compact">
		<article class="first">
			<h3>Password generation service</h3>
			
			<p>
				Enter a seed string that identifies the company/website that the password is for,
				then click on the 'Get password' button.
			</p>
			
			<table class="two-col-table">
				<tr>
					<td class="heading"><label for="org">Seed</label></td>
					<td><input id="password-org" type="text" name="org" size="16" /></td>
				</tr>
				<tr>
					<td class="heading"><label for="password">Password</label></td>
					<td><input id="password-pwd" type="text" name="password" size="16" readonly style="background: #cccccc" /></td>
				</tr>
			</table>
			
			<br />
			<button type="button" class="button" id="password-update">Get password</button>

			<p style="margin-top: 20px">
				This triggers an ajax request that calls a RESTful service, that in turn calls a 
				SOAP service. Both services are hosted on this server.
				The SOAP service digests the seed, generates a corresponding password, and returns that
				to the RESTful service, which returns a JSON object to the Ajax caller.
			</p>
			
			<p>(Inspect the <a href=/jaxws/password?wsdl>wsdl</a>)</p>
			
		</article>
	</div>
</div>