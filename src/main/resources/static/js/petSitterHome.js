const token = localStorage.getItem("token");
let perfilSitterId = null;

document.addEventListener("DOMContentLoaded", async function () {
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const btnCriarPerfil = document.getElementById("criarPerfilBtn");
    const btnVerPerfil = document.getElementById("verPerfilBtn");
    const btnHistorico = document.getElementById("btnHistorico");
    
    if (btnHistorico) btnHistorico.addEventListener("click", () => window.location.href = "petSitterAppointmentHistory.html");
    
    if (btnCriarPerfil) {
        btnCriarPerfil.addEventListener("click", () => {
            // Se já tem perfil criado, vai para a mesma tela de criação, mas para edição no futuro
            window.location.href = "petSitterProfileCreate.html";
        });
    }

    if (btnVerPerfil) {
        btnVerPerfil.addEventListener("click", () => {
            if (perfilSitterId) window.location.href = "petSitterProfile.html?id=" + encodeURIComponent(perfilSitterId);
        });
    }

    // 1. DADOS DO PERFIL E NOME NA NAVBAR
    try {
        const res = await fetch(`${BASE_URL}/petsitters/me`, { headers: { Authorization: `Bearer ${token}` } });
        if (res.ok) {
            const sitter = await res.json();
            
            const userNameDisplay = document.getElementById("userNameDisplay");
            if (userNameDisplay && sitter.name) userNameDisplay.textContent = sitter.name.split(' ')[0];

            if (sitter.specialty) { 
                perfilSitterId = sitter.id;
                if (btnCriarPerfil) btnCriarPerfil.innerHTML = "Editar Serviços";
                if (btnVerPerfil && sitter.id != null) {
                    btnVerPerfil.classList.remove("hidden");
                }
            }
        }
    } catch (e) { console.error(e); }

    // 2. BUSCAR SOLICITAÇÕES E PREENCHER PAINEL
    try {
        const lista = document.getElementById("solicitacoesLista");
        const msg = document.getElementById("solicitacoesMsg");
        
        const resSol = await fetch(`${BASE_URL}/appointments/petsitter`, { headers: { Authorization: `Bearer ${token}` } });
        if (!resSol.ok) { msg.textContent = "Não foi possível carregar as solicitações."; return; }
        
        const data = await resSol.json();
        const agendamentos = Array.isArray(data) ? data : [];
        
        const pendentesArr = agendamentos.filter(a => a.status === "PENDENTE");
        const aceitosArr = agendamentos.filter(a => a.status === "ACEITO" || a.status === "Aceito");
        const concluidosArr = agendamentos.filter(a => a.status === "CONCLUIDO");
        
        if(document.getElementById('totalAgenda')) document.getElementById('totalAgenda').textContent = aceitosArr.length;
        if(document.getElementById('totalPendentes')) document.getElementById('totalPendentes').textContent = pendentesArr.length;
        if(document.getElementById('totalConcluidos')) document.getElementById('totalConcluidos').textContent = concluidosArr.length;

        lista.innerHTML = "";
        if (pendentesArr.length === 0) { 
            msg.textContent = "Você não tem solicitações pendentes no momento. Aproveite o descanso!"; 
            return; 
        }

        msg.textContent = "";
        pendentesArr.forEach(a => {
            let dataFormatada = a.serviceDate || "-";
            if (a.serviceDate) {
                const parts = a.serviceDate.split('-');
                if (parts.length === 3) dataFormatada = `${parts[2]}/${parts[1]}/${parts[0]}`;
            }

            const card = document.createElement("div");
            card.className = "content-card";
            card.innerHTML = `
                <div class="flex-between">
                    <div>
                        <h4 class="mb-5">${a.serviceName || "-"}</h4>
                        <p><strong>Tutor:</strong> ${a.petOwnerName || "-"}</p>
                        <p><strong>Pet:</strong> ${a.petName || "-"}</p>
                        <p><strong>Data:</strong> ${dataFormatada} às ${a.serviceTime ? a.serviceTime.slice(0,5) : "-"}</p>
                    </div>
                    <span class="status-badge pendente">${a.status || "-"}</span>
                </div>
                <button class="btn-secondary mt-5" onclick="window.location.href='petSitterAppointmentHistory.html'">Gerenciar</button>
            `;
            lista.appendChild(card);
        });
    } catch (e) { document.getElementById("solicitacoesMsg").textContent = "Erro de conexão."; }
});