<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%-- 
	Paginação reutilizável. Para usar, a página que inclui precisa:
	  1) declarar a taglib c (JSTL core);
	  2) definir  <c:set var="baseUrl" value="vehicles" />  (rota da listagem, sem a barra inicial);
	  3) incluir  <%@include file="pagination.jsp"%>
	Os atributos currentPage e totalPages são colocados na requisição pelo controller
	(ControllerUtil.preparePagination).
--%>
<ul class="pagination">
	<%-- c:url acrescenta o context path e monta  /rota?page=N  --%>
	<c:url var="prevUrl" value="/${baseUrl}"><c:param name="page" value="${currentPage - 1}" /></c:url>
	<c:url var="nextUrl" value="/${baseUrl}"><c:param name="page" value="${currentPage + 1}" /></c:url>

	<li class="${currentPage <= 1 ? 'disabled' : ''}">
		<a href="${currentPage <= 1 ? '#' : prevUrl}">&lt; Anterior</a>
	</li>

	<c:forEach var="i" begin="1" end="${totalPages}">
		<c:url var="pageUrl" value="/${baseUrl}"><c:param name="page" value="${i}" /></c:url>
		<li class="${i == currentPage ? 'active' : ''}"><a href="${pageUrl}">${i}</a></li>
	</c:forEach>

	<li class="${currentPage >= totalPages ? 'disabled' : ''}">
		<a href="${currentPage >= totalPages ? '#' : nextUrl}">Próximo &gt;</a>
	</li>
</ul>
