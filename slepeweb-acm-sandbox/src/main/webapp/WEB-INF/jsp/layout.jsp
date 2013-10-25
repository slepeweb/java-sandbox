<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><!DOCTYPE html>


<html>
<head><tiles:insertAttribute name="head"/></head>
<body>
	<tiles:insertAttribute name="body" />
	<tiles:insertAttribute name="foot"/>
</body>
</html>