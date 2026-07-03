document.addEventListener("DOMContentLoaded", function () {
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

    function renderHistorico(items) {
        lista.innerHTML = "";

        if (!items || items.length === 0) {
            msg.textContent = "Não existem agendamentos no histórico.";
            return;
        }

        msg.textContent = "";

        items.forEach(function (a) {
            const card = document.createElement("div");
            card.className = "content-card mb-15"; // Usando nossas classes utilitárias

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
                        <span class="status-badge ${classeStatus}">${a.status || "-"}</span>
                    </div>
                </div>
            `;

            if (a.status === "PENDENTE") {
                const actions = document.createElement("div");
                actions.className = "flex gap10 mt-20";

                const btnAceitar = document.createElement("button");
                btnAceitar.textContent = "Aceitar";
                btnAceitar.onclick = () => decidirAgendamento(a.id, "ACEITAR");

                const btnRecusar = document.createElement("button");
                btnRecusar.className = "btn-secondary";
                btnRecusar.textContent = "Recusar";
                btnRecusar.onclick = () => decidirAgendamento(a.id, "RECUSAR");

                actions.appendChild(btnAceitar);
                actions.appendChild(btnRecusar);
                card.appendChild(actions);
            }

            lista.appendChild(card);
        });
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

    carregarHistorico();
});