// Comportamentos comuns das listagens.
// Filtro rápido: esconde as linhas da tabela (da página atual) que não contêm o texto digitado.
$(document).ready(function () {
	$("#search").on("keyup", function () {
		var text = $(this).val().toLowerCase();

		$("#list tbody tr").each(function () {
			$(this).toggle($(this).text().toLowerCase().indexOf(text) > -1);
		});
	});
});
