<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Feedbacks</title>
</head>
<body>
	<%@include file="modal.html"%>
	<fmt:setLocale value="pt_BR" />
	<c:set var="client" value="${sessionScope.loggedUser.client}" />
	<c:set var="boss" value="${sessionScope.loggedUser.boss}" />
	<c:set var="activeMenu" value="feedbacks" />
	<c:set var="pageTitle" value="${client ? 'Meus Feedbacks' : 'Feedbacks'}" />
	<c:set var="pageSubtitle" value="${client ? 'Conte como foi o serviço feito no seu veículo.' : 'O que os clientes acharam dos nossos serviços.'}" />
	<c:set var="pageIcon" value="glyphicon-star" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<div id="alert" style="${not empty message ? 'display: block;' : 'display: none;'}" class="alert alert-dismissable ${alertType eq 1 ? 'alert-success' : 'alert-danger'}">
		  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
		  <c:out value="${message}" />
		</div>
		
		<div id="top" class="row">
			<div class="${client ? 'col-md-9' : 'col-md-12'}">
				<div class="input-group h2">
					<input name="data[search]" class="form-control" id="search" type="text" placeholder="Filtrar nesta página...">
					<span class="input-group-btn">
						<button class="btn btn-danger" type="submit">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</div>
			
			<%-- só o cliente envia feedback --%>
			<c:if test="${client}">
				<div class="col-md-3">
					<a href="${pageContext.request.contextPath}/feedback/form" class="btn btn-danger pull-right h2"><span class="glyphicon glyphicon-plus"></span>&nbsp;Novo Feedback</a>
				</div>
			</c:if>
		</div>
		
		<hr />
		
		<div id="list" class="row">
			<div class="table-responsive col-md-12">
				<table class="table table-striped table-hover" cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<th>Data</th>
							<c:if test="${not client}"><th>Cliente</th></c:if>
							<th>Carro</th>
							<th>Serviço</th>
							<c:if test="${not client}"><th>Atendido por</th></c:if>
							<th>Avaliação</th>
							<th>Comentário</th>
							<c:if test="${client}"><th>Editar</th></c:if>
							<c:if test="${client or boss}"><th>Excluir</th></c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="f" items="${feedbacks}">
							<tr>
								<td><fmt:formatDate value="${f.feedbackDate}" pattern="dd/MM/yyyy" /></td>
								<c:if test="${not client}"><td><c:out value="${f.user.name}" /></td></c:if>
								<td><c:out value="${f.service.vehicle.model}" /> <small class="text-muted">${f.service.vehicle.modelYear}</small></td>
								<td>
									<c:choose>
										<c:when test="${f.service.type eq 'LAVAGEM'}"><span class="chip chip-wash">Lavagem</span></c:when>
										<c:when test="${f.service.type eq 'POLIMENTO'}"><span class="chip chip-polish">Polimento</span></c:when>
										<c:when test="${f.service.type eq 'VITRIFICACAO'}"><span class="chip chip-coat">Vitrificação</span></c:when>
										<c:when test="${f.service.type eq 'HIGIENIZACAO'}"><span class="chip chip-clean">Higienização</span></c:when>
										<c:otherwise><span class="chip"><c:out value="${f.service.type}" /></span></c:otherwise>
									</c:choose>
								</td>
								<c:if test="${not client}">
									<td>
										<c:choose>
											<c:when test="${not empty f.service.employee}"><c:out value="${f.service.employee.user.name}" /></c:when>
											<c:otherwise><span class="muted-dash">&mdash;</span></c:otherwise>
										</c:choose>
									</td>
								</c:if>
								<td>
									<%-- estrelas (1 a 5) + selo com o nome da avaliação --%>
									<span class="stars" title="${f.stars} de 5">
										<c:forEach begin="1" end="5" var="i"><span class="glyphicon glyphicon-star${i <= f.stars ? '' : ' off'}"></span></c:forEach>
									</span>
									<c:choose>
										<c:when test="${f.rating eq 'RUIM'}"><span class="chip chip-r1">Ruim</span></c:when>
										<c:when test="${f.rating eq 'REGULAR'}"><span class="chip chip-r2">Regular</span></c:when>
										<c:when test="${f.rating eq 'BOM'}"><span class="chip chip-r3">Bom</span></c:when>
										<c:when test="${f.rating eq 'OTIMO'}"><span class="chip chip-r4">Ótimo</span></c:when>
										<c:otherwise><span class="chip chip-r5">Excelente</span></c:otherwise>
									</c:choose>
								</td>
								<td><c:out value="${f.comment}" /></td>
								
								<c:if test="${client}">
									<td class="actions">
										<a class="btn btn-info btn-xs" 
										   href="${pageContext.request.contextPath}/feedback/update?feedbackId=${f.id}">
										   <span class="glyphicon glyphicon-edit"></span>
										</a>
									</td>
								</c:if>
								<c:if test="${client or boss}">
									<td class="actions">
										<a class="btn btn-danger btn-xs modal-remove"
										   feedback-id="${f.id}" 
										   feedback-name="<c:out value="${f.service.vehicle.model}" />" data-toggle="modal" 
										   data-target="#delete-modal" href="#"><span 
										   class="glyphicon glyphicon-trash"></span></a>
									</td>
								</c:if>
							</tr>
						</c:forEach>
						<c:if test="${empty feedbacks}">
							<tr><td colspan="10" class="text-center text-muted">Nenhum feedback por aqui ainda.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
		
		<div id="bottom" class="row">
			<div class="col-md-12">
				<c:set var="baseUrl" value="feedbacks" />
				<%@include file="pagination.jsp"%>
			</div>
		</div>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/app.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// fecha o alert após 3 segundos
			setTimeout(function() {
				$("#alert").slideUp(500);
			}, 3000);
			
			// ao clicar na lixeira, passa o nome, o id e a ação (delete) para o modal 
			$(".modal-remove").click(function () {
				var name = $(this).attr('feedback-name');
				var id = $(this).attr('feedback-id');
				$(".modal-body #hiddenValue").text("o feedback do veículo '" + name + "'");
				$("#id").attr("value", id);
				$("#form").attr("action", "${pageContext.request.contextPath}/feedback/delete");
			})
		});
	</script>
</body>
</html>
