const token = localStorage.getItem("token");
let perfilAtualId = null;

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

function formatarData(data) {
    if (!data) return "-";
    const partes = data.split("-");
    return partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : data;
}

function formatarHora(hora) {
    return hora ? hora.slice(0, 5) : "-";
}

// ADICIONEI "async" AQUI PARA O AWAIT FUNCIONAR
document.addEventListener('DOMContentLoaded', async function() {
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    document.getElementById('cadastrarPetBtn')?.addEventListener('click', () => window.location.href = 'petCreate.html');
    
    document.getElementById('criarPerfilBtn')?.addEventListener('click', () => {
        window.location.href = 'petOwnerProfileCreate.html';
    });

    document.getElementById("btnToggleBusca")?.addEventListener("click", () => {
        document.getElementById("secaoBuscaSitter").classList.toggle("hidden");
    });

    document.getElementById('btnBuscarSitter')?.addEventListener('click', () => {
        const termo = document.getElementById('buscaSitter')?.value.trim();
        if (termo) {
            window.location.href = `petSitterSearch.html?search=${encodeURIComponent(termo)}`;
        } else {
            window.location.href = 'petSitterSearch.html';
        }
    });

    document.getElementById('btnLimparBusca')?.addEventListener('click', () => {
        document.getElementById('buscaSitter').value = '';
    });
    
    const modalHistorico = document.getElementById('modalHistorico');
    document.getElementById('btnAbrirHistorico')?.addEventListener('click', () => {
        modalHistorico.classList.remove('hidden');
    });
    document.getElementById('fecharModalHistorico')?.addEventListener('click', () => {
        modalHistorico.classList.add('hidden');
    });
    
    // Fechar ao clicar fora (no overlay)
    modalHistorico?.addEventListener('click', (e) => {
        if (e.target.id === 'modalHistorico') {
            modalHistorico.classList.add('hidden');
        }
    });

    

    // Agora o await funciona corretamente porque a função é async
    await verificarPerfil();
    carregarPets();
    carregarSitters();
    carregarHistorico();
});

