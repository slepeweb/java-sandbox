<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ps:component>
<script language="javascript" type="text/javascript">
componentLoadHandler = function (){
};

componentLoadHandler();
</script>
<%
  String[] lists = propertyPublisher.getPropertyList("list1");
  String listValues = "";
  for(int i=0; i<lists.length; i++){
  	listValues +=" "+lists[i];	
  }
%>
<div>
<h4>Suppplied values:</h4>
<div style="align:left;"><span>String : </span></div><div><%= propertyPublisher.getProperty("string1") %></div><br/>
<div style="align:left;"><span>Numeric : </span></div><div><%= propertyPublisher.getProperty("numeric1") %></div><br/>
<div style="align:left;"><span>List : </span></div><div><%=  listValues %></div><br/>
<div style="align:left;"><span>SingleValueList : </span></div><div><%= propertyPublisher.getProperty("singlevaluelist1") %></div><br/>
</div>
</ps:component>
