<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%>
    
<c:forEach items="${__genericList}" var="item">
	<a href="${item.path}">${item.text}</a><br />
</c:forEach>
