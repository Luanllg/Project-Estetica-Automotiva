<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%--
	Menu superior + faixa de título da página.
	Cada página define, ANTES de incluir este arquivo, as variáveis:
	  activeMenu   -> item destacado no menu (users, vehicles, services, employees, feedbacks)
	  pageTitle    -> título grande da faixa
	  pageSubtitle -> frase abaixo do título
	  pageIcon     -> classe do glyphicon exibido no círculo vermelho
	(a página que inclui precisa declarar a taglib c)
--%>
<nav id="menu" class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="${pageContext.request.contextPath}">
				<img class="brand-logo" alt="Logo" src="${pageContext.request.contextPath}/images/logo.png"><span class="brand-text">Estética<b>Automotiva</b><small>Gestão de veículos e serviços</small></span>
			</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<%-- Os itens do menu dependem do perfil (loggedUser.role): CHEFE, FUNCIONARIO ou CLIENTE --%>
				<li class="${activeMenu eq 'services' ? 'active' : ''}"><a href="${pageContext.request.contextPath}/services"><span class="glyphicon glyphicon-wrench"></span>&nbsp;${sessionScope.loggedUser.client ? 'Meus Serviços' : 'Serviços'}</a></li>
				<c:if test="${sessionScope.loggedUser.staff}">
					<li class="${activeMenu eq 'vehicles' ? 'active' : ''}"><a href="${pageContext.request.contextPath}/vehicles"><span class="glyphicon glyphicon-road"></span>&nbsp;Veículos</a></li>
					<li class="${activeMenu eq 'employees' ? 'active' : ''}"><a href="${pageContext.request.contextPath}/employees"><span class="glyphicon glyphicon-briefcase"></span>&nbsp;Equipe</a></li>
				</c:if>
				<li class="${activeMenu eq 'feedbacks' ? 'active' : ''}"><a href="${pageContext.request.contextPath}/feedbacks"><span class="glyphicon glyphicon-star"></span>&nbsp;${sessionScope.loggedUser.client ? 'Meus Feedbacks' : 'Feedbacks'}</a></li>
				<c:if test="${sessionScope.loggedUser.boss}">
					<li class="${activeMenu eq 'users' ? 'active' : ''}"><a href="${pageContext.request.contextPath}/users"><span class="glyphicon glyphicon-user"></span>&nbsp;Usuários</a></li>
				</c:if>
				<li class="dropdown">
					<a class="dropdown-toggle" data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;<c:out value="${not empty sessionScope.loggedUser ? sessionScope.loggedUser.name : 'Perfil'}" />
					<span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li class="dropdown-header">Perfil: ${sessionScope.loggedUser.roleLabel}</li>
						<li><a href="${pageContext.request.contextPath}/logout"><span class="glyphicon glyphicon-log-out"></span>&nbsp;Sair</a></li>
					</ul>
				</li>
			</ul>
		</div>
	</div>
</nav>

<c:if test="${not empty pageTitle}">
	<div class="page-hero">
		<div class="hero-icon"><span class="glyphicon ${pageIcon}"></span></div>
		<div class="hero-text">
			<span class="hero-kicker">${sessionScope.loggedUser.client ? 'Área do cliente' : 'Painel de gestão'}</span>
			<h1 class="hero-title">${pageTitle}</h1>
			<p class="hero-sub">${pageSubtitle}</p>
		</div>
	</div>
</c:if>
