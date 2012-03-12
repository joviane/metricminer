<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>MetricMiner - Project ${project.name}</title>
		<c:import url="../import/head.jsp" />
	</head>
	<body>
		<c:import url="../import/header.jsp" />
		<section id="project-details">
			<h1>${project.name}</h1>
		</section>
		<c:import url="../import/footer.jsp" />
	</body>
</html>