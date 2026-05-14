const BASE_URL = "http://localhost:8080";

function mostrar(id) {
  document.querySelectorAll("section").forEach(s => s.classList.add("hidden"));
  document.getElementById(id).classList.remove("hidden");
}

function setupLogoutButton() {
  const btn = document.getElementById('logoutBtn');
  if (btn) {
    btn.onclick = function() {
      localStorage.removeItem('token');
      window.location.href = 'login.html';
    };
  }
}

const linkCadastro = document.getElementById("link-cadastro");
if (linkCadastro) {
  linkCadastro.addEventListener("click", () => mostrar("section-cadastro"));
}
const linkLogin = document.getElementById("link-login");
if (linkLogin) {
  linkLogin.addEventListener("click", () => mostrar("section-login"));
}

const formLogin = document.getElementById("form-login");
if (formLogin) formLogin.addEventListener("submit", async (e) => {
  e.preventDefault();
  console.log("[Login] Submit acionado");
  const email = document.getElementById("email").value.trim();
  const senha = document.getElementById("senha").value;
  if (!email || !senha) {
    alert("Preencha email e senha.");
    return;
  }
  const body = { email, senha };
  console.log("[Login] Enviando:", body);
  try {
    const response = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });
    console.log("[Login] Status:", response.status);
    if (response.ok) {
      const data = await response.json();
      console.log("[Login] Token recebido:", data.token);
      localStorage.setItem("token", data.token);
      // Decodifica o token JWT para extrair o tipo de usuário
      const payload = JSON.parse(atob(data.token.split('.')[1]));
      const role = payload.role || payload.authorities?.[0] || "";
      console.log("[Login] Role extraído:", role);
      if (role === "PO") {
        window.location.href = "petOwnerHome.html";
      } else if (role === "PS") {
        window.location.href = "petSitterHome.html";
      } else {
        window.location.href = "home.html";
      }
    } else {
      const erro = await response.text();
      console.error("[Login] Erro backend:", erro);
      alert("Email ou senha incorretos.");
    }
  } catch (err) {
    console.error("[Login] Erro JS:", err);
    alert("Erro ao tentar login. Veja o console.");
  }
});

document.getElementById("btnCadastro").addEventListener("click", async () => {
  const body = {
    nome: document.getElementById("nome").value,
    email: document.getElementById("emailCad").value,
    senha: document.getElementById("senhaCad").value
  };

  const response = await fetch(`${BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  if (response.ok) {
    alert("Cadastro realizado! Faça login.");
    mostrar("section-login");
  } else {
    const erro = await response.text();
    alert("Erro: " + erro);
  }
});

document.getElementById("btnLogout").addEventListener("click", async () => {
  const token = localStorage.getItem("token");

  await fetch(`${BASE_URL}/auth/logout`, {
    method: "POST",
    headers: { "Authorization": `Bearer ${token}` }
  });

  localStorage.removeItem("token");
  mostrar("section-login");
});

window.addEventListener("load", () => {
  if (localStorage.getItem("token")) {
    mostrar("section-home");
  }
});

window.addEventListener('DOMContentLoaded', setupLogoutButton);