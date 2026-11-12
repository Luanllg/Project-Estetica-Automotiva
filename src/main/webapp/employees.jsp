<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Equipe</title>
</head>
<body>
	<%@include file="modal.html"%>
	<fmt:setLocale value="pt_BR" />
	<%-- o funcionário só consulta; quem cadastra, edita e remove é o chefe --%>
	<c:set var="boss" value="${sessionScope.loggedUser.boss}" />
	<c:set var="activeMenu" value="employees" />
	<c:set var="pageTitle" value="Equipe" />
	<c:set var="pageSubtitle" value="Quem trabalha na oficina e o serviço que cada um está executando agora." />
	<c:set var="pageIcon" value="glyphicon-briefcase" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<div id="alert" style="${not empty message ? 'display: block;' : 'display: none;'}" class="alert alert-dismissable ${alertType eq 1 ? 'alert-success' : 'alert-danger'}">
		  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
		  <c:out value="${message}" />
		</div>
		
		<div id="top" class="row">
			<div class="${boss ? 'col-md-9' : 'col-md-12'}">
				<div class="input-group h2">
					<input name="data[search]" class="form-control" id="search" type="text" placeholder="Filtrar nesta página...">
					<span class="input-group-btn">
						<button class="btn btn-danger" type="submit">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</div>
			
			<c:if test="${boss}">
				<div class="col-md-3">
					<a href="${pageContext.request.contextPath}/employee/form" class="btn btn-danger pull-right h2"><span class="glyphicon glyphicon-plus"></span>&nbsp;Adicionar à Equipe</a>
				</div>
			</c:if>
		</div>
		
		<hr />
		
		<div id="list" class="row">
			<div class="table-responsive col-md-12">
				<table class="table table-striped table-hover" cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<th>Funcionário</th>
							<th>Cargo</th>
							<th>Especialidade</th>
							<th>Turno</th>
							<th>Telefone</th>
							<th>Admissão</th>
							<th>Situação</th>
							<th>Serviço atual</th>
							<c:if test="${boss}">
								<th>Editar</th>
								<th>Excluir</th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="e" items="${employees}">
							<tr>
								<td><strong><c:out value="${e.user.name}" /></strong><br><small class="text-muted"><c:out value="${e.user.roleLabel}" /></small></td>
								<td><c:out value="${e.jobTitle}" /></td>
								<td>
									<c:choose>
										<c:when test="${e.specialty eq 'LAVAGEM'}"><span class="chip chip-wash">Lavagem</span></c:when>
										<c:when test="${e.specialty eq 'POLIMENTO'}"><span class="chip chip-polish">Polimento</span></c:when>
										<c:when test="${e.specialty eq 'VITRIFICACAO'}"><span class="chip chip-coat">Vitrificação</span></c:when>
										<c:when test="${e.specialty eq 'HIGIENIZACAO'}"><span class="chip chip-clean">Higienização</span></c:when>
										<c:otherwise><span class="chip">Geral</span></c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${e.shift eq 'MANHA'}">Manhã</c:when>
										<c:when test="${e.shift eq 'TARDE'}">Tarde</c:when>
										<c:otherwise>Integral</c:otherwise>
									</c:choose>
								</td>
								<td><c:out value="${e.phone}" /></td>
								<td><fmt:formatDate value="${e.hireDate}" pattern="dd/MM/yyyy" /></td>
								<td>
									<c:choose>
										<c:when test="${e.availability eq 'EM_SERVICO'}"><span class="chip chip-progress">Em serviço</span></c:when>
										<c:when test="${e.availability eq 'DISPONIVEL'}"><span class="chip chip-yes">Disponível</span></c:when>
										<c:otherwise><span class="chip chip-no">Inativo</span></c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${not empty e.currentService}">
											<strong>
												<c:choose>
													<c:when test="${e.currentService.type eq 'LAVAGEM'}">Lavagem</c:when>
													<c:when test="${e.currentService.type eq 'POLIMENTO'}">Polimento</c:when>
													<c:when test="${e.currentService.type eq 'VITRIFICACAO'}">Vitrificação</c:when>
													<c:otherwise>Higienização</c:otherwise>
												</c:choose>
											</strong>
											<br><small class="text-muted"><c:out value="${e.currentService.vehicle.model}" /> &middot; <c:out value="${e.currentService.vehicle.user.name}" /></small>
										</c:when>
										<c:otherwise><span class="muted-dash">Nenhum</span></c:otherwise>
									</c:choose>
								</td>
								
								<c:if test="${boss}">
									<td class="actions">
										<a class="btn btn-info btn-xs" 
										   href="${pageContext.request.contextPath}/employee/update?employeeId=${e.id}">
										   <span class="glyphicon glyphicon-edit"></span>
										</a>
									</td>
									<td class="actions">
										<a class="btn btn-danger btn-xs modal-remove"
										   employee-id="${e.id}" 
										   employee-name="<c:out value="${e.user.name}" />" data-toggle="modal" 
										   data-target="#delete-modal" href="#"><span 
										   class="glyphicon glyphicon-trash"></span></a>
									</td>
								</c:if>
							</tr>
						</c:forEach>
						<c:if test="${empty employees}">
							<tr><td colspan="10" class="text-center text-muted">Nenhum funcionário cadastrado.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
		
		<div id="bottom" class="row">
			<div class="col-md-12">
				<c:set var="baseUrl" value="employees" />
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
				var name = $(this).attr('employee-name');
				var id = $(this).attr('employee-id');
				$(".modal-body #hiddenValue").text("'" + name + "' da equipe");
				$("#id").attr("value", id);
				$("#form").attr("action", "${pageContext.request.contextPath}/employee/delete");
			})
		});
	</script>
</body>
</html>
