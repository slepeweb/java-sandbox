<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="target" required="true" rtexprvalue="true" %>

<c:set var="_menuIconClass" value="menu-icon" />
<c:set var="_menuDialogId" value="menu-action-dialog" />
<c:set var="_menuCloseClass" value="menu-close" />
<c:set var="_menuFindClass" value="menu-find" />

<div id="${_menuDialogId}">
	<ul>
		<li class="${_menuCloseClass}"><i class="far fa-window-close"></i></li>
		<li>Edit</li>
		<li>Delete</li>
		<li class="${_menuFindClass}">Find transactions</li>
	</ul>
</div>


<script>
$(".${_menuIconClass}").click(function(e){
	var dialog = $("#${_menuDialogId}");
	var td = $(this).parent();
	var id = td.attr("data-id");
	var menuOffset = $(this).offset();
	dialog.offset({left: menuOffset.left, top: menuOffset.top + 28});		
	dialog.css("visibility", "visible");
	dialog.attr("data-id", id);
	
	// Un-highlight any previously highlighted cells
	$(".highlighted").removeClass("highlighted");
	
	// Highlight selected cell
	td.addClass("highlighted");
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