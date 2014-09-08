<%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
	taglib prefix="gen" tagdir="/WEB-INF/tags"%>

<h3>${_comp.heading}</h3>
<div>This is a ${_comp.view} feature. ${_comp.blurb}</div>

<%-- <gen:insertComponentSet site="${_site}" owner="${_comp}" /> --%>