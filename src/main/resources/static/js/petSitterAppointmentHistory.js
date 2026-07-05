document.addEventListener("DOMContentLoaded", function () {
    const itensPorPagina = 6;
    let paginaAtual = 1;
    let agendamentosCompletos = [];

    const paginacao = document.getElementById("paginacao");
    const btnAnterior = document.getElementById("btnPaginaAnterior");
    const btnProxima = document.getElementById("btnProximaPagina");
    const infoPagina = document.getElementById("infoPagina");
    const btnVoltar = document.getElementById("btnVoltar");
    const msg = document.getElementById("historicoMsg");
    const lista = document.getElementById("historicoLista");
    const token = localStorage.getItem("token");

    if (btnVoltar) {
        btnVoltar.addEventListener("click", () => window.location.href = "petSitterHome.html");
    }

    function formatarData(isoDate) {
        if (!isoDate) return "-";
        const parts = String(isoDate).split("-");
        return parts.length === 3 ? `${parts[2]}/${parts[1]}/${parts[0]}` : isoDate;
    }

    function formatarHora(isoTime) {
        return isoTime ? String(isoTime).slice(0, 5) : "-";
    }

    async function decidirAgendamento(id, action) {
        if (!token) {
            msg.textContent = "Sessão expirada. Faça login novamente.";
            return;
        }

        try {
            const status = action === "ACEITAR" ? "ACEITO" : "RECUSADO";
            const response = await fetch(`${BASE_URL}/appointments/${id}/status?status=${status}`, {
                method: "PUT",
                headers: { Authorization: "Bearer " + token }
            });

            if (!response.ok) {
                const err = await response.text();
                msg.textContent = err || "Erro ao atualizar.";
                return;
            }

            msg.textContent = action === "ACEITAR" ? "Solicitação aceita." : "Solicitação recusada.";
            await carregarHistorico();
        } catch (e) {
            msg.textContent = "Erro de conexão.";
        }
    }

    async function iniciarAgendamento(id) {
        if (!token) { msg.textContent = "Sessão expirada. Faça login novamente."; return; }
        try {
            const res = await fetch(`${BASE_URL}/appointments/${id}/start`, {
                method: "PUT",
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) {
                msg.textContent = await res.text();
                return;
            }
            msg.textContent = "Agendamento iniciado.";
            await carregarHistorico();
        } catch (e) {
            msg.textContent = "Erro de conexão.";
        }
    }

    async function finalizarAgendamento(id) {
        if (!token) { msg.textContent = "Sessão expirada. Faça login novamente."; return; }
        try {
            const res = await fetch(`${BASE_URL}/appointments/${id}/finish`, {
                method: "PUT",
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) {
                msg.textContent = await res.text();
                return;
            }
            msg.textContent = "Agendamento finalizado.";
            await carregarHistorico();
        } catch (e) {
            msg.textContent = "Erro de conexão.";
        }
    }

    function atualizarPaginacao() {
        const totalPaginas = Math.max(1, Math.ceil(agendamentosCompletos.length / itensPorPagina));

        if (agendamentosCompletos.length === 0) {
            paginacao?.classList.add("hidden");
            return;
        }

        paginacao?.classList.remove("hidden");
        infoPagina.textContent = `Página ${paginaAtual} de ${totalPaginas}`;
        btnAnterior.disabled = paginaAtual === 1;
        btnProxima.disabled = paginaAtual >= totalPaginas;
    }

    function renderPaginaAtual() {
        const inicio = (paginaAtual - 1) * itensPorPagina;
        const fim = inicio + itensPorPagina;
        const itensPagina = agendamentosCompletos.slice(inicio, fim);

        lista.innerHTML = "";

        if (!itensPagina.length) {
            msg.textContent = "Não existem agendamentos no histórico.";
            return;
        }

        msg.textContent = "";

        itensPagina.forEach(function (a) {
            const card = document.createElement("div");
            card.className = "content-card mb-15";

            const classeStatus = (a.status || "PENDENTE").toLowerCase();

            card.innerHTML = `
            <div class="flex-between">
                <div>
                    <h4 class="mb-5">${a.serviceName || "-"}</h4>
                    <p><strong>Tutor:</strong> ${a.petOwnerName || "-"}</p>
                    <p><strong>Pet:</strong> ${a.petName || "-"}</p>
                    <p><strong>Data:</strong> ${formatarData(a.serviceDate)} às ${formatarHora(a.serviceTime)}</p>
                </div>
                <div class="text-right">
                    <span class="status-badge ${classeStatus}">${formatarStatus(a.status)}</span>
                </div>
            </div>
        `;

            const actions = document.createElement("div");
            actions.className = "flex gap10 mt-20";

            if (a.status === "PENDENTE") {
                const btnAceitar = document.createElement("button");
                btnAceitar.textContent = "Aceitar";
                btnAceitar.onclick = () => decidirAgendamento(a.id, "ACEITAR");

                const btnRecusar = document.createElement("button");
                btnRecusar.className = "btn-secondary";
                btnRecusar.textContent = "Recusar";
                btnRecusar.onclick = () => decidirAgendamento(a.id, "RECUSAR");

                actions.appendChild(btnAceitar);
                actions.appendChild(btnRecusar);
            }

            if (a.status === "ACEITO") {
                const btnIniciar = document.createElement("button");
                btnIniciar.textContent = "Iniciar";
                btnIniciar.onclick = () => iniciarAgendamento(a.id);
                actions.appendChild(btnIniciar);
            }

            if (a.status === "EM_ANDAMENTO") {
                const btnFinalizar = document.createElement("button");
                btnFinalizar.textContent = "Finalizar";
                btnFinalizar.onclick = () => finalizarAgendamento(a.id);
                actions.appendChild(btnFinalizar);
            }

            if (actions.children.length > 0) card.appendChild(actions);

            if (a.history) {
                const histDiv = document.createElement("div");
                histDiv.className = "history-box";

                const titulo = document.createElement("div");
                titulo.className = "history-title";
                titulo.textContent = "Histórico";
                histDiv.appendChild(titulo);

                a.history
                    .split("\n")
                    .filter(linha => linha.trim() !== "")
                    .forEach(linha => {
                        const item = document.createElement("div");
                        item.className = "history-item";
                        item.textContent = linha;
                        histDiv.appendChild(item);
                    });

                card.appendChild(histDiv);
            }

            lista.appendChild(card);
        });

        atualizarPaginacao();
    }

    function renderHistorico(items) {
        agendamentosCompletos = Array.isArray(items) ? items : [];
        paginaAtual = 1;

        if (agendamentosCompletos.length === 0) {
            lista.innerHTML = "";
            msg.textContent = "Não existem agendamentos no histórico.";
            paginacao?.classList.add("hidden");
            return;
        }

        renderPaginaAtual();
    }

    function formatarStatus(status) {
        switch (status) {
            case "PENDENTE":
                return "PENDENTE";
            case "ACEITO":
                return "ACEITO";
            case "RECUSADO":
                return "RECUSADO";
            case "EM_ANDAMENTO":
                return "EM ANDAMENTO";
            case "CONCLUIDO":
                return "FINALIZADO";
            default:
                return status || "-";
        }
    }

    async function carregarHistorico() {
        if (!token) return;

        try {
            const response = await fetch(`${BASE_URL}/appointments/petsitter`, {
                headers: { Authorization: "Bearer " + token }
            });

            if (!response.ok) {
                msg.textContent = "Não foi possível carregar o histórico.";
                return;
            }

            const data = await response.json();
            const ordenado = (Array.isArray(data) ? data : []).sort((a, b) =>
                new Date(b.createdAt || 0) - new Date(a.createdAt || 0)
            );

            renderHistorico(ordenado);
        } catch (e) {
            msg.textContent = "Erro de conexão.";
        }
    }
    btnAnterior?.addEventListener("click", () => {
        if (paginaAtual > 1) {
            paginaAtual--;
            renderPaginaAtual();
        }
    });

    btnProxima?.addEventListener("click", () => {
        const totalPaginas = Math.max(1, Math.ceil(agendamentosCompletos.length / itensPorPagina));
        if (paginaAtual < totalPaginas) {
            paginaAtual++;
            renderPaginaAtual();
        }
    });
    carregarHistorico();
});
