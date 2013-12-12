<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>


<ps:component>
<p>Number of properties: <%= componentProperties.size() %></p>
<div class="ps_form">
	<span class="ps_form_title">Validator Test Properties</span>
	<label class="label">SingleValueList</label>
	<select id="list" name="singlevaluelist1" >
		<option <% if (propertyPublisher.getProperty("singlevaluelist1").equals("value1")) out.print(" selected='selected' "); %>  value="value1">Value 1</option>
		<option <% if (propertyPublisher.getProperty("singlevaluelist1").equals("value2")) out.print(" selected='selected' "); %> value="value2">Value 2</option>
		<option <% if (propertyPublisher.getProperty("singlevaluelist1").equals("invalid1")) out.print(" selected='selected' "); %> value="invalid1">Invalid 1</option>
		<option <% if (propertyPublisher.getProperty("singlevaluelist1").equals("invalid2")) out.print(" selected='selected' "); %> value="invalid2">Invalid 2</option>
	</select>
	
	<label>String</label>
	<input name="string1" type="text" size="25" value="<%= propertyPublisher.getProperty("string1") %>"/>
	
	<label>Numeric</label>
	<input name="numeric1" type="text" size="25" value="<%= propertyPublisher.getProperty("numeric1") %>"/>
	
	<label>List</label>
	<% 	java.util.List<String> list1 = null;
		String[] props = propertyPublisher.getPropertyList("list1");
		if (props == null) props = new String[0]; 
		list1 = java.util.Arrays.asList(props);  %>
	<select multiple="multiple" id="list" name="list1" >
		<option <% if (list1.contains("/l1")) out.print(" selected='selected' "); %> label="List1" value="/l1">List1</option>
		<option <% if (list1.contains("/l2")) out.print(" selected='selected' "); %> label="List2" value="/l2">List2</option>
		<option <% if (list1.contains("/l3")) out.print(" selected='selected' "); %> label="List3" value="/l3">List3</option>
		<option <% if (list1.contains("/l4")) out.print(" selected='selected' "); %> label="List4" value="/l4">List4</option>
		<option <% if (list1.contains("/oddoneout")) out.print(" selected='selected' "); %> label="OddOneOut" value="/oddoneout">OddOneOut</option>
	</select>
</div>
</ps:component>
