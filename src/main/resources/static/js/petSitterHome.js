document.addEventListener("DOMContentLoaded", function () {
  const BASE_URL = "http://localhost:8080";
  const btnCriarPerfil = document.getElementById("criarPerfilBtn");
  const msg = document.getElementById("solicitacoesMsg");
  const lista = document.getElementById("solicitacoesLista");
  const token = localStorage.getItem("token");
  const btnHistorico = document.getElementById("btnHistorico");

if (btnHistorico) {
  btnHistorico.addEventListener("click", function () {
    window.location.href = "petSitterAppointmentsHistory.html";
  });
}

  if (btnHistorico) {
  btnHistorico.addEventListener("click", function () {
    window.location.href = "petSitterAppointmentsHistory.html";
  });
}

if (btnHistorico) {
  btnHistorico.addEventListener("click", function () {
    window.location.href = "petSitterAppointmentHistory.html";
  });
}
  if (btnCriarPerfil) {
    btnCriarPerfil.addEventListener("click", function () {
      window.location.href = "petSitterProfileCreate.html";
    });
  }

  if (typeof setupLogoutButton === "function") {
    setupLogoutButton();
  }

  function formatarData(isoDate) {
    if (!isoDate) return "-";
    const parts = String(isoDate).split("-");
    if (parts.length !== 3) return isoDate;
    return parts[2] + "/" + parts[1] + "/" + parts[0];
  }

  function renderSolicitacoes(items) {
    lista.innerHTML = "";
    if (!items || items.length === 0) {
      msg.textContent = "Não existem solicitações disponíveis.";
      return;
    }

    msg.textContent = "";

    items.forEach(function (a) {
      const card = document.createElement("div");
      card.className = "card";
      card.style.textAlign = "left";
      card.style.marginBottom = "0.75rem";
      card.style.padding = "0.75rem";
      card.style.border = "1px solid #ddd";
      card.style.borderRadius = "8px";
      card.style.background = "#fff";

      card.innerHTML =
        "<p><strong>Tutor:</strong> " + (a.petOwnerName || "-") + "</p>" +
        "<p><strong>Pet:</strong> " + (a.petName || "-") + "</p>" +
        "<p><strong>Serviço:</strong> " + (a.serviceName || (a.service && a.service.nome) || "-") + "</p>" +
        "<p><strong>Data:</strong> " + formatarData(a.serviceDate) + "</p>" +
        "<p><strong>Status:</strong> " + (a.status || "-") + "</p>";

      lista.appendChild(card);
    });
  }
  async function carregarSolicitacoes() {
    if (!token) {
      msg.textContent = "Sessão expirada. Faça login novamente.";
      return;
    }

    try {
      const response = await fetch(BASE_URL + "/appointments/petsitter", {
        headers: { Authorization: "Bearer " + token }
      });

      if (!response.ok) {
        msg.textContent = "Não foi possível carregar as solicitações.";
        return;
      }

      const data = await response.json();

      const pendentes = (Array.isArray(data) ? data : [])
        .filter(function (a) { return a.status === "PENDENTE"; })
        .sort(function (a, b) {
          const da = new Date(a.createdAt || 0).getTime();
          const db = new Date(b.createdAt || 0).getTime();
          return db - da;
        });

      renderSolicitacoes(pendentes);
    } catch (e) {
      msg.textContent = "Erro de conexão ao carregar solicitações.";
    }
  }

  carregarSolicitacoes();
});