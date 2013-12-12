<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>

<ps:component>

<div class="ps_form">
<p>Please configure the components details</p>
    <div style="clear: both; padding-top: 10px;">
      <label for="username">Name:</label><input name="name" type="text" size="25" value="<%= propertyPublisher.getProperty("name")%>" />
    </div>
    <div style="clear: both; padding-top: 10px;">
      <label for="age">Age:</label><input size="25" name="age" value="<%= propertyPublisher.getProperty("age")%>"/>
    </div>
    <div style="clear: both; padding-top: 10px;" >
      <label for="sex">Sex:</label>
      <div class="ps_form_radio_group">
      	<input <% if (propertyPublisher.getProperty("sex").equals("male")) out.print(" checked='checked'" ); %> name="sex" type="radio" value="male" checked>Male</input>
      	<input <% if (propertyPublisher.getProperty("sex").equals("female")) out.print(" checked='checked'" ); %> name="sex" type="radio" value="female">Female</input>
      </div>
    </div>
    <div style="clear: both; padding-top: 10px;" >
    </div>
</div>
</ps:component>