<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/addnew.tag --></cms:debug>
	
<form>
	<div class="ff">
		<label for="type">Template: </label>
		<select name="template">
			<option value="0">Choose ...</option>
			<c:forEach items="${editingItem.site.availableTemplates}" var="template">
				<option value="${template.id}" 
					data-isproduct="${template.itemTypeId == _productTypeId ? 1 : 0}">${template.name}</option>
			</c:forEach>
		</select>
	</div>
	<div class="ff">
		<label for="type">Type: </label>
		<select name="itemtype">
			<option value="0">Choose ...</option>
			<c:forEach items="${editingItem.site.availableItemTypes}" var="it">
				<option value="${it.id}"
					data-isproduct="${it.id == _productTypeId ? 1 : 0}">${it.name}</option>
			</c:forEach>
		</select>			
	</div>
	<div class="ff">
		<label for="name">Name: </label><input name="name" value="" />
	</div>
	<div class="ff">
		<label for="simplename">Simple name: </label><input name="simplename" value="" />
	</div>
	
	<%-- This div will only be visible if the selected item type is Product --%>
	<div id="core-commerce">
		<div class="ff">
			<label for="partNum">Part number: </label><input type="text" name="partNum" value="" />
		</div>
		<div class="ff">
			<label for="price">Price: </label><input type="text" name="price" value="0" />
		</div>
		<div class="ff">
			<label for="stock">Stock: </label><input type="text" name="stock" value="0" />
		</div>
		<div class="ff">
			<label for="alphaaxis">Axis A: </label>
			<select id="alphaaxis" name="alphaaxis">
				<option value="-1">Choose ...</option>
				<c:forEach items="${availableAxes}" var="axis">
					<option value="${axis.id}"<c:if 
						test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
				</c:forEach>
			</select>
		</div>
		<div class="ff">
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
		<button id="add-button" type="button"
			title="Add a new item of the specified type below the current item">Add</button>
	</div>
</form>
