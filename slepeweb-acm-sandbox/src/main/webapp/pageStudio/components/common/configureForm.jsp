<%
	//singlevaluelist
	if(pageContext.getRequest().getParameter("type").equals("singlevaluelist")){
		
		String items = pageContext.getRequest().getParameter("range");
		String defaultValue = pageContext.getRequest().getParameter("defaultValue");
		String item;
		
		out.print("<tr><td style='text-align: right; font-weight: bold'>" + pageContext.getRequest().getParameter("label") + ":</td><td><select name='" + pageContext.getRequest().getParameter("name") + "'>");
		int itemStart = 0;
		for(int i = 0; i<items.length(); i++){
			if(items.charAt(i) == '|'){
				item = items.substring(itemStart, i);
				out.print("<option value='" + item + "'");
				if(pageContext.getRequest().getParameter("defaultValue") != null && defaultValue.equals(item)){
					out.print(" selected='selected'");
				} 
				out.print(">" + item + "</option>");
				itemStart = i+1;
			}
		}
		item = items.substring(itemStart, items.length());
		out.print("<option value='" + item + "'");
		if(defaultValue != null && defaultValue.equals(item)) {
			out.print(" selected='selected'");
		} 
		out.print(">" + item + "</option>");
		out.print("</select></td></tr>");

	}
	//list
	else if(pageContext.getRequest().getParameter("type").equals("list")){
		String items = pageContext.getRequest().getParameter("range");
		out.print("<tr><td style='text-align: right; font-weight: bold;'>" + pageContext.getRequest().getParameter("label") + ":</td><td>" + "<select name='" + pageContext.getRequest().getParameter("name") + "' multiple='true'>");

		int itemStart = 0;
		for(int i = 0; i<items.length(); i++){
			if(items.charAt(i) == '|'){
				String item = items.substring(itemStart, i);
				
				out.print("<option value='" + item + "'>" + item + "</option>");
				itemStart = i+1;
			}
		}
		out.print("<option value='" + items.substring(itemStart, items.length()) + "'>" + items.substring(itemStart, items.length())+ "</option>");
		out.print("</select></td></tr>");

	}
	//string or numeric
	else{
		String value = "";
		if(pageContext.getRequest().getParameter("defaultValue") != null){
			value = pageContext.getRequest().getParameter("defaultValue");
		}
		out.print("<tr><td style='text-align: right; font-weight: bold;'>" + pageContext.getRequest().getParameter("label") + ":</td><td><input type='text' name='"+ pageContext.getRequest().getParameter("name") + "' value='" + value + "'/></td></tr>");
	}
%>