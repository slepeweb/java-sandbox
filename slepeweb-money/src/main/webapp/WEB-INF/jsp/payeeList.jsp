<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2>Payees</h2>			
	<p><strong>Total no. of payees = ${_count}</strong></p>

	<c:choose><c:when test="${not empty _payees}">
	
		<div id="accordion">		
			<c:forEach items="${_payees}" var="_m">
				<h3>${_m.name}</h3>
				<div>
					<table>
						<c:forEach items="${_m.objects}" var="_p">
							<tr>
								<td data-id="${_p.id}"><i class="fas fa-bars payee-menu"></i>&nbsp;&nbsp;${_p.name}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:forEach>			
		</div>
					
	</c:when><c:otherwise>
		<p><strong>No payees defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

<div id="payee-menu-dialog">
	<ul>
		<li>Edit</li>
		<li>Delete</li>
		<li class="payee-menu-find">Find transactions</li>
		<li class="payee-menu-close">Close menu</li>
	</ul>
</div>


<script>
$(".payee-menu").click(function(e){
	var id = $(this).parent().attr("data-id");
	var dialog = $("#payee-menu-dialog");
	if (dialog.css("visibility") == "visible") {
		dialog.css("visibility", "hidden");
		return;
	}
	
	var menuOffset = $(this).offset();
	dialog.offset({left: menuOffset.left - 150, top: menuOffset.top});		
	dialog.css("visibility", "visible");
	dialog.attr("data-id", id);
	e.stopPropagation();
});

$(".payee-menu-close").click(function(e){
	$("#payee-menu-dialog").css("visibility", "hidden");
});

$(".payee-menu-find").click(function(e){
	var id = $(this).parent().parent().attr("data-id");
	window.location = "/money/transaction/list/by/payee/" + id;
});
</script>