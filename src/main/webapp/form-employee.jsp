<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Funcionário</title>
</head>
<body>
	<c:set var="activeMenu" value="employees" />
	<c:set var="pageTitle" value="Equipe" />
	<c:set var="pageSubtitle" value="Dados do funcionário na oficina." />
	<c:set var="pageIcon" value="glyphicon-briefcase" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<h3 class="page-header">${not empty employee ? 'Atualizar' : 'Adicionar'} Funcionário</h3>
		
		<form action="${pageContext.request.contextPath}/employee/${action}" method="POST">
			
			<input type="hidden" value="${employee.id}" name="employeeId">
			
			<div class="row">
				<%-- 1) SELECT da chave estrangeira: só usuários com perfil Chefe ou Funcionário --%>
				<div class="form-group col-md-6">
					<label for="user">Usuário</label>
					<select id="user" class="form-control" name="user" autofocus="autofocus"
							required oninvalid="this.setCustomValidity('Por favor, informe o usuário.')"
							oninput="setCustomValidity('')">
						<option value="" ${not empty employee ? '' : 'selected'}>Selecione um usuário da equipe</option>
						<c:forEach var="u" items="${users}">
							<option value="${u.id}" ${employee.user.id == u.id ? 'selected' : ''}>
								<c:out value="${u.name}" /> (${u.roleLabel})
							</option>
						</c:forEach>
					</select>
					<span class="help-block">Não achou? O chefe cadastra o usuário em "Usuários" com perfil Funcionário.</span>
				</div>
				
				<%-- 2) input TEXT --%>
				<div class="form-group col-md-6">
					<label for="jobTitle">Cargo</label>
					<input type="text" class="form-control" id="jobTitle" name="jobTitle" 
						   placeholder="Ex.: Detalhador" maxlength="80"
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe o cargo.')"
						   oninput="setCustomValidity('')"
						   value="<c:out value="${employee.jobTitle}" />" />
				</div>
			</div>
			
			<div class="row">
				<%-- 3) SELECT de especialidade --%>
				<div class="form-group col-md-4">
					<label for="specialty">Especialidade</label>
					<select id="specialty" class="form-control" name="specialty" required
							oninvalid="this.setCustomValidity('Por favor, informe a especialidade.')"
							oninput="setCustomValidity('')">
						<option value="" ${not empty employee ? '' : 'selected'}>Selecione</option>
						<option value="LAVAGEM" ${employee.specialty eq 'LAVAGEM' ? 'selected' : ''}>Lavagem</option>
						<option value="POLIMENTO" ${employee.specialty eq 'POLIMENTO' ? 'selected' : ''}>Polimento</option>
						<option value="VITRIFICACAO" ${employee.specialty eq 'VITRIFICACAO' ? 'selected' : ''}>Vitrificação</option>
						<option value="HIGIENIZACAO" ${employee.specialty eq 'HIGIENIZACAO' ? 'selected' : ''}>Higienização</option>
						<option value="GERAL" ${employee.specialty eq 'GERAL' ? 'selected' : ''}>Geral</option>
					</select>
				</div>
				
				<%-- 4) input TEL --%>
				<div class="form-group col-md-4">
					<label for="phone">Telefone</label>
					<input type="tel" class="form-control" id="phone" name="phone" 
						   placeholder="(35) 99999-9999" maxlength="20"
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe o telefone.')"
						   oninput="setCustomValidity('')"
						   value="<c:out value="${employee.phone}" />" />
				</div>
				
				<%-- 5) input DATE --%>
				<div class="form-group col-md-4">
					<label for="hireDate">Data de admissão</label>
					<input type="date" class="form-control" id="hireDate" name="hireDate" 
						   required 
						   oninvalid="this.setCustomValidity('Por favor, informe a data de admissão.')"
						   oninput="setCustomValidity('')"
						   value="${employee.hireDate}" />
				</div>
			</div>
			
			<div class="row">
				<%-- 6) input RADIO --%>
				<div class="form-group col-md-6">
					<label>Turno</label><br />
					<label class="radio-inline">
						<input type="radio" name="shift" value="MANHA" required
							   ${employee.shift eq 'MANHA' ? 'checked' : ''}> Manhã
					</label>
					<label class="radio-inline">
						<input type="radio" name="shift" value="TARDE" 
							   ${employee.shift eq 'TARDE' ? 'checked' : ''}> Tarde
					</label>
					<label class="radio-inline">
						<input type="radio" name="shift" value="INTEGRAL" 
							   ${employee.shift eq 'INTEGRAL' ? 'checked' : ''}> Integral
					</label>
				</div>
				
				<%-- 7) input CHECKBOX: novo funcionário já nasce ativo --%>
				<div class="form-group col-md-6">
					<label>Situação</label>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="active" value="true"
								   ${empty employee or employee.active ? 'checked' : ''}> Ativo (pode receber serviços)
						</label>
					</div>
				</div>
			</div>
			
			<hr />
			<div id="actions" class="row pull-right">
				<div class="col-md-12">
					<a href="${pageContext.request.contextPath}/employees" class="btn btn-default">Cancelar</a>
					<button type="submit" class="btn btn-primary">${not empty employee ? 'Atualizar' : 'Cadastrar'} Funcionário</button>
				</div>
			</div>
		</form>
	</div>
	
	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>
