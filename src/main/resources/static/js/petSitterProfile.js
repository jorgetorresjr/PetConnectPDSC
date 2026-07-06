const token = localStorage.getItem("token");

// Dicionário para converter o nome do serviço no ID correto para o Backend
const MAPA_SERVICOS = {
    "Passeio": 1, "Hospedagem": 2, "Creche": 3,
    "Banho": 4, "Tosa": 5, "Adestramento": 6
};

document.addEventListener("DOMContentLoaded", async () => {
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const params = new URLSearchParams(window.location.search);
    const sitterId = params.get("id");
    const divPerfil = document.getElementById("perfilPetSitter");

    // Elementos do Modal
    const modal = document.getElementById("agendamentoModal");
    const btnAbrir = document.getElementById("btnSolicitarAgendamento");
    const btnFechar = document.getElementById("btnFecharModal");
    const btnFecharX = document.getElementById("btnFecharX");

    // Controles de abrir/fechar o modal
    if (btnAbrir) btnAbrir.onclick = () => modal.classList.remove("hidden");
    const fecharModal = () => {
        modal.classList.add("hidden");
        document.getElementById("agendamentoMsg").textContent = "";
    };
    if (btnFechar) btnFechar.onclick = fecharModal;
    if (btnFecharX) btnFecharX.onclick = fecharModal;

    if (!sitterId) {
        divPerfil.innerHTML = "<p class='msg-erro'>pet sitter não encontrado (ID ausente).</p>";
        return;
    }

    let sitterNameGlobal = "o pet sitter";

    // ==========================================
    // 1. BUSCAR DADOS DA ANA (PET SITTER)
    // ==========================================
    try {
        const res = await fetch(`${BASE_URL}/petsitters/${sitterId}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) throw new Error("Falha ao carregar perfil.");

        const sitter = await res.json();
        sitterNameGlobal = sitter.name || "o pet sitter";

        // Formatar Disponibilidade (De ["Segunda"] para "Segunda")
        let dias = "-";
        if (sitter.availability) {
            try {
                // 1. Separa o que é JSON (os dias) do que é texto (a hora) usando o |
                const partes = sitter.availability.split('|');

                // 2. Converte a parte dos dias de JSON para Array
                // O JSON.parse tira aquelas aspas e colchetes chatos automaticamente
                const listaDias = JSON.parse(partes[0]);

                // 3. Junta tudo num texto só
                const horario = partes[1] ? ` (${partes[1]})` : "";
                dias = listaDias.join(", ") + horario;

            } catch (e) {
                // Se der qualquer erro, a gente limpa manualmente (plano B)
                dias = sitter.availability.replace(/[\[\]"]/g, '').replace('|', ' ');
            }
        }
        // Formatar Preços e Preencher o Select de Serviços
        let precosHtml = "-";
        const selectServico = document.getElementById("agServiceId");
        selectServico.innerHTML = "<option value=''>Selecione um serviço</option>";

        try {
            if (sitter.servicePrices) {
                const precosObj = JSON.parse(sitter.servicePrices);
                const listaPrecos = [];
                for (const [nomeServico, preco] of Object.entries(precosObj)) {

                    const nomesServicos = {
                        1: "Passeio",
                        2: "Hospedagem",
                        3: "Creche",
                        4: "Banho",
                        5: "Tosa",
                        6: "Adestramento"
                    };

                    const nomeExibicao = nomesServicos[nomeServico] || nomeServico;
                    listaPrecos.push(`<strong>${nomeExibicao}:</strong> R$ ${parseFloat(preco).toFixed(2)}`);

                    // Preenche a caixinha do modal magicamente
                    const opt = document.createElement("option");
                    opt.value = MAPA_SERVICOS[nomeServico] || nomeServico;
                    opt.textContent = `${nomeExibicao} (R$ ${parseFloat(preco).toFixed(2)})`;
                    selectServico.appendChild(opt);
                }
                precosHtml = listaPrecos.join("<br>");
            }
        } catch (e) {
            console.error("Erro ao processar preços", e);
        }

        // Esconde o botão de agendar se for o próprio pet sitter vendo o seu perfil
        try {
            const meuRes = await fetch(`${BASE_URL}/petsitters/me`, { headers: { "Authorization": "Bearer " + token } });
            if (meuRes.ok) {
                const meuPerfil = await meuRes.json();
                if (String(meuPerfil.id) === String(sitter.id)) {
                    btnAbrir.style.display = "none";
                }
            }
        } catch (e) { }



        // Deixar o Perfil lindo no fundo da tela
        divPerfil.innerHTML = `
            <div class="profile-header">
                <img id="sitterPhoto" class="profile-photo" src="/assets/image.png" alt="Foto do pet sitter" />
                <div class="profile-info">
                    <p><strong>Nome:</strong> ${sitter.name}</p>
                    <p><strong>E-mail:</strong> ${sitter.email || "-"}</p>
                    <p><strong>Especialidade:</strong> ${sitter.specialty || "-"}</p>
                    <p><strong>Certificados:</strong> ${sitter.certificates || "Nenhum"}</p>
                </div>
            </div>
            <p style="margin-top:10px; color: var(--primary);"><strong>Dias Disponíveis:</strong></p>
            <p>${dias}</p>
            <p style="margin-top:10px; color: var(--primary);"><strong>Tabela de Preços:</strong></p>
            <p>${precosHtml}</p>
        `;

        const sitterPhoto = document.getElementById("sitterPhoto");
        let _sitterLastObjectUrl = null;
        if (sitterPhoto) {
            sitterPhoto.addEventListener('error', () => {
                sitterPhoto.src = '../assets/image.png';
            });

            try {
                const resPhoto = await fetch(`${BASE_URL}/users/${sitterId}/photo`, {
                    headers: { "Authorization": "Bearer " + token }
                });
                if (resPhoto.ok) {
                    const blob = await resPhoto.blob();
                    const newUrl = URL.createObjectURL(blob);
                    if (_sitterLastObjectUrl) URL.revokeObjectURL(_sitterLastObjectUrl);
                    sitterPhoto.src = newUrl;
                    _sitterLastObjectUrl = newUrl;
                } else {
                    sitterPhoto.src = '../assets/image.png';
                }
            } catch (err) {
                console.error("Erro ao carregar foto do petsitter:", err);
                sitterPhoto.src = '../assets/image.png';
            }
        }
    } catch (e) {
        divPerfil.innerHTML = "<p class='msg-erro'>Erro ao carregar os dados do Pet Sitter.</p>";
    }

    // ==========================================
    // 2. BUSCAR OS SEUS PETS (PARA O MODAL)
    // ==========================================
    try {
        const resPets = await fetch(`${BASE_URL}/pets/my`, {
            headers: { "Authorization": "Bearer " + token }
        });

        const selectPet = document.getElementById("agPetId");
        if (resPets.ok) {
            const pets = await resPets.json();
            selectPet.innerHTML = "<option value=''>Selecione um pet</option>";
            if (pets.length === 0) {
                selectPet.innerHTML = "<option value=''>Nenhum pet cadastrado.</option>";
            } else {
                pets.forEach(pet => {
                    const opt = document.createElement("option");
                    opt.value = pet.id;
                    opt.textContent = pet.name;
                    selectPet.appendChild(opt);
                });
            }
        }
    } catch (e) {
        console.error("Erro ao buscar pets", e);
    }

    // ==========================================
    // 3. ENVIAR O AGENDAMENTO PARA O JAVA
    // ==========================================
    document.getElementById("agendamentoForm").onsubmit = async (e) => {
        e.preventDefault();
        const msg = document.getElementById("agendamentoMsg");

        const payload = {
            petSitterId: parseInt(sitterId),
            petId: parseInt(document.getElementById("agPetId").value),
            serviceId: parseInt(document.getElementById("agServiceId").value),
            serviceDate: document.getElementById("agDate").value,
            serviceTime: document.getElementById("agTime").value + ":00",
            status: "PENDENTE"
        };

        try {
            msg.style.color = "var(--primary)";
            msg.textContent = "Processando agendamento...";

            console.log("DADOS ENVIADOS:", JSON.stringify(payload));
            const res = await fetch(`${BASE_URL}/appointments`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert(`Agendamento solicitado para ${sitterNameGlobal} com sucesso!`);
                fecharModal();
                window.location.href = "petOwnerHome.html";
            } else {
                msg.style.color = "var(--danger)";
                msg.textContent = "Erro ao agendar. Verifique os dados.";
            }
        } catch (error) {
            msg.style.color = "var(--danger)";
            msg.textContent = "Falha de conexão com o servidor.";
        }
    };
});