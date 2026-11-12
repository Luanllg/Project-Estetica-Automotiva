<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br" class="login-html">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Login</title>
</head>
<body class="login-page">
	<%-- A tela de login não inclui o nav-menu: ainda não há usuário logado --%>
	<div class="login-wrap">
		<div class="login-brand">
			<img alt="Logo" src="${pageContext.request.contextPath}/images/logo.png">
			<h1>Estética <b>Automotiva</b></h1>
			<span>Gestão de veículos e serviços</span>
		</div>
		
		<div class="login-card">
			<h3>Acesse sua conta</h3>
			
			<c:if test="${not empty error}">
				<div class="alert alert-danger">${error}</div>
			</c:if>
			
			<form action="${pageContext.request.contextPath}/login" method="POST">
				<div class="form-group">
					<label for="email">E-mail</label>
					<input type="email" class="form-control" id="email" name="email" 
						   autofocus="autofocus" placeholder="seu@email.com" required
						   value="${email}" />
				</div>
				
				<div class="form-group">
					<label for="password">Senha</label>
					<input type="password" class="form-control" id="password" name="password" 
						   placeholder="Sua senha" required />
				</div>
				
				<button type="submit" class="btn btn-primary btn-block">Entrar</button>
			</form>
		</div>
		
		<p class="login-foot">LAVAGEM &middot; POLIMENTO &middot; VITRIFICAÇÃO &middot; HIGIENIZAÇÃO</p>
	</div>
</body>
</html>
