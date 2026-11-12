<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<%@include file="base-head.jsp"%>
		<title>Estética Automotiva - Usuários</title>
	</head>
	<body>
		<%@include file="modal.html"%>
		<c:set var="activeMenu" value="users" />
		<c:set var="pageTitle" value="Usuários" />
		<c:set var="pageSubtitle" value="Cadastre contas e defina o perfil de cada uma: chefe, funcionário ou cliente." />
		<c:set var="pageIcon" value="glyphicon-user" />
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
			        <a href="${pageContext.request.contextPath}/user/form" class="btn btn-danger pull-right h2"><span class="glyphicon glyphicon-plus"></span>&nbsp;Adicionar Usuário</a>
			    </div>
	     	</div>
	 
	     	<hr />
	     	
	     	<div id="list" class="row">
	     		<div class="table-responsive col-md-12">
			        <table class="table table-striped table-hover" cellspacing="0" cellpadding="0">
			            <thead>
			                <tr>
			                    <th>Nome</th>
			                    <th>Sexo</th>
			                    <th>E-mail</th>
											<th>Perfil</th>
			                    <th>Editar</th>
			                    <th>Excluir</th>
			                 </tr>
			            </thead>
			            <tbody>
			            	<c:forEach var="user" items="${users}">
								<tr>
				                    <td>${user.getName()}</td>
				                    <td>${user.getGender() eq 'M' ? 'Masculino' : 'Feminino'}</td>
				                    <td>${user.getEmail()}</td>
				                    <td>
				                    	<span class="chip ${user.boss ? 'chip-coat' : (user.staff ? 'chip-wash' : '')}">${user.roleLabel}</span>
				                    </td>				                    
				                    <td class="actions">
				                        <a class="btn btn-info btn-xs" 
				                           href="${pageContext.request.contextPath}/user/update?userId=${user.getId()}" >
				                           <span class="glyphicon glyphicon-edit"></span>
				                        </a>
				                    </td>
				                    <td class="actions">
				                        <a class="btn btn-danger btn-xs modal-remove"
				                           user-id="${user.getId()}" 
				                           user-name="${user.getName()}" data-toggle="modal" 
				                           data-target="#delete-modal"  href="#"><span 
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
			        <c:set var="baseUrl" value="users" />
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
			    
			    // ao clicar no delete de algum post, pega o nome do usuário, 
			    // o id do usuário e a ação (delete) e envia para o modal 
			    $(".modal-remove").click(function () {
		            var userName = $(this).attr('user-name');
		            var userId = $(this).attr('user-id');
		            $(".modal-body #hiddenValue").text("o usuário '"+userName+"'");
		            $("#id").attr( "value", userId);
		            $("#form").attr( "action","${pageContext.request.contextPath}/user/delete");
		        })
			});
		</script>
	</body>
</html>
