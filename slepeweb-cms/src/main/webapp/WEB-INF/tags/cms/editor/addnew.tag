<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>
        
<cms:debug><!-- tags/cms/editor/addnew.tag --></cms:debug>
	
<div id="add-tab">
	<form>
		<div>
			<label for="type">Template: </label>
			<select name="template">
				<option value="0">Choose ...</option>
				<c:forEach items="${editingItem.site.availableTemplates}" var="template">
					<option value="${template.id}" 
						data-isproduct="${template.itemTypeId == _productTypeId ? 1 : 0}">${template.name}</option>
				</c:forEach>
			</select>
		</div>
		<div>
			<label for="type">Type: </label>
			<select name="itemtype">
				<option value="0">Choose ...</option>
				<c:forEach items="${editingItem.site.availableItemTypes}" var="it">
					<option value="${it.id}"
						data-isproduct="${it.id == _productTypeId ? 1 : 0}">${it.name}</option>
				</c:forEach>
			</select>			
		</div>
		<div>
			<label for="name">Name: </label><input name="name" value="" />
		</div>
		<div>
			<label for="simplename">Simple name: </label><input name="simplename" value="" />
		</div>
		
		<%-- This div will only be visible if the selected item type is Product --%>
		<div id="core-commerce">
			<div>
				<label for="partNum">Part number: </label><input type="text" name="partNum" value="" />
			</div>
			<div>
				<label for="price">Price: </label><input type="text" name="price" value="0" />
			</div>
			<div>
				<label for="stock">Stock: </label><input type="text" name="stock" value="0" />
			</div>
			<div>
				<label for="alphaaxis">Axis A: </label>
				<select id="alphaaxis" name="alphaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"<c:if 
							test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
			<div>
				<label for="betaaxis">Axis B: </label>
				<select id="betaaxis" name="betaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"<c:if 
							test="${axis.id eq editingItem.betaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div>
			<label>&nbsp;</label><button id="add-button" type="button">Add</button>
		</div>
	</form>
</div>
