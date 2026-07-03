const token = localStorage.getItem("token");
let perfilAtualId = null;

// ADICIONEI "async" AQUI PARA O AWAIT FUNCIONAR
document.addEventListener('DOMContentLoaded', async function() {
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    document.getElementById('cadastrarPetBtn')?.addEventListener('click', () => window.location.href = 'petCreate.html');
    
    document.getElementById('criarPerfilBtn')?.addEventListener('click', () => {
        if (perfilAtualId) {
            window.location.href = `petOwnerProfile.html?id=${perfilAtualId}`;
        } else {
            window.location.href = 'petOwnerProfileCreate.html';
        }
    });

    document.getElementById("btnToggleBusca")?.addEventListener("click", () => {
        document.getElementById("secaoBuscaSitter").classList.toggle("hidden");
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
    const historicoLista = document.getElementById('historicoLista');
    try {
        const res = await fetch(`${BASE_URL}/appointments/my`, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error("Erro");
        
        const agendamentos = await res.json();
        historicoLista.innerHTML = "";
        
        const aceitos = agendamentos.filter(a => a.status === 'ACEITO' || a.status === 'AGENDADO');
        const pendentes = agendamentos.filter(a => a.status === 'PENDENTE');
        const finalizados = agendamentos.filter(a => a.status === 'FINALIZADO');
        
        if (document.getElementById('totalAgenda')) document.getElementById('totalAgenda').textContent = aceitos.length;
        if (document.getElementById('totalPendentes')) document.getElementById('totalPendentes').textContent = pendentes.length;
        if (document.getElementById('totalFinalizados')) document.getElementById('totalFinalizados').textContent = finalizados.length;

        if (agendamentos.length === 0) {
            historicoLista.innerHTML = "<p class='page-subtitle'>Sem histórico de serviços.</p>";
            return;
        }

        agendamentos.forEach(ag => {
            let dataFormatada = ag.serviceDate || "-";
            if (ag.serviceDate) {
                const parts = ag.serviceDate.split('-');
                if (parts.length === 3) dataFormatada = `${parts[2]}/${parts[1]}/${parts[0]}`;
            }

            const classeStatus = (ag.status || "PENDENTE").toLowerCase();

            const card = document.createElement("div");
            card.className = "content-card mb-15";
            card.innerHTML = `
                <div class="flex-between">
                    <div>
                        <h4 class="mb-5">${ag.serviceName || "Serviço"}</h4>
                        <p><strong>Pet:</strong> ${ag.petName || "-"}</p>
                        <p><strong>Sitter:</strong> ${ag.petSitterName || "-"}</p>
                        <p><strong>Data:</strong> ${dataFormatada} às ${ag.serviceTime ? ag.serviceTime.slice(0,5) : "-"}</p>
                    </div>
                    <div class="text-right">
                        <span class="status-badge ${classeStatus}">${ag.status || "PENDENTE"}</span>
                    </div>
                </div>
            `;
            historicoLista.appendChild(card);
        });
    } catch (e) { historicoLista.innerHTML = "<p class='page-subtitle'>Erro ao carregar o histórico.</p>"; }
}