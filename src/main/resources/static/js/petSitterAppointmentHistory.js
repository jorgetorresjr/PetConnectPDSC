document.addEventListener("DOMContentLoaded", function () {
  const BASE_URL = "http://localhost:8080";
  const btnVoltar = document.getElementById("btnVoltar");
  const msg = document.getElementById("historicoMsg");
  const lista = document.getElementById("historicoLista");
  const token = localStorage.getItem("token");

  if (btnVoltar) {
    btnVoltar.addEventListener("click", function () {
      window.location.href = "petSitterHome.html";
    });
  }

  function formatarData(isoDate) {
    if (!isoDate) return "-";
    const parts = String(isoDate).split("-");
    if (parts.length !== 3) return isoDate;
    return parts[2] + "/" + parts[1] + "/" + parts[0];
  }

  function formatarHora(isoTime) {
    if (!isoTime) return "-";
    return String(isoTime).slice(0, 5);
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
        "<p><strong>Serviço:</strong> " + (a.serviceName || "-") + "</p>" +
        "<p><strong>Data:</strong> " + formatarData(a.serviceDate) + "</p>" +
        "<p><strong>Hora:</strong> " + formatarHora(a.serviceTime) + "</p>" +
        "<p><strong>Status:</strong> " + (a.status || "-") + "</p>";

      lista.appendChild(card);
    });
  }

  async function carregarHistorico() {
    if (!token) {
      msg.textContent = "Sessão expirada. Faça login novamente.";
      return;
    }

    try {
      const response = await fetch(BASE_URL + "/appointments/petsitter", {
        headers: { Authorization: "Bearer " + token }
      });

      if (!response.ok) {
        msg.textContent = "Não foi possível carregar o histórico.";
        return;
      }

      const data = await response.json();

      const ordenado = (Array.isArray(data) ? data : []).sort(function (a, b) {
        const da = new Date(a.createdAt || 0).getTime();
        const db = new Date(b.createdAt || 0).getTime();
        return db - da;
      });

      renderHistorico(ordenado);
    } catch (e) {
      msg.textContent = "Erro de conexão ao carregar histórico.";
    }
  }

  carregarHistorico();
});