<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInpageCss" scope="request">
	details {
		margin-bottom: 2em;
	}
	
	table th {
		width: auto;
	}
</c:set>

<pho:pageLayout type="std">
	<gen:debug><!-- jsp/pho/csvExport.jsp --></gen:debug>
		
	<div class="main">
		<h2>CSV Export</h2>
		
		<details>
			<summary>NOTES</summary>
			<ul>
				<li>The CSV file containing the full content in this table can be found on the server at 
					<code>/tmp/photos.csv</code>.</li>
				<li>Append '?refresh' to the URL if you want to run this again after a change to the content.</li>
				<li>Media files are located below <code>/home/photos</code> on the server. The file paths
					in the table below are relative to this folder.</li>
				<li>Some dates are approximate, which is often the case for older no-digital photos. In these
					cases, you will see a partial date appended with an 'a', to indicate that it's an approximation.</li>
				<li>There are ${fn:length(_csv)} entries in the table below.</li>
			</ul>
		</details>
		
		<table>
			<tr>
				<c:forEach items="${_header}" var="_h">
					<th>${_h}</th>
				</c:forEach>
			</tr>
		
			<c:forEach items="${_csv}" var="_row">
				<tr>
					<td><a target="_blank" href="${_row.itemPath}">${_row.mediaFilePath}</a></td>
					<td>${_row.mediaType}</td>
					<td>${_row.tags}</td>
					<td>${_row.title}</td>
					<td>${_row.teaser}</td>
					<td>${_row.dateishStr}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</pho:pageLayout>