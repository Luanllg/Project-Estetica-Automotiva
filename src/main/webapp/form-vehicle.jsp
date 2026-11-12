<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Veículo</title>
</head>
<body>
	<c:set var="activeMenu" value="vehicles" />
	<c:set var="pageTitle" value="Veículos" />
	<c:set var="pageSubtitle" value="Preencha os dados do veículo do cliente." />
	<c:set var="pageIcon" value="glyphicon-road" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<h3 class="page-header">${not empty vehicle ? 'Atualizar' : 'Adicionar'} Veículo</h3>
		
		<%-- ${action} vale "insert" ou "update" (definido pelo controller) --%>
		<form action="${pageContext.request.contextPath}/vehicle/${action}" method="POST">
			
			<input type="hidden" value="${vehicle.id}" name="vehicleId">
			
			<div class="row">
				<%-- 1) input TEXT --%>
				<div class="form-group col-md-6">
					<label for="model">Modelo</label>
					<input type="text" class="form-control" id="model" name="model" 
						   autofocus="autofocus" placeholder="Ex.: Honda Civic" maxlength="100"
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe o modelo do veículo.')"
						   oninput="setCustomValidity('')"
						   value="${vehicle.model}" />
				</div>
				
				<%-- 2) input NUMBER --%>
				<div class="form-group col-md-3">
					<label for="modelYear">Ano</label>
					<input type="number" class="form-control" id="modelYear" name="modelYear" 
						   min="1950" max="2035" placeholder="Ex.: 2020"
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe um ano entre 1950 e 2035.')"
						   oninput="setCustomValidity('')"
						   value="${vehicle.modelYear > 0 ? vehicle.modelYear : ''}" />
				</div>
				
				<%-- 3) input COLOR (grava #RRGGBB) --%>
				<div class="form-group col-md-3">
					<label for="color">Cor</label>
					<input type="color" class="form-control" id="color" name="color" 
						   value="${not empty vehicle ? vehicle.color : '#000000'}" />
				</div>
			</div>
			
			<div class="row">
				<%-- 4) input RADIO: o "checked" deixa marcado o valor salvo (na edição) --%>
				<div class="form-group col-md-4">
					<label>Categoria</label><br />
					<label class="radio-inline">
						<input type="radio" name="category" value="HATCH" required
							   ${vehicle.category eq 'HATCH' ? 'checked' : ''}> Hatch
					</label>
					<label class="radio-inline">
						<input type="radio" name="category" value="SEDAN" 
							   ${vehicle.category eq 'SEDAN' ? 'checked' : ''}> Sedan
					</label>
					<label class="radio-inline">
						<input type="radio" name="category" value="SUV" 
							   ${vehicle.category eq 'SUV' ? 'checked' : ''}> SUV
					</label>
					<label class="radio-inline">
						<input type="radio" name="category" value="PICKUP" 
							   ${vehicle.category eq 'PICKUP' ? 'checked' : ''}> Picape
					</label>
				</div>
				
				<%-- 5) input CHECKBOX --%>
				<div class="form-group col-md-4">
					<label>Proteção</label>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="ceramicCoating" value="true"
								   ${vehicle.ceramicCoating ? 'checked' : ''}> Possui vitrificação
						</label>
					</div>
				</div>
				
				<%-- 6) SELECT da chave estrangeira: as opções vêm do banco (nada escrito à mão) --%>
				<div class="form-group col-md-4">
					<label for="user">Proprietário</label>
					<select id="user" class="form-control" name="user" 
							required oninvalid="this.setCustomValidity('Por favor, informe o proprietário.')"
							oninput="setCustomValidity('')">
						<option value="" ${not empty vehicle ? '' : 'selected'}>Selecione um proprietário</option>
						
						<c:forEach var="u" items="${users}">
							<option value="${u.id}" ${vehicle.user.id == u.id ? 'selected' : ''}>
								${u.name}
							</option>
						</c:forEach>
					</select>
				</div>
			</div>
			
			<hr />
			<div id="actions" class="row pull-right">
				<div class="col-md-12">
					<a href="${pageContext.request.contextPath}/vehicles" class="btn btn-default">Cancelar</a>
					<button type="submit" class="btn btn-primary">${not empty vehicle ? 'Atualizar' : 'Cadastrar'} Veículo</button>
				</div>
			</div>
		</form>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>
