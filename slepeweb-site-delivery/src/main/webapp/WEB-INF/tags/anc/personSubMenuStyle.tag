<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

/* tags/anc/personSubMenuStyle.tag */

#sub-menu {
	list-style-type: none;
	text-align: left;
	margin-top: 0em;
}

#sub-menu .selected {
	opacity: 0.7;
} 

#sub-menu .disabled {
	opacity: 0.3;
} 

@media (min-width: 48em) {
  .pull-right-sm {
    float:right;
  }
  
  #sub-menu {
		margin-top: 7em;
		text-align: center;
  }
}

		