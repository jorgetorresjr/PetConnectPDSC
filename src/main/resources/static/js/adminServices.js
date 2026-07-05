function getToken() {
    return localStorage.getItem("token");
}

function getRoleFromToken(token) {
    try {
        return JSON.parse(atob(token.split(".")[1])).role;
    } catch {
        return null;
    }
}

function authHeaders() {
    return { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` };
}

function mostrarFeedback(mensagem, sucesso) {
    const el = document.getElementById("msgFeedback");
    el.textContent = mensagem;
    el.style.display = "block";
    el.style.background = sucesso ? "#dcfce7" : "#fee2e2";
    el.style.color = sucesso ? "#15803d" : "#b91c1c";
    el.style.border = sucesso ? "1px solid #bbf7d0" : "1px solid #fecaca";
    setTimeout(() => { el.style.display = "none"; }, 4000);
}

function renderizarServicos(servicos) {
    const lista = document.getElementById("servicosLista");
    if (!servicos || servicos.length === 0) {
        lista.innerHTML = '<p class="page-subtitle">Nenhum serviço cadastrado ainda.</p>';
        return;
    }

    lista.innerHTML = servicos.map(s => {
        const badgeClass = s.ativo ? "finalizado" : "recusado";
        const badgeLabel = s.ativo ? "Ativo" : "Inativo";
        const btnLabel = s.ativo ? "Inativar" : "Ativar";
        const btnClass = s.ativo ? "btn-danger" : "";
        const btnAction = s.ativo ? "inativar" : "ativar";

        return `
        <div class="content-card" id="card-${s.id}">
            <div class="flex-between">
                <h4>${s.nome}</h4>
                <span class="status-badge ${badgeClass}">${badgeLabel}</span>
            </div>
            <p>${s.descricao || "Sem descrição"}</p>
            <p class="price-tag">R$ ${Number(s.precoBase).toFixed(2).replace(".", ",")}</p>
            <button class="${btnClass}" onclick="toggleServico(${s.id}, '${btnAction}')">${btnLabel}</button>
        </div>`;
    }).join("");
}

async function carregarServicos() {
    try {
        const res = await fetch(`${BASE_URL}/services/admin/all`, {
            headers: authHeaders()
        });
        if (res.status === 403) {
            alert("Acesso negado. Faça login como administrador.");
            window.location.href = "login.html";
            return;
        }
        if (!res.ok) throw new Error("Erro ao carregar serviços.");
        const servicos = await res.json();
        renderizarServicos(servicos);
    } catch (err) {
        document.getElementById("servicosLista").innerHTML =
            '<p class="page-subtitle">Erro ao carregar serviços.</p>';
    }
}

async function toggleServico(id, acao) {
    try {
        const res = await fetch(`${BASE_URL}/services/admin/${id}/${acao}`, {
            method: "PATCH",
            headers: authHeaders()
        });
        if (!res.ok) throw new Error();
        await carregarServicos();
    } catch {
        mostrarFeedback("Erro ao alterar status do serviço.", false);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const token = getToken();
    if (!token || getRoleFromToken(token) !== "AD") {
        window.location.href = "login.html";
        return;
    }

    carregarServicos();

    document.getElementById("btnCadastrarServico").addEventListener("click", async () => {
        const nome = document.getElementById("nomeServico").value.trim();
        const descricao = document.getElementById("descricaoServico").value.trim();
        const precoBase = parseFloat(document.getElementById("precoBase").value);

        if (!nome) { mostrarFeedback("O nome do serviço é obrigatório.", false); return; }
        if (!precoBase || precoBase <= 0) { mostrarFeedback("Informe um preço base válido.", false); return; }

        try {
            const res = await fetch(`${BASE_URL}/services/admin`, {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify({ nome, descricao: descricao || null, precoBase })
            });

            if (res.ok) {
                mostrarFeedback("Serviço cadastrado com sucesso!", true);
                document.getElementById("nomeServico").value = "";
                document.getElementById("descricaoServico").value = "";
                document.getElementById("precoBase").value = "";
                await carregarServicos();
            } else {
                const msg = await res.text();
                mostrarFeedback(msg || "Erro ao cadastrar serviço.", false);
            }
        } catch {
            mostrarFeedback("Erro de conexão com o servidor.", false);
        }
    });
});
