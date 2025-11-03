<%@ tag%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp"%>

<cms:debug>
	<!-- tags/cms/editor/addnew.tag -->
</cms:debug>

<c:set var="_positionOptions">below,alongside</c:set>
<c:set var="_lastPosition" value="${_stickyAddNewControls.lastPosition}" />

<c:if test="${editingItem.shortcut or editingItem.type.media}">
	<c:set var="_positionOptions">alongside</c:set>
	<%-- Overriding the cookie value setting will force the 'alongside' option to be selected --%>
	<c:set var="_lastPosition">alongside</c:set>
</c:if>

<form>
	<div class="ff">
		<label>Add new item: </label>
		<div class="inputs">
			<select name="relativePosition">
				<option value="0">Choose ...</option>
				<c:forTokens items="${_positionOptions}" delims="," var="relativePosition">
					<option value="${relativePosition}"
						<c:if test="${_lastPosition eq relativePosition}">selected</c:if>>${relativePosition}</option>
				</c:forTokens>
			</select> '<span class="current-item-name">${editingItem.name}</span>'
		</div>
	</div>

	<div class="ff">
		<label for="type">Template: </label>
		<div class="inputs">
			<select name="template">
				<option value="0">Choose ...</option>
				<hr />

				<%-- First display non-admin templates --%>
				<c:forEach items="${availableTemplatesForType}" var="template">
					<c:if test="${not template.admin}">
						<option value="${template.id}"
							<c:if test="${_stickyAddNewControls.lastTemplate eq template.id}">selected</c:if>
							data-isproduct="${template.itemTypeId == _productTypeId ? 1 : 0}">${template.name}</option>
					</c:if>
				</c:forEach>

				<%-- Then display templates only available to admin users --%>
				<c:set var="adminTemplateOptions">
					<c:forEach items="${availableTemplatesForType}" var="template">
						<c:if test="${template.admin}">
							<option value="${template.id}"
								<c:if test="${_stickyAddNewControls.lastTemplate eq template.id}">selected</c:if>
								data-isproduct="${template.itemTypeId == _productTypeId ? 1 : 0}">${template.name}</option>
						</c:if>
					</c:forEach>
				</c:set>

				<c:if test="${not empty adminTemplateOptions}">
					<hr />
					${adminTemplateOptions}
				</c:if>

			</select>
		</div>
	</div>

	<div class="ff">
		<label for="type">Item type: </label>
		<div class="inputs">
			<select name="itemtype">
				<option value="0">Choose ...</option>
				<c:forEach items="${editingItem.site.availableItemTypes}" var="st">
					<option value="${st.type.id}"
						<c:if test="${_stickyAddNewControls.lastType eq st.type.id}">selected</c:if>
						data-isproduct="${st.type.id == _productTypeId ? 1 : 0}">${st.type.name}</option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="ff">
		<label for="type">Link type: </label>
		<div class="inputs">
			<select name="linktype">
				<c:forEach items="${editingItem.orthogonalLinkTypes}" var="ltype">
					<option value="${ltype}">${ltype}</option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="ff">
		<label for="type">Link name: </label>
		<div class="inputs">
			<select name="linkname">
			</select>
		</div>
	</div>

	<div class="ff">
		<label for="name">Name: </label>
		<div class="inputs">
			<input name="name" value="" />
		</div>
	</div>

	<div class="ff">
		<label for="simplename">Simple name: </label>
		<div class="inputs">
			<input name="simplename" value="" />
		</div>
	</div>

	<%-- This div will only be visible if the selected item type is Product --%>
	<div id="core-commerce">
		<div class="ff">
			<label for="partNum">Part number: </label>
			<div class="inputs">
				<input type="text" name="partNum" value="" />
			</div>
		</div>

		<div class="ff">
			<label for="price">Price: </label>
			<div class="inputs">
				<input type="text" name="price" value="0" />
			</div>
		</div>

		<div class="ff">
			<label for="stock">Stock: </label>
			<div class="inputs">
				<input type="text" name="stock" value="0" />
			</div>
		</div>

		<div class="ff">
			<label for="alphaaxis">Axis A: </label>
			<div class="inputs">
				<select id="alphaaxis" name="alphaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"
							<c:if 
							test="${axis.id eq editingItem.alphaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="ff">
			<label for="betaaxis">Axis B: </label>
			<div class="inputs">
				<select id="betaaxis" name="betaaxis">
					<option value="-1">Choose ...</option>
					<c:forEach items="${availableAxes}" var="axis">
						<option value="${axis.id}"
							<c:if 
							test="${axis.id eq editingItem.betaAxisId}"> selected</c:if>>${axis.shortname}</option>
					</c:forEach>
				</select>
			</div>
		</div>

	</div>

	<div class="button-set">
		<button class="action" type="button" disabled="disabled"
			title="Add a new item below/alongside the current item">Add</button>
		<button class="reset" type="button" disabled="disabled" ${_resetHelp}>Reset
			form</button>
	</div>
</form>
