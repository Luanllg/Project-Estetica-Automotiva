<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<%@include file="base-head.jsp"%>
		<title>Estética Automotiva - Usuário</title>
	</head>
	<body>
		<c:set var="activeMenu" value="users" />
		<c:set var="pageTitle" value="Usuários" />
		<c:set var="pageSubtitle" value="Dados do usuário e acesso ao sistema." />
		<c:set var="pageIcon" value="glyphicon-user" />
		<%@include file="nav-menu.jsp"%>
			
		<div id="container" class="container-fluid">
			<h3 class="page-header">${not empty user ? 'Atualizar' : 'Adicionar'} Usuário</h3>

			<form action="${pageContext.request.contextPath}/user/${action}" method="POST">
				<input type="hidden" value="${user.getId()}" name="userId">
				<div class="row">
					<div class="form-group col-md-4">
					<label for="name">Nome</label>
						<input type="text" class="form-control" id="name" name="name" 
							   autofocus="autofocus" placeholder="Nome do usuário" 
							   required oninvalid="this.setCustomValidity('Por favor, informe o nome do usuário.')"
							   oninput="setCustomValidity('')"
							   value="${user.getName()}">
					</div>

					<div class="form-group col-md-4">
						<label for="gender">Sexo</label>
						<select id="gender" class="form-control selectpicker" name="gender" 
							    required oninvalid="this.setCustomValidity('Por favor, informe o sexo')"
							    oninput="setCustomValidity('')">
					      <option value="" disabled ${not empty user ? "" : "selected"}>Selecione o sexo</option>
						  <option value="F" ${user.getGender() == 'F' ? "selected" : ""}>Feminino</option>
						  <option value="M" ${user.getGender() == 'M' ? "selected" : ""}>Masculino</option>
						</select>
					</div>
					
					<div class="form-group col-md-4">
					<label for="mail">E-mail</label>
						<input type="email" class="form-control" id="mail" name="mail" 
							   autofocus="autofocus" placeholder="E-mail do usuário" 
							   required oninvalid="this.setCustomValidity('Por favor, informe o email do usuário.')"
							   oninput="setCustomValidity('')"
							   value="${user.getEmail()}">
					</div>
				</div>
				<%-- Perfil: quem define é o chefe. Ele não pode mudar o próprio perfil (o controller também garante). --%>
				<div class="row">
					<div class="form-group col-md-8">
						<c:set var="ownAccount" value="${not empty user and user.id == sessionScope.loggedUser.id}" />
						<label>Perfil de acesso</label><br />
						<label class="radio-inline">
							<input type="radio" name="role" value="CLIENTE" required ${ownAccount ? 'disabled' : ''}
								   ${empty user or user.role eq 'CLIENTE' ? 'checked' : ''}> Cliente
						</label>
						<label class="radio-inline">
							<input type="radio" name="role" value="FUNCIONARIO" ${ownAccount ? 'disabled' : ''}
								   ${user.role eq 'FUNCIONARIO' ? 'checked' : ''}> Funcionário
						</label>
						<label class="radio-inline">
							<input type="radio" name="role" value="CHEFE" ${ownAccount ? 'disabled' : ''}
								   ${user.role eq 'CHEFE' ? 'checked' : ''}> Chefe
						</label>
						<c:if test="${ownAccount}"><span class="help-block">Você não pode alterar o seu próprio perfil.</span></c:if>
						<c:if test="${not ownAccount}"><span class="help-block">Cliente: acompanha seus serviços e envia feedback. Funcionário: gerencia veículos e serviços. Chefe: acesso total.</span></c:if>
					</div>
				</div>
				<%-- A senha só é pedida no cadastro; ela vira hash SHA-256 no controller --%>
				<c:if test="${action eq 'insert'}">
					<div class="row">
						<div class="form-group col-md-4">
							<label for="password">Senha</label>
							<input type="password" class="form-control" id="password" name="password" 
								   minlength="6" placeholder="Mínimo de 6 caracteres" 
								   required oninvalid="this.setCustomValidity('Por favor, informe uma senha de pelo menos 6 caracteres.')"
								   oninput="setCustomValidity('')">
						</div>
					</div>
				</c:if>
				<hr />
				<div id="actions" class="row pull-right">
					<div class="col-md-12">
						<a href="${pageContext.request.contextPath}/users" class="btn btn-default">Cancelar</a>
						<button type="submit" class="btn btn-primary">${not empty user ? "Alterar Usuário" : "Cadastrar Usuário"}</button>
					</div>
				</div>
			</form>
		</div>

		<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	</body>
</html>
