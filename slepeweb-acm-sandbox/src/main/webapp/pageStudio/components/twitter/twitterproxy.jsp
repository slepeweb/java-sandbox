<%	
	String action = request.getParameter("action");
	response.setContentType("application/json;charset=UTF-8");
	if (action != null) {
		String url = null;
		String qs = request.getQueryString();
		
		if(action.equals("friends")) {
			url = "http://twitter.com/statuses/friends_timeline.json?"+qs;
		}
		else if(action.equals("user")) {
			url = "http://twitter.com/statuses/user_timeline.json?"+qs;
		}
		else if(action.equals("update")) {
			url = "http://twitter.com/statuses/update.json";
		}
		else if(action.equals("favorite")) {
			String id = request.getParameter("id");
			if (id != null) {
				url = "http://twitter.com/favorites/create/"+id+".json";
			}
		}
		else if(action.equals("unfavorite")) {
			String id = request.getParameter("id");
			if (id != null) {
				url = "http://twitter.com/favorites/destroy/"+id+".json";
			}
		}
		else if(action.equals("delete")) {
			String id = request.getParameter("id");
			if (id != null) {
				url = "http://twitter.com/statuses/destroy/"+id+".json";
			}
		}
		
		if (url != null) {
			java.net.URL twit = new java.net.URL(url);
			java.io.InputStream is = null;
			
			if(action.equals("update") || action.equals("favorite") || action.equals("unfavorite") || action.equals("delete")) {
				java.net.HttpURLConnection conn = (java.net.HttpURLConnection)twit.openConnection();
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setAllowUserInteraction(false);
				
				try {
					java.io.OutputStreamWriter dataOut = new java.io.OutputStreamWriter(conn.getOutputStream());
					dataOut.write(qs);
					dataOut.flush();
					dataOut.close();
				}
				catch (Exception e) {
					if (e != null) {
						response.setStatus(500);
						out.print(e);
					}
				}
				
				conn.getInputStream();
			}
			else {
				try {
					is = twit.openStream();
				}
				catch (java.io.IOException e) {
					response.setStatus(400);
				}
			}
			
			if (is != null) {
				try {
					java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(is));
					
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
						out.println(inputLine);
					
					in.close();
				}
				catch (Exception e) {
					response.setStatus(500);
					out.print(e);
				}
			}
			
		}
	}
%>