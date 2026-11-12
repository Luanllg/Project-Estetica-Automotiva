<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Serviços</title>
</head>
<body>
	<%@include file="modal.html"%>
	<fmt:setLocale value="pt_BR" />
	<%-- staff = chefe ou funcionário: só eles alteram serviços. O cliente apenas acompanha. --%>
	<c:set var="staff" value="${sessionScope.loggedUser.staff}" />
	<c:set var="activeMenu" value="services" />
	<c:set var="pageTitle" value="${staff ? 'Serviços' : 'Meus Serviços'}" />
	<c:set var="pageSubtitle" value="${staff ? 'Lavagem, polimento, vitrificação e higienização.' : 'Acompanhe o andamento do serviço no seu veículo.'}" />
	<c:set var="pageIcon" value="glyphicon-wrench" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<div id="alert" style="${not empty message ? 'display: block;' : 'display: none;'}" class="alert alert-dismissable ${alertType eq 1 ? 'alert-success' : 'alert-danger'}">
		  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
		  <c:out value="${message}" />
		</div>
		
		<div id="top" class="row">
			<div class="${staff ? 'col-md-9' : 'col-md-12'}">
				<div class="input-group h2">
					<input name="data[search]" class="form-control" id="search" type="text" placeholder="Filtrar nesta página...">
					<span class="input-group-btn">
						<button class="btn btn-danger" type="submit">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</div>
			
			<c:if test="${staff}">
				<div class="col-md-3">
					<a href="${pageContext.request.contextPath}/service/form" class="btn btn-danger pull-right h2"><span class="glyphicon glyphicon-plus"></span>&nbsp;Adicionar Serviço</a>
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
							<th>Tipo</th>
							<th>Veículo</th>
							<th>Responsável</th>
							<th>Valor</th>
							<th>Detalhes</th>
							<th>Status</th>
							<c:choose>
								<c:when test="${staff}">
									<th>Pago</th>
									<th>Editar</th>
									<th>Excluir</th>
								</c:when>
								<c:otherwise>
									<th>Feedback</th>
								</c:otherwise>
							</c:choose>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="s" items="${services}">
							<tr>
								<td><fmt:formatDate value="${s.serviceDate}" pattern="dd/MM/yyyy" /></td>
								<td>
									<c:choose>
										<c:when test="${s.type eq 'LAVAGEM'}"><span class="chip chip-wash">Lavagem</span></c:when>
										<c:when test="${s.type eq 'POLIMENTO'}"><span class="chip chip-polish">Polimento</span></c:when>
										<c:when test="${s.type eq 'VITRIFICACAO'}"><span class="chip chip-coat">Vitrificação</span></c:when>
										<c:when test="${s.type eq 'HIGIENIZACAO'}"><span class="chip chip-clean">Higienização</span></c:when>
										<c:otherwise><span class="chip"><c:out value="${s.type}" /></span></c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:out value="${s.vehicle.model}" />
									<%-- a equipe vê de quem é o carro; o cliente só vê os dele --%>
									<c:if test="${staff}"><small class="text-muted">(<c:out value="${s.vehicle.user.name}" />)</small></c:if>
								</td>
								<td>
									<c:choose>
										<c:when test="${not empty s.employee}"><c:out value="${s.employee.user.name}" /></c:when>
										<c:otherwise><span class="muted-dash">A definir</span></c:otherwise>
									</c:choose>
								</td>
								<td class="price"><fmt:formatNumber value="${s.price}" type="currency" /></td>
								<td><c:out value="${s.details}" /></td>
								<td>
									<c:choose>
										<c:when test="${s.status eq 'AGENDADO'}"><span class="chip chip-sched">Agendado</span></c:when>
										<c:when test="${s.status eq 'EM_ANDAMENTO'}"><span class="chip chip-progress">Em andamento</span></c:when>
										<c:otherwise><span class="chip chip-done">Concluído</span></c:otherwise>
									</c:choose>
								</td>
								
								<c:choose>
									<c:when test="${staff}">
										<td><span class="chip ${s.paid ? 'chip-yes' : 'chip-no'}">${s.paid ? 'Pago' : 'Pendente'}</span></td>
										<td class="actions">
											<a class="btn btn-info btn-xs" 
											   href="${pageContext.request.contextPath}/service/update?serviceId=${s.id}">
											   <span class="glyphicon glyphicon-edit"></span>
											</a>
										</td>
										<td class="actions">
											<a class="btn btn-danger btn-xs modal-remove"
											   service-id="${s.id}" 
											   service-name="<c:out value="${s.type} - ${s.vehicle.model}" />" data-toggle="modal" 
											   data-target="#delete-modal" href="#"><span 
											   class="glyphicon glyphicon-trash"></span></a>
										</td>
									</c:when>
									<c:otherwise>
										<td>
											<c:choose>
												<c:when test="${s.rated}"><span class="chip chip-yes">Avaliado</span></c:when>
												<c:when test="${s.finished}">
													<a class="btn btn-danger btn-xs" 
													   href="${pageContext.request.contextPath}/feedback/form?serviceId=${s.id}">
													   <span class="glyphicon glyphicon-star"></span>&nbsp;Avaliar
													</a>
												</c:when>
												<c:otherwise><span class="muted-dash">Disponível após a conclusão</span></c:otherwise>
											</c:choose>
										</td>
									</c:otherwise>
								</c:choose>
							</tr>
						</c:forEach>
						<c:if test="${empty services}">
							<tr><td colspan="10" class="text-center text-muted">Nenhum serviço encontrado.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
		
		<div id="bottom" class="row">
			<div class="col-md-12">
				<c:set var="baseUrl" value="services" />
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
				var serviceName = $(this).attr('service-name');
				var serviceId = $(this).attr('service-id');
				$(".modal-body #hiddenValue").text("o serviço '" + serviceName + "'");
				$("#id").attr("value", serviceId);
				$("#form").attr("action", "${pageContext.request.contextPath}/service/delete");
			})
		});
	</script>
</body>
</html>
