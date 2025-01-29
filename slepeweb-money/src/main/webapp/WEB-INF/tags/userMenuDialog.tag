<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- userMenuDialog.tag -->

<c:set var="_menuOpener" value="user-welcome" />    <%-- Where to click to open the menu --%>
<c:set var="_menuDialogId" value="user-menu" />     <%-- The menu dialog --%>
<c:set var="_menuCloseClass" value="menu-close" />  <%-- The class of the element containing the close icon --%>

<div id="${_menuDialogId}">
	<ul>
		<li class="${_menuCloseClass}"><i class="far fa-window-close"></i></li>
		<li><a href="${_ctxPath}/login?logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
	</ul>
</div>


<script>
$("#${_menuOpener}").click(function(e){
	var dialog = $("#${_menuDialogId}");
	var menuOffset = $(this).offset();	
	dialog.offset({left: menuOffset.left, top: menuOffset.top + 28});		
	dialog.css("visibility", "visible");
	e.stopPropagation();
});

$(".${_menuCloseClass}").click(function(e){
	$("#${_menuDialogId}").css("visibility", "hidden");
});
</script>