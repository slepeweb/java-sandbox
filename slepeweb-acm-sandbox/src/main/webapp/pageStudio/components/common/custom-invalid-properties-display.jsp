<%@page import="java.util.ArrayList" %>
<%
	ArrayList invalidProperties = (ArrayList) request.getAttribute("invalidProperties");
%>
<script language="javascript" type="text/javascript">
displayInvalidProperties = function (){
	alert("Some of the property values supplied were invalid\nPlease re-configure the component.");
};

displayInvalidProperties();
</script>