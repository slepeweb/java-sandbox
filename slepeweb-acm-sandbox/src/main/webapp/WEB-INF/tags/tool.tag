<%@ tag language="java" %>
<%@ tag import="com.mediasurface.pagestudio.PageStudioLoader" %>
<%@ attribute name="id" required="true" rtexprvalue="true"%>
<%@ attribute name="locale" required="false" rtexprvalue="true"%>

<!--  BEGIN: PageStudio Tool -->
<%
PageStudioLoader psl = PageStudioLoader.getExistingInstance(request);
if(psl.isFirstLoad() && psl.hasPermission()) {
	String root = request.getContextPath();
	if(root.equals("/")) root = "";
	%>	
	<jsp:include page="/pageStudio/html/includes.jsp">
		<jsp:param name="psRoot" value="<%=root%>" />
	</jsp:include>
	<%
}
//Default locale is English.
if (locale == null) locale = "PSLanguage_en";
psl.createPageStudioTool(request, id, locale);
%>
<!--  END: PageStudio Tool -->