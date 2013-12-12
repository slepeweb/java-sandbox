<%@page import="com.mediasurface.pagestudio.PageStudioLoader"%>
<%
	PageStudioLoader psl = PageStudioLoader.getInstance(pageContext);
	String id = request.getParameter("id");
	psl.initalizeJSPPageStudio(id);
	if (psl.isInContributor()) {
		if(psl.isFirstLoad()) {
			String root = request.getContextPath();
			if(root.equals("/")) root = "";
			%>	
			<jsp:include page="/pageStudio/html/includes.jsp">
				<jsp:param name="psRoot" value="<%=root%>" />
			</jsp:include>
			<%
		}
		psl.createPageStudioTool(request, id, "PSLanguage_en");
	}
%>