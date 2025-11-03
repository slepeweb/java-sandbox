<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="widefield-wrapper">
	<div id="widefield-toolbar">
		<div class="left">
			<label>Insert markup:</label>
			<select id="widefield-menu">
				<option value="">Choose ...</option>
				<hr />
				<option value="p">&lt;p&gt;</option>
				<option value="aside">&lt;aside&gt;</option>
				<option value="code">&lt;code&gt;</option>
				<hr />
				<option value="br">&lt;break&gt;</option>
				<option value="hr">&lt;rule&gt;</option>
				<hr />
				<option value="h2">&lt;h2&gt;</option>
				<option value="h3">&lt;h3&gt;</option>
				<option value="h4">&lt;h4&gt;</option>
				<hr />
				<option value="link">&lt;a&gt;</option>
				<option value="ul">&lt;ul&gt;</option>
				<option value="table">&lt;table&gt;</option>
				<option value="div">&lt;div&gt;</option>
				<hr />
				<option value="ximg">&lt;ximg&gt; (custom)</option>
				<option value="xcomp">&lt;xcomp&gt; (custom)</option>
			</select>
		</div>
		
		<div class="right">
			<div id="widefield-close-icon" class="widefield-icon">X</div>
		</div>
	</div>
	
	<textarea id="widefield-editor"></textarea>
</div>
