<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

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
	      });
	    },
	    error: function(x, t, m) {
	      console.trace();
	      /*
	      if (!(console == 'undefined')) {
	        console.log("ERROR: " + x + t + m);
	      }
	      console.log(" At the end");
	      */
	    }
	  });
