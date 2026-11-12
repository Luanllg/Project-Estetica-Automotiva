<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Serviço</title>
</head>
<body>
	<c:set var="activeMenu" value="services" />
	<c:set var="pageTitle" value="Serviços" />
	<c:set var="pageSubtitle" value="Registre o serviço, o responsável e o andamento." />
	<c:set var="pageIcon" value="glyphicon-wrench" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<h3 class="page-header">${not empty service ? 'Atualizar' : 'Adicionar'} Serviço</h3>
		
		<form action="${pageContext.request.contextPath}/service/${action}" method="POST">
			
			<input type="hidden" value="${service.id}" name="serviceId">
			
			<div class="row">
				<%-- 1) SELECT da chave estrangeira (veículo), preenchido pelo banco --%>
				<div class="form-group col-md-6">
					<label for="vehicle">Veículo</label>
					<select id="vehicle" class="form-control" name="vehicle" autofocus="autofocus"
							required oninvalid="this.setCustomValidity('Por favor, informe o veículo.')"
							oninput="setCustomValidity('')">
						<option value="" ${not empty service ? '' : 'selected'}>Selecione um veículo</option>
						
						<c:forEach var="v" items="${vehicles}">
							<option value="${v.id}" ${service.vehicle.id == v.id ? 'selected' : ''}>
								<c:out value="${v.model}" /> (<c:out value="${v.user.name}" />)
							</option>
						</c:forEach>
					</select>
				</div>
				
				<%-- 2) input DATE --%>
				<div class="form-group col-md-3">
					<label for="serviceDate">Data do serviço</label>
					<input type="date" class="form-control" id="serviceDate" name="serviceDate" 
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe a data do serviço.')"
						   oninput="setCustomValidity('')"
						   value="${service.serviceDate}" />
				</div>
				
				<%-- 3) input NUMBER com casas decimais --%>
				<div class="form-group col-md-3">
					<label for="price">Valor (R$)</label>
					<input type="number" class="form-control" id="price" name="price" 
						   step="0.01" min="0" placeholder="0,00"
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe o valor do serviço.')"
						   oninput="setCustomValidity('')"
						   value="${not empty service ? service.price : ''}" />
				</div>
			</div>
			
			<div class="row">
				<%-- 4) input RADIO --%>
				<div class="form-group col-md-6">
					<label>Tipo de serviço</label><br />
					<label class="radio-inline">
						<input type="radio" name="type" value="LAVAGEM" required
							   ${service.type eq 'LAVAGEM' ? 'checked' : ''}> Lavagem
					</label>
					<label class="radio-inline">
						<input type="radio" name="type" value="POLIMENTO" 
							   ${service.type eq 'POLIMENTO' ? 'checked' : ''}> Polimento
					</label>
					<label class="radio-inline">
						<input type="radio" name="type" value="VITRIFICACAO" 
							   ${service.type eq 'VITRIFICACAO' ? 'checked' : ''}> Vitrificação
					</label>
					<label class="radio-inline">
						<input type="radio" name="type" value="HIGIENIZACAO" 
							   ${service.type eq 'HIGIENIZACAO' ? 'checked' : ''}> Higienização
					</label>
				</div>
				
				<%-- 5) SELECT de status: é o que o cliente enxerga em "Meus Serviços" --%>
				<div class="form-group col-md-3">
					<label for="status">Status</label>
					<select id="status" class="form-control" name="status" required>
						<option value="AGENDADO" ${service.status eq 'AGENDADO' || empty service ? 'selected' : ''}>Agendado</option>
						<option value="EM_ANDAMENTO" ${service.status eq 'EM_ANDAMENTO' ? 'selected' : ''}>Em andamento</option>
						<option value="CONCLUIDO" ${service.status eq 'CONCLUIDO' ? 'selected' : ''}>Concluído</option>
					</select>
				</div>
				
				<%-- 6) SELECT da chave estrangeira (funcionário responsável) --%>
				<div class="form-group col-md-3">
					<label for="employee">Funcionário responsável</label>
					<select id="employee" class="form-control" name="employee">
						<option value="">Ainda sem responsável</option>
						
						<c:forEach var="e" items="${employees}">
							<%-- funcionário inativo não aparece para novas atribuições --%>
							<option value="${e.id}" 
								${service.employee.id == e.id ? 'selected' : ''}
								${not e.active and service.employee.id != e.id ? 'disabled' : ''}>
								<c:out value="${e.user.name}" />${e.active ? '' : ' (inativo)'}
							</option>
						</c:forEach>
					</select>
					<span class="help-block">Obrigatório para "Em andamento". Cada funcionário executa um serviço por vez.</span>
				</div>
			</div>
			
			<div class="row">
				<%-- 7) input CHECKBOX --%>
				<div class="form-group col-md-4">
					<label>Pagamento</label>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="paid" value="true"
								   ${service.paid ? 'checked' : ''}> Serviço pago
						</label>
					</div>
				</div>
			</div>
			
			<div class="row">
				<%-- 8) TEXTAREA (sem espaços entre as tags, para não entrar espaço no texto) --%>
				<div class="form-group col-md-12">
					<label for="details">Detalhes</label>
					<textarea class="form-control" id="details" name="details" rows="3" 
							  placeholder="Produtos usados, observações, garantia..."><c:out value="${service.details}" /></textarea>
				</div>
			</div>
			
			<hr />
			<div id="actions" class="row pull-right">
				<div class="col-md-12">
					<a href="${pageContext.request.contextPath}/services" class="btn btn-default">Cancelar</a>
					<button type="submit" class="btn btn-primary">${not empty service ? 'Atualizar' : 'Cadastrar'} Serviço</button>
				</div>
			</div>
		</form>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>
