<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>

<ps:component>
<div class="ps_form">
	<p>Please configure the components details</p>
    <div style="clear: both; padding-top: 10px;">
      <label for="username">Name:</label><input name="name" type="text" size="25" value="<%= propertyPublisher.getProperty("username")%>" />
    </div>
    <div style="clear: both; padding-top: 10px;">
      <label for="age">Age:</label><input size="25" name="age" value="<%= propertyPublisher.getProperty("age")%>"/>
    </div>
    <div style="clear: both; padding-top: 10px;" >
      <label for="sex">Sex:</label>
      <div class="ps_form_radio_group">
      	<% boolean female = "female".equals(propertyPublisher.getProperty("sex")); %>
      	<input <% if (!female) out.print(" checked='checked'" ); %> name="sex" type="radio" value="male">Male</input>
      	<input <% if (female) out.print(" checked='checked'" ); %> name="sex" type="radio" value="female">Female</input>
      </div>
    </div>
    <div style="clear: both; padding-top: 10px;" >
    </div>
</div>
</ps:component>