<%@ taglib uri="mediasurfaceTags" prefix="ms"%>
<%@ taglib uri="pageStudioTags" prefix="ps"%>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ps:component>
<%
	String path = propertyPublisher.getComponentHandler().getComponent().getPSCLocation();
	class MyAuthenticator extends java.net.Authenticator {
		private String username;
		private String password;

		public MyAuthenticator(String username,
				String password) {
			super();
			this.username = username;
			this.password = password;
		}

		protected java.net.PasswordAuthentication getPasswordAuthentication() {
			return new java.net.PasswordAuthentication(
					username, password.toCharArray());
		}
	}
	String username = propertyPublisher.getProperty("username");
	String count = propertyPublisher.getProperty("count");
	if(count.equals("")) count = "20";
	MyAuthenticator auth = new MyAuthenticator(username,
			propertyPublisher.getProperty("password"));
	java.net.Authenticator.setDefault(auth);

	String id = "ps_twitter"+propertyPublisher.getInstId();
%>


	<link rel="stylesheet" type="text/css" href="<%=path%>/twitter.css"/>

	<div class="ps_twitter" id="<%=id%>">
		<div class="ps_twitter_logo"></div>
		<div class="ps_twitter_body">
			<div class="ps_twitter_header">
				<h2 class="ps_twitter_status">What are you doing?</h2>
				<div class="ps_twitter_char_count">140</div>
			</div>
			<form class="ps_twitter_form">
				<textarea></textarea>
				<input type="submit" class="ps_twitter_update_btn" value="update" />
			</form>
			<div class="ps_twitter_notifications"></div>
			<div class="ps_tweets"></div>
			<div class="ps_twitter_nav">
				<a class="ps_twitter_prev" href="javascript:void(0);">Previous</a><a class="ps_twitter_next" href="javascript:void(0);">Next</a>
			</div>
		</div>
	</div>
	<script language="javascript" type="text/javascript">
	/* <![CDATA[ */
		function TwitterComponentLoader() {
			var loaded;

			function init() {
				if (document.loaded) {
					loadPrototype();
				}
				else {
					if(window.addEventListener) window.addEventListener("load", loadPrototype, false);
					else if(window.attachEvent) window.attachEvent("onload", loadPrototype);
				}
			}
			
			function loadPrototype() {
				loaded = {
					"prototype": window["Prototype"],
					"pagestudio": window["PageStudio"],
					"twitter": window["Twitter"]
				};
				if(!loaded.prototype) {
					var script = document.createElement("script");
					script.src = "/pageStudio/scripts/lib/prototype.js";
					var head = document.getElementsByTagName("head")[0];
					head.appendChild(script);
				}
				checkPrototypeLoad();				
			}

			function checkPrototypeLoad() {
				setTimeout(function() {
					loaded.prototype = window["Prototype"];
					if(!loaded.prototype) checkPrototypeLoad();
					else loadTwitter();
				}, 2000);
			}

			function loadTwitter() {
				//if we are in PS, wait for it to finish loading, otherwise, just continue
				if(loaded.pagestudio) {
					if(!PageStudio.loaded) {
						Event.observe(document, PageStudio.EVENT.LOADED, loadTwitter);
					}
					else { 
						Event.stopObserving(document, PageStudio.EVENT.LOADED, loadTwitter);
					} 
				}
				if (!loaded.pagestudio || PageStudio.loaded) {
					if (!window["Twitter"]) {
						var newScript = new Element("script");
						newScript.src = "<%=path%>/twitter.js";
						$$("head")[0].insert(newScript);
						
						//if the script hasn't loaded yet we keep trying.
						new PeriodicalExecuter(function(pe) {
							if (window["Twitter"]) {
								pe.stop();
								new Twitter("<%=id%>", "<%=username%>", "<%=path%>/", <%=count%>);
							}
						}, 0.5);
					}
					else {
						new Twitter("<%=id%>", "<%=username%>", "<%=path%>/", <%=count%>);
					}
				} 
			}

			init();
		}
		TwitterComponentLoader();
	/* ]]>*/
	</script>
</ps:component>