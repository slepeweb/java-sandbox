<%@ taglib uri="mediasurfaceTags" prefix="ms" %>
<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ps:component>
<script language="javascript" type="text/javascript">

componentLoadHandler = function (){
//alert("Hey its me .. running in the user details [<%= propertyPublisher.getInstId() %>] callback");
};
componentLoadHandler();
</script>
<div>
<h3>User :&nbsp;<%= propertyPublisher.getProperty("name")%></h3>
<p>Age :&nbsp;<%= propertyPublisher.getProperty("age")%></p>
<p>Sex :&nbsp;<%= propertyPublisher.getProperty("sex")%></p>
</div>
</ps:component>