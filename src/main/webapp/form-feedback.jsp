<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<%@include file="base-head.jsp"%>
	<title>Estética Automotiva - Feedback</title>
</head>
<body>
	<fmt:setLocale value="pt_BR" />
	<c:set var="activeMenu" value="feedbacks" />
	<c:set var="pageTitle" value="Meus Feedbacks" />
	<c:set var="pageSubtitle" value="Avalie o serviço feito no seu veículo." />
	<c:set var="pageIcon" value="glyphicon-star" />
	<%@include file="nav-menu.jsp"%>
	
	<div id="container" class="container-fluid">
		<h3 class="page-header">${not empty feedback ? 'Atualizar' : 'Enviar'} Feedback</h3>

		<form action="${pageContext.request.contextPath}/feedback/${action}" method="POST">
			<input type="hidden" value="${feedback.id}" name="feedbackId">
			
			<div class="row">
				<div class="form-group col-md-8">
					<label for="serviceId">Carro e serviço</label>
					<c:choose>
						<%-- Edição: o serviço avaliado não muda, só a nota e o comentário --%>
						<c:when test="${not empty feedback}">
							<p class="form-control-static">
								<strong><c:out value="${feedback.service.vehicle.model}" /> ${feedback.service.vehicle.modelYear}</strong>
								&mdash; ${feedback.service.type}
								(<fmt:formatDate value="${feedback.service.serviceDate}" pattern="dd/MM/yyyy" />)
							</p>
							<input type="hidden" name="serviceId" value="${feedback.service.id}">
						</c:when>
						<%-- Novo: só aparecem serviços do cliente, já concluídos e ainda sem feedback --%>
						<c:otherwise>
							<select id="serviceId" class="form-control" name="serviceId" autofocus="autofocus"
									required oninvalid="this.setCustomValidity('Por favor, escolha o serviço.')"
									oninput="setCustomValidity('')">
								<option value="" ${empty selectedServiceId ? 'selected' : ''}>Selecione o serviço</option>
								<c:forEach var="s" items="${services}">
									<option value="${s.id}" ${selectedServiceId eq s.id ? 'selected' : ''}>
										<c:out value="${s.vehicle.model}" /> ${s.vehicle.modelYear} &mdash; ${s.type}
										(<fmt:formatDate value="${s.serviceDate}" pattern="dd/MM/yyyy" />)
									</option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			
			<div class="row">
				<%-- input RADIO com as avaliações pré-definidas --%>
				<div class="form-group col-md-12">
					<label>Avaliação</label><br />
					<label class="radio-inline">
						<input type="radio" name="rating" value="RUIM" required
							   ${feedback.rating eq 'RUIM' ? 'checked' : ''}> Ruim
					</label>
					<label class="radio-inline">
						<input type="radio" name="rating" value="REGULAR" 
							   ${feedback.rating eq 'REGULAR' ? 'checked' : ''}> Regular
					</label>
					<label class="radio-inline">
						<input type="radio" name="rating" value="BOM" 
							   ${feedback.rating eq 'BOM' ? 'checked' : ''}> Bom
					</label>
					<label class="radio-inline">
						<input type="radio" name="rating" value="OTIMO" 
							   ${feedback.rating eq 'OTIMO' ? 'checked' : ''}> Ótimo
					</label>
					<label class="radio-inline">
						<input type="radio" name="rating" value="EXCELENTE" 
							   ${feedback.rating eq 'EXCELENTE' ? 'checked' : ''}> Excelente
					</label>
				</div>
			</div>
			
			<div class="row">
				<div class="form-group col-md-12">
					<label for="comment">Comentário</label>
					<textarea class="form-control" id="comment" name="comment" rows="4" maxlength="1000"
							  placeholder="Conte como ficou o carro, o atendimento..."><c:out value="${feedback.comment}" /></textarea>
				</div>
			</div>
			
			<hr />
			<div id="actions" class="row pull-right">
				<div class="col-md-12">
					<a href="${pageContext.request.contextPath}/feedbacks" class="btn btn-default">Cancelar</a>
					<button type="submit" class="btn btn-primary">${not empty feedback ? 'Alterar Feedback' : 'Enviar Feedback'}</button>
				</div>
			</div>
		</form>
	</div>

	<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>
