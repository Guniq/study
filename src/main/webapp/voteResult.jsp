<%@page import="model.ResultDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	//list 불러오기
	ArrayList<ResultDTO> list = new ArrayList<ResultDTO>();
	list = (ArrayList<ResultDTO>)request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>voteResult</title>
</head>
<body>
<%@ include file="header.jsp" %>
<section>
<h2>∙ 후보자등수 ∙</h2>
<div class="table">
	<table>
		<tr>
			<th>후보번호</th>
			<th>성명</th>
			<th>총투표건수</th>
		</tr>
		<%
		for(int i=0; i<list.size(); i++){
		%>
		<tr>
			<td><%=list.get(i).getM_no()%></td>
			<td><%=list.get(i).getM_name()%></td>
			<td><%=list.get(i).getV_total() %></td>
		</tr>
		<%
		}
		%>
	</table>
</div>
</section>
<%@ include file="footer.jsp" %>
</body>
</html>