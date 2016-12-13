<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<%-- This page requires extra js --%>
<c:set var="_extraJs" scope="request">/resources/sws/js/wsdemo.js</c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/template/wsdemo.jsp --></gen:debug>
	
		<!-- Main content -->	
		<div class="col-3-4 primary-col">	
			<sw:standardBody />	
			
			<!-- This jsp is identical to article-leftnav.jsp, except for the
					assignment of this js variable: -->
			<script>var _isPasswordClient = ${_isPasswordClient};</script>
			
			<%-- The form could be loaded by this jsp, but for the moment, will continue
					to use the componentisation functionality, so that the component can
					manage the form content. --%>			
			<site:insertComponents site="${_item.site.shortname}" 
				list="${_page.components}" 
				view="main" /> 
		</div>					
	
		<!-- Left Sidebar -->
		<div class="col-1-4 primary-col grey-gradient left2right pull-right-sm">
			<sw:navigation-left />
		</div>
		
</sw:standardLayout>