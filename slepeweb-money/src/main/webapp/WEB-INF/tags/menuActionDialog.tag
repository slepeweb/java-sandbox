<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="target" required="true" rtexprvalue="true" %><%@ 
	attribute name="disable" required="false" rtexprvalue="true" %>
	
<!-- menuActionDialog.tag -->

<%--
	This tag was used to provide a menu for each item in a list, offering 'Edit' and 'Delete' (etc) menu options.
	It is currently redundant, but is being retained for reference.
 --%>

<c:set var="_menuIconClass" value="menu-icon" />
<c:set var="_menuDialogId" value="menu-action-dialog" />
<c:set var="_menuCloseClass" value="menu-close" />
<c:set var="_menuFindClass" value="menu-find" />

<div id="${_menuDialogId}">
	<ul>
		<li class="${_menuCloseClass}"><i class="far fa-window-close"></i></li>
		<c:if test="${not fn:contains(disable, 'edit')}"><li>Edit</li></c:if>
		<c:if test="${not fn:contains(disable, 'delete')}"><li>Delete</li></c:if>
		<c:if test="${target ne 'none'}"><li class="${_menuFindClass}">Find transactions</li></c:if>
	</ul>
</div>


<script>
$(".${_menuIconClass}").click(function(e){
	var dialog = $("#${_menuDialogId}");
	var menuIconCell = $(this);
	var menuIconRow = menuIconCell.parent();
	var id = menuIconCell.attr("data-id");
	var menuOffset = $(this).offset();
	
	dialog.offset({left: menuOffset.left - 174, top: menuOffset.top + 28});		
	dialog.css("visibility", "visible");
	dialog.attr("data-id", id);
	
	// Un-highlight any previously highlighted cells
	$(".highlighted").removeClass("highlighted");
	
	// Highlight selected cell
	menuIconRow.addClass("highlighted");
	e.stopPropagation();
});

$(".${_menuCloseClass}").click(function(e){
	$("#${_menuDialogId}").css("visibility", "hidden");
	$(".highlighted").removeClass("highlighted");
});

$(".${_menuFindClass}").click(function(e){
	var id = $(this).parent().parent().attr("data-id");
	window.location = "${target}" + id;
});
</script>