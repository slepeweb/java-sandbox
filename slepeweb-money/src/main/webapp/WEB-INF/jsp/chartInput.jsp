<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2>Choose category spends to chart</h2>			
	
	<form method="post" action="${_ctxPath}/chart/by/categories/out">
		<table>
		    <tr>
		        <td class="heading"><label for="from">From</label></td>
		        <td><input id="from" type="text" name="from" placeholder="Enter (for example) '2000'"
		        	value="${_fromYear}" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="numYears">No. of years</label></td>
		        <td><input id="numYears" type="text" name="numYears" placeholder="Enter (for example) '10'"
		        	value="${_numYears}" /></td>
		    </tr>
		</table>
		
		<div id="accordion">
				<c:forEach items="${_categories}" var="_m">
					<h3>${_m.name}</h3>
					<div>
						<table>
							<c:forEach items="${_m.objects}" var="_c">
								<tr>
									<td><input type="checkbox" name="_${_c.id}" value="${_c.major}|${_c.minor}" /></td>
									<td>${_c.minor}</td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</c:forEach>
		</div>
		
		<input type="submit" value="Submit" />
	</form>			

</mny:standardLayout>
