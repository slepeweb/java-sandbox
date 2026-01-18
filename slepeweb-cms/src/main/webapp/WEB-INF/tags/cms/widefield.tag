<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="widefield-wrapper">
	<div id="widefield-toolbar">
		<div class="left">
			<label>Insert markup:</label>
			<select id="widefield-menu">
				<option value="">Choose ...</option>
				<hr />
				<option value="h2">&lt;h2&gt;</option>
				<option value="h3">&lt;h3&gt;</option>
				<option value="h4">&lt;h4&gt;</option>
				<hr />
				<option value="div">&lt;div&gt;</option>
				<option value="p">&lt;para&gt;</option>
				<option value="link">&lt;a&gt;</option>
				<hr />
				<option value="strong">&lt;strong&gt;</option>
				<option value="i">&lt;italic&gt;</option>
				<option value="u">&lt;underline&gt;</option>
				<hr />
				<option value="aside">&lt;aside&gt;</option>
				<option value="code">&lt;code&gt;</option>
				<hr />
				<option value="br">&lt;break&gt;</option>
				<option value="hr">&lt;rule&gt;</option>
				<hr />
				<option value="ul">&lt;ul&gt;</option>
				<option value="ol">&lt;ol&gt;</option>
				<option value="li">&lt;li&gt;</option>
				<hr />
				<option value="table">&lt;table&gt;</option>
				<option value="tr">&lt;row&gt;</option>
				<option value="td">&lt;cell&gt;</option>
				<hr />
				<option value="ximg">&lt;ximg&gt; (custom)</option>
				<option value="xlink">&lt;xlink&gt; (custom)</option>
				<option value="xcomp">&lt;xcomp&gt; (custom)</option>
			</select>
		</div>
		
		<div class="right">
			<div id="widefield-close-icon" class="widefield-icon">X</div>
		</div>
	</div>
	
	<textarea id="widefield-editor"></textarea>
</div>
