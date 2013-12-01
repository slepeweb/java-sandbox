<article class="first">
	<h2>User login account management</h2>

	<p>Spring and Hibernate are working together here to support the
		various CRUD activities: list/add/update/delete users. Users are
		assigned to one or more roles, each role giving access to different
		parts of the webapp. Spring forms are bound to backing objects which
		collect the form data. Annotations on the persisitent entity classes
		define various validation constraints. DAO services use the Hibernate
		APIs to interact with the MySql backend.</p>

	<p>
		Site visitors will be able to to interact with these pages, and see
		form validation at work, but will not be able to commit changes to
		the database unless they are logged in with the appropriate
		privileges. Please contact the <a href="mailto:admin@slepeweb.com">administrator</a>
		if this is what you'd like to see.</p>

</article>
