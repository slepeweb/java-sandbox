<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

 $("#payee").change(function(e) {	
	var payeeName = $(this).val();
	var major = $("select[name='major']").find(":selected").val();
	var memo = $("input[name='memo']").val();
	
	if (! major) {
  	$.ajax({
	    url: webContext + "/rest/transaction/latest/bypayee/" + payeeName,
	    type: "GET",
	    contentType: "application/json",
	    dataType: "json",
	    success: function(trn) {
	      $("select[name='major']").val(trn.majorCategory);
			  var promet = _updateMinorCategories();
			  promet.done(function(res) {						  
			      if (! memo) {
				      $("input[name='memo']").val(trn.memo);
			      }
			      $("select[name='minor']").val(trn.minorCategory);
			      
			      var amountStr = trn.amountInPounds;
			      var len = amountStr.length;					      
			      if (len > 0 && amountStr.substring(0, 1) == '-') {
			    	  amountStr = amountStr.substring(1);
			      }
			      
			      if (trn.amount < 0) {
			    	  $("#debit").prop("checked", true);
			    	  //$("#credit").prop("checked", false);
			      }
			      else {
			    	  //$("#debit").prop("checked", false);
			    	  $("#credit").prop("checked", true);
			      }
			      
			      $("#amount").val(amountStr);
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
	}
 });
