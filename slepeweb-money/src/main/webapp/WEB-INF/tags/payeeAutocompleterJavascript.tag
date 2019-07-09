<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="autofill" required="false" type="java.lang.Boolean" rtexprvalue="true" %>

<!-- payeeAutocompleterJavascript.tag -->

<script>
	$(function(){
		$.ajax({
			url: webContext + "/rest/payee/list/all",
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			success: function(data) {
				// init the widget with response data and let it do the filtering
				$("#payee").autocomplete({
					source: data,
					minLength: 2
					<c:if test="${autofill}">, 
						change: function() {
							_autofill();
						}
					</c:if>
				});
			},
			error: function(x, t, m) {
				console.trace();
			}
		});
	});
</script>