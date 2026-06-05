const BASE_URL = "http://localhost:8080";

const btnVoltar = document.getElementById("btnVoltar");
const modal = document.getElementById("agendamentoModal");
const formAg = document.getElementById("agendamentoForm");
const msgAg = document.getElementById("agendamentoMsg");
const btnSolicitar = document.getElementById("btnSolicitarAgendamento");
const btnFecharModal = document.getElementById("btnFecharModal");
const perfilDiv = document.getElementById("perfilPetSitter");

let petSitterAtual = null;
let servicesCatalog = [];

if (btnVoltar) {
  btnVoltar.onclick = function () {
    history.back();
  };
}

function getToken() {
  return localStorage.getItem("token");
}

function showError(msg) {
  if (!msgAg) return;
  msgAg.style.color = "#b00020";
  msgAg.textContent = msg || "Erro inesperado.";
}

function showSuccess(msg) {
  if (!msgAg) return;
  msgAg.style.color = "#1b5e20";
  msgAg.textContent = msg || "Sucesso.";
}

function resetModal() {
  if (!formAg) return;
  formAg.reset();

  if (msgAg) msgAg.textContent = "";

  const serviceSelect = document.getElementById("agServiceId");
  const petSelect = document.getElementById("agPetId");
  const sitterInput = document.getElementById("agPetSitterId");

  if (serviceSelect) {
    serviceSelect.innerHTML = "<option value=''>Selecione um serviço</option>";
  }
  if (petSelect) {
    petSelect.innerHTML = "<option value=''>Selecione um pet</option>";
  }
  if (sitterInput) {
    sitterInput.value = "";
  }
}

function closeModal() {
  if (!modal) return;
  modal.classList.add("hidden");
  resetModal();
}

async function carregarCatalogoServicos(token) {
  if (servicesCatalog.length > 0) return servicesCatalog;

  const res = await fetch(BASE_URL + "/services", {
    headers: token ? { Authorization: "Bearer " + token } : {}
  });

  if (!res.ok) {
    throw new Error("Falha ao carregar catálogo de serviços.");
  }

  const data = await res.json();
  servicesCatalog = Array.isArray(data) ? data : [];
  return servicesCatalog;
}

function normalizarTexto(texto) {
  return String(texto || "")
    .trim()
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");
}

async function fillServicesSelectFromEntityOrPrices(token) {
  const serviceSelect = document.getElementById("agServiceId");
  if (!serviceSelect) return;

  serviceSelect.innerHTML = "<option value=''>Selecione um serviço</option>";
  let added = 0;

  if (Array.isArray(petSitterAtual && petSitterAtual.services) && petSitterAtual.services.length > 0) {
    petSitterAtual.services.forEach(function (s) {
      if (s && s.id != null && s.nome) {
        serviceSelect.innerHTML += "<option value='" + s.id + "'>" + s.nome + "</option>";
        added++;
      }
    });
  }

  if (added === 0) {
    const catalogo = await carregarCatalogoServicos(token);

    try {
      const pricesObj = JSON.parse((petSitterAtual && petSitterAtual.servicePrices) || "{}");
      const nomes = Object.keys(pricesObj);

      nomes.forEach(function (nome) {
        const nomeNorm = normalizarTexto(nome);

        const found = catalogo.find(function (s) {
          return normalizarTexto(s && s.nome) === nomeNorm;
        });

        if (found && found.id != null && found.nome) {
          serviceSelect.innerHTML += "<option value='" + found.id + "'>" + found.nome + "</option>";
          added++;
        }
      });
    } catch (e) {
      // Ignora parse inválido de servicePrices.
    }
  }

  if (added === 0) {
    showError("Este pet sitter não possui serviços disponíveis para agendamento.");
  }
}

async function carregarPetsDoTutor(token) {
  const petSelect = document.getElementById("agPetId");
  if (!petSelect) return;

  petSelect.innerHTML = "<option value=''>Selecione um pet</option>";

  const petsRes = await fetch(BASE_URL + "/pets/my", {
    headers: token ? { Authorization: "Bearer " + token } : {}
  });

  if (!petsRes.ok) {
    throw new Error("Não foi possível carregar seus pets.");
  }

  const pets = await petsRes.json();
  (Array.isArray(pets) ? pets : []).forEach(function (p) {
    if (p && p.id != null) {
      petSelect.innerHTML += "<option value='" + p.id + "'>" + (p.name || ("Pet #" + p.id)) + "</option>";
    }
  });
}

