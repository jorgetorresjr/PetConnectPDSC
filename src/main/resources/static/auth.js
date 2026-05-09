const BASE_URL = "http://localhost:8080";

function mostrar(id) {
  document.querySelectorAll("section").forEach(s => s.classList.add("hidden"));
  document.getElementById(id).classList.remove("hidden");
}

document.getElementById("link-cadastro").addEventListener("click", () => mostrar("section-cadastro"));
document.getElementById("link-login").addEventListener("click", () => mostrar("section-login"));

document.getElementById("btnLogin").addEventListener("click", async () => {
  const body = {
    email: document.getElementById("email").value,
    senha: document.getElementById("senha").value
  };

  const response = await fetch(`${BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  if (response.ok) {
    const data = await response.json();
    localStorage.setItem("token", data.token);
    mostrar("section-home");
  } else {
    alert("Email ou senha incorretos.");
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