async function verificarPerfil() {
    try {
        const res = await fetch(`${BASE_URL}/petowners/me`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (res.status === 403 || res.status === 401) {
            localStorage.removeItem("token");
            window.location.href = "login.html";
            return;
        }

        if (res.ok) {
            const perfil = await res.json();
            
            const userNameDisplay = document.getElementById("userNameDisplay");
            if (userNameDisplay && perfil.name) userNameDisplay.textContent = perfil.name.split(' ')[0];

            if (perfil.id) {
                perfilAtualId = perfil.id;
                const criarPerfilBtn = document.getElementById('criarPerfilBtn');
                if (criarPerfilBtn) criarPerfilBtn.innerHTML = "Meu Perfil";
            }
        }
    } catch (e) { console.error("Erro ao verificar perfil", e); }
}

async function carregarPets() {
    const petsLista = document.getElementById('petsLista');
    const totalPetsEl = document.getElementById('totalPets');

    try {
        const res = await fetch(`${BASE_URL}/pets/my`, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error("Erro");
        
        const pets = await res.json();
        if (totalPetsEl) totalPetsEl.textContent = pets.length;
        
        petsLista.innerHTML = "";
        if (pets.length === 0) {
            petsLista.innerHTML = "<p class='page-subtitle'>Ainda não tem pets registados.</p>";
            return;
        }

        pets.forEach(pet => {
            const card = document.createElement("div");
            card.className = "content-card";
            card.innerHTML = `
                <div>
                    <h4> ${pet.name}</h4>
                    <p class="mt-5">${pet.specie} (${pet.breed})</p>
                </div>
                <button class="btn-secondary" onclick="window.location.href='petProfile.html?id=${pet.id}'">Ver Perfil</button>
            `;
            petsLista.appendChild(card);
        });
    } catch (e) { petsLista.innerHTML = "<p class='page-subtitle'>Erro ao carregar os pets.</p>"; }
}

async function carregarSitters() {
    const sittersLista = document.getElementById('sittersLista');
    try {
        const res = await fetch(`${BASE_URL}/petsitters`, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error("Erro");
        
        const sitters = await res.json();
        sittersLista.innerHTML = "";
        if (sitters.length === 0) { sittersLista.innerHTML = "<p class='page-subtitle'>Nenhum cuidador.</p>"; return; }

        sitters.forEach(sitter => {
            let precoTexto = "Sob consulta";
            if (sitter.servicePrices) {
                try {
                    const precosObj = JSON.parse(sitter.servicePrices);
                    const valores = Object.values(precosObj);
                    if (valores.length > 0) precoTexto = `R$ ${parseFloat(valores[0]).toFixed(2)} / hora`;
                } catch (e) {}
            }

            const card = document.createElement("div");
            card.className = "content-card";
            card.innerHTML = `
                <div>
                    <h4 class="flex-between">
                        ${sitter.name} 
                        <span class="status-badge novo">⭐ Novo</span>
                    </h4>
                    <p class="mt-8">${sitter.specialty || 'Disponível para cuidados'}</p>
                    <p class="price-tag">${precoTexto}</p>
                </div>
                <button onclick="window.location.href='petSitterProfile.html?id=${sitter.id}'">Agendar Serviço</button>
            `;
            sittersLista.appendChild(card);
        });
    } catch (e) { sittersLista.innerHTML = "<p class='page-subtitle'>Erro ao carregar os cuidadores.</p>"; }
}

async function carregarHistorico() {
    const historicoLista = document.getElementById("historicoLista");

    try {
        const res = await fetch(`${BASE_URL}/appointments/my`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Erro");

        const agendamentos = await res.json();

        historicoLista.innerHTML = "";

        const aceitos = agendamentos.filter(a =>
            a.status === "ACEITO" || a.status === "EM_ANDAMENTO"
        );

        const pendentes = agendamentos.filter(a =>
            a.status === "PENDENTE"
        );

        const finalizados = agendamentos.filter(a =>
            a.status === "CONCLUIDO"
        );

        document.getElementById("totalAgenda").textContent = aceitos.length;
        document.getElementById("totalPendentes").textContent = pendentes.length;
        document.getElementById("totalFinalizados").textContent = finalizados.length;

        if (agendamentos.length === 0) {
            historicoLista.innerHTML =
                "<p class='page-subtitle'>Sem histórico de serviços.</p>";
            return;
        }

        agendamentos.forEach(ag => {

            const classeStatus = (ag.status || "PENDENTE").toLowerCase();

            const card = document.createElement("div");
            card.className = "content-card mb-15";

            card.innerHTML = `
                <div class="flex-between">
                    <div>
                        <h4 class="mb-5">${ag.serviceName || "-"}</h4>
                        <p><strong>Pet:</strong> ${ag.petName || "-"}</p>
                        <p><strong>Cuidador:</strong> ${ag.petSitterName || "-"}</p>
                        <p><strong>Data:</strong> ${formatarData(ag.serviceDate)} às ${formatarHora(ag.serviceTime)}</p>
                    </div>

                    <div class="text-right">
                        <span class="status-badge ${classeStatus}">
                            ${formatarStatus(ag.status)}
                        </span>
                    </div>
                </div>
            `;

            // Histórico do agendamento
            if (ag.history) {

                const histDiv = document.createElement("div");
                histDiv.className = "history-box";

                const titulo = document.createElement("div");
                titulo.className = "history-title";
                titulo.textContent = "Histórico";
                histDiv.appendChild(titulo);

                ag.history
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

            historicoLista.appendChild(card);

        });

    } catch (e) {
        historicoLista.innerHTML =
            "<p class='page-subtitle'>Erro ao carregar o histórico.</p>";
    }
}