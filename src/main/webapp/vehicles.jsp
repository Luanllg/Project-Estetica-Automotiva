<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Veículos</title>
</head>
<body>
	<%@include file="modal.html"%>
	<c:set var="activeMenu" value="vehicles" />
	<c:set var="pageTitle" value="Veículos" />
	<c:set var="pageSubtitle" value="Os carros dos clientes, prontos para o tratamento." />
	<c:set var="pageIcon" value="glyphicon-road" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<div id="alert" style="${not empty message ? 'display: block;' : 'display: none;'}" class="alert alert-dismissable ${alertType eq 1 ? 'alert-success' : 'alert-danger'}">
		  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
		  ${message}
		</div>
		
		<div id="top" class="row">
			<div class="col-md-9">
				<div class="input-group h2">
					<input name="data[search]" class="form-control" id="search" type="text" placeholder="Filtrar nesta página...">
					<span class="input-group-btn">
						<button class="btn btn-danger" type="submit">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</div>
			
			<div class="col-md-3">
				<a href="${pageContext.request.contextPath}/vehicle/form" class="btn btn-danger pull-right h2"><span class="glyphicon glyphicon-plus"></span>&nbsp;Adicionar Veículo</a>
			</div>
		</div>
		
		<hr />
		
		<div id="list" class="row">
			<div class="table-responsive col-md-12">
				<table class="table table-striped table-hover" cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<th>Modelo</th>
							<th>Ano</th>
							<th>Cor</th>
							<th>Categoria</th>
							<th>Vitrificação</th>
							<th>Proprietário</th>
							<th>Editar</th>
							<th>Excluir</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="v" items="${vehicles}">
							<tr>
								<td>${v.model}</td>
								<td>${v.modelYear}</td>
								<td>
									<%-- bolinha com a cor escolhida no input type="color" --%>
									<span class="color-dot" style="background:${v.color};"></span>${v.color}
								</td>
								<td>
									<span class="chip">
										<c:choose>
											<c:when test="${v.category eq 'HATCH'}">Hatch</c:when>
											<c:when test="${v.category eq 'SEDAN'}">Sedan</c:when>
											<c:when test="${v.category eq 'SUV'}">SUV</c:when>
											<c:when test="${v.category eq 'PICKUP'}">Picape</c:when>
											<c:otherwise>${v.category}</c:otherwise>
										</c:choose>
									</span>
								</td>
								<td>
									<span class="chip ${v.ceramicCoating ? 'chip-yes' : 'chip-no'}">${v.ceramicCoating ? 'Vitrificado' : 'Sem vitrificação'}</span>
								</td>
								<td>${v.user.name}</td>
								
								<td class="actions">
									<a class="btn btn-info btn-xs" 
									   href="${pageContext.request.contextPath}/vehicle/update?vehicleId=${v.id}">
									   <span class="glyphicon glyphicon-edit"></span>
									</a>
								</td>
								
								<td class="actions">
									<a class="btn btn-danger btn-xs modal-remove"
									   vehicle-id="${v.id}" 
									   vehicle-name="${v.model}" data-toggle="modal" 
									   data-target="#delete-modal" href="#"><span 
									   class="glyphicon glyphicon-trash"></span></a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		
		<div id="bottom" class="row">
			<div class="col-md-12">
				<c:set var="baseUrl" value="vehicles" />
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
				var vehicleName = $(this).attr('vehicle-name');
				var vehicleId = $(this).attr('vehicle-id');
				$(".modal-body #hiddenValue").text("o veículo '" + vehicleName + "'");
				$("#id").attr("value", vehicleId);
				$("#form").attr("action", "${pageContext.request.contextPath}/vehicle/delete");
			})
		});
	</script>
</body>
</html>
