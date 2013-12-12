<%@ taglib uri="pageStudioTags" prefix="ps" %>
<%@ taglib prefix="pagestudio" tagdir="/WEB-INF/tags"%>
<ps:component>
<h4>Select the image to use for this component</h4>
    <div style="clear: both; padding-top: 10px;">
        <label for="alt">Alternative Text:</label><input size="50" name="alt" value="<%= propertyPublisher.getProperty("alt")%>"/>
    </div>
    <div style="clear: both; padding-top: 10px;">
    <span class="label">Image:</span>
    <span>
	    <select id="imgList" name="selection" >
	        <% String selection = propertyPublisher != null  ? propertyPublisher.getProperty("selection") : null; %>
	        <option <% if (selection != null && selection.equals("/pageStudio/components/examples/external-image/cubes_red.png")) out.print(" selected='selected' "); %> label="Red Image" value="/pageStudio/components/examples/external-image/cubes_red.png">Red Image</option>
	        <option <% if (selection != null && selection.equals("/pageStudio/components/examples/external-image/cubes_blue.png")) out.print(" selected='selected' "); %> label="Blue Image" value="/pageStudio/components/examples/external-image/cubes_blue.png">Blue Image</option>
	    </select>
    </span>
    </div>
</ps:component>