async function abrirModalAgendamento() {
  const token = getToken();

  if (!token) {
    showError("Você precisa estar logado para agendar.");
    return;
  }

  if (!petSitterAtual || petSitterAtual.id == null) {
    showError("Pet sitter não carregado.");
    if (modal) modal.classList.remove("hidden");
    return;
  }

  resetModal();

  const sitterInput = document.getElementById("agPetSitterId");
  if (sitterInput) sitterInput.value = String(petSitterAtual.id);

  try {
    await fillServicesSelectFromEntityOrPrices(token);
    await carregarPetsDoTutor(token);
  } catch (e) {
    showError(e.message || "Erro ao preparar agendamento.");
  }

  if (modal) modal.classList.remove("hidden");
}

if (btnFecharModal) {
  btnFecharModal.addEventListener("click", closeModal);
}

if (btnSolicitar) {
  btnSolicitar.addEventListener("click", abrirModalAgendamento);
}

if (formAg) {
  formAg.addEventListener("submit", async function (event) {
    event.preventDefault();

    const token = getToken();
    if (!token) return showError("Você precisa estar logado para agendar.");

    const petSitterId = (document.getElementById("agPetSitterId") || {}).value || "";
    const serviceIdRaw = (document.getElementById("agServiceId") || {}).value || "";
    const petIdRaw = (document.getElementById("agPetId") || {}).value || "";
    const serviceDate = (document.getElementById("agDate") || {}).value || "";
    const serviceTime = (document.getElementById("agTime") || {}).value || "";

    if (!serviceIdRaw) return showError("Selecione um serviço.");
    if (!petIdRaw) return showError("Selecione um pet.");
    if (!serviceDate) return showError("Data do serviço é obrigatória.");
    if (!serviceTime) return showError("Horário do serviço é obrigatório.");

    const serviceId = Number(serviceIdRaw);
    const petId = Number(petIdRaw);

    if (!Number.isInteger(serviceId) || serviceId <= 0) {
      return showError("Serviço inválido.");
    }

    if (!Number.isInteger(petId) || petId <= 0) {
      return showError("Pet inválido.");
    }

    const body = new URLSearchParams({
      petSitterId: String(petSitterId),
      serviceId: String(serviceId),
      petId: String(petId),
      serviceDate: String(serviceDate),
      serviceTime: String(serviceTime)
    });

    try {
      const res = await fetch(BASE_URL + "/appointments", {
        method: "POST",
        headers: {
          Authorization: "Bearer " + token,
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body: body.toString()
      });

      if (!res.ok) {
        const err = await res.text();
        return showError(err || "Não foi possível enviar solicitação.");
      }

      showSuccess("Solicitação enviada com status PENDENTE.");
      setTimeout(closeModal, 1000);
    } catch (e) {
      showError("Erro de conexão ao enviar solicitação.");
    }
  });
}

(async function carregarPerfil() {
  const token = getToken();
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");

  if (!id) {
    if (perfilDiv) perfilDiv.innerHTML = "<p>ID do pet sitter não informado.</p>";
    if (btnSolicitar) btnSolicitar.disabled = true;
    return;
  }

  try {
    const res = await fetch(BASE_URL + "/petsitters/" + id, {
      headers: token ? { Authorization: "Bearer " + token } : {}
    });

    if (!res.ok) {
      if (perfilDiv) perfilDiv.innerHTML = "<p>Pet sitter não encontrado.</p>";
      if (btnSolicitar) btnSolicitar.disabled = true;
      return;
    }

    const ps = await res.json();
    petSitterAtual = ps;

    if (btnSolicitar && token) {
      try {
        const meuPerfilRes = await fetch(BASE_URL + "/petsitters/me", {
          headers: { Authorization: "Bearer " + token }
        });

        if (meuPerfilRes.ok) {
          const meuPerfil = await meuPerfilRes.json();
          if (meuPerfil && String(meuPerfil.id) === String(ps.id)) {
            btnSolicitar.style.display = "none";
          }
        }
      } catch (e) {
        // mantém o botão visível se não for possível comparar o perfil
      }
    }

    if (perfilDiv) {
      perfilDiv.innerHTML =
        "<p><strong>Nome:</strong> " + (ps.name || "-") + "</p>" +
        "<p><strong>Email:</strong> " + (ps.email || "-") + "</p>" +
        "<p><strong>Telefone:</strong> " + (ps.phone || "-") + "</p>" +
        "<p><strong>Especialidade:</strong> " + (ps.specialty || "-") + "</p>" +
        "<p><strong>Certificados:</strong> " + (ps.certificates || "-") + "</p>" +
        "<p><strong>Disponibilidade:</strong> " + (ps.availability || "-") + "</p>" +
        "<p><strong>Preços:</strong> " + (ps.servicePrices || "-") + "</p>";
    }
  } catch (e) {
    if (perfilDiv) perfilDiv.innerHTML = "<p>Erro ao carregar perfil do pet sitter.</p>";
    if (btnSolicitar) btnSolicitar.disabled = true;
  }
})();