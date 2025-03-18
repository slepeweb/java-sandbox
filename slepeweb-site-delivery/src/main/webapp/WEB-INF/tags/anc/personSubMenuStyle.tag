<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

/* tags/anc/personSubMenuStyle.tag */

#sub-menu {
	text-align: left;
	margin-top: 0em;
}

#sub-menu .selected {
	font-weight: bold;
} 

#sub-menu .disabled {
	opacity: 0.5;
} 

@media (min-width: 48em) {
  .pull-right-sm {
    float:right;
  }
  
  #sub-menu {
		margin-top: 7em;
  }
}

		