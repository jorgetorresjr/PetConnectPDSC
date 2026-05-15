const BASE_URL = "http://localhost:8080";

function mostrar(id) {
  document.querySelectorAll("section").forEach(s => s.classList.add("hidden"));
  const target = document.getElementById(id);
  if (target) target.classList.remove("hidden");
}


const linkCadastro = document.getElementById("link-cadastro");
if (linkCadastro) {
  linkCadastro.addEventListener("click", () => mostrar("section-cadastro"));
}

const linkLogin = document.getElementById("link-login");
if (linkLogin) {
  linkLogin.addEventListener("click", () => mostrar("section-login"));
}


function redirecionarPorRole(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const role = payload.role || payload.authorities?.[0] || "";
    console.log("[Login] Role extraído:", role);
    if (role === "PO") {
      window.location.href = "petOwnerHome.html";
    } else if (role === "PS") {
      window.location.href = "petSitterHome.html";
    } else {
      window.location.href = "home.html";
    }
  } catch {
    window.location.href = "home.html";
  }
}

async function realizarLogin(email, senha, erroEl) {
  if (erroEl) erroEl.style.display = "none";

  if (!email || !senha) {
    if (erroEl) {
      erroEl.textContent = "Preencha email e senha.";
      erroEl.style.display = "block";
    }
    return;
  }

  console.log("[Login] Enviando:", { email });

  try {
    const response = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha })
    });

    console.log("[Login] Status:", response.status);

    if (response.ok) {
      const data = await response.json();
      console.log("[Login] Token recebido:", data.token);
      localStorage.setItem("token", data.token);
      redirecionarPorRole(data.token);
    } else {
      const mensagem = await response.text();
      console.error("[Login] Erro backend:", mensagem);
      if (erroEl) {
        erroEl.textContent = mensagem || "Email ou senha incorretos.";
        erroEl.style.display = "block";
      }
    }
  } catch (err) {
    console.error("[Login] Erro JS:", err);
    if (erroEl) {
      erroEl.textContent = "Erro de conexão com o servidor.";
      erroEl.style.display = "block";
    }
  }
}

const btnLogin = document.getElementById("btnLogin");
if (btnLogin) {
  btnLogin.addEventListener("click", async () => {
    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("senha").value;
    const erroEl = document.getElementById("mensagemErro");
    await realizarLogin(email, senha, erroEl);
  });
}

const formLogin = document.getElementById("form-login");
if (formLogin) {
  formLogin.addEventListener("submit", async (e) => {
    e.preventDefault();
    console.log("[Login] Submit acionado");
    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("senha").value;
    const erroEl = document.getElementById("mensagemErro");
    await realizarLogin(email, senha, erroEl);
  });
}


const btnCadastro = document.getElementById("btnCadastro");
if (btnCadastro) {
  btnCadastro.addEventListener("click", async () => {
    const body = {
      nome: document.getElementById("nome").value,
      email: document.getElementById("emailCad").value,
      senha: document.getElementById("senhaCad").value
    };

    try {
      const response = await fetch(`${BASE_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      if (response.ok) {
        alert("Cadastro realizado com sucesso!");
        const loginLink = document.getElementById("link-login");
        if (loginLink) {
          mostrar("section-login");
        } else {
          window.location.href = "login.html";
        }
      } else {
        const erro = await response.text();
        alert("Erro: " + erro);
      }
    } catch (err) {
      alert("Erro de conexão com o servidor.");
    }
  });
}

function setupLogoutButton() {
  const btnLogout = document.getElementById("btnLogout");
  if (btnLogout) {
    btnLogout.addEventListener("click", async () => {
      const token = localStorage.getItem("token");

      try {
        await fetch(`${BASE_URL}/auth/logout`, {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` }
        });
      } catch (err) {
        console.warn("[Logout] Erro ao chamar endpoint:", err);
      }

      localStorage.removeItem("token");

      const loginLink = document.getElementById("link-login");
      if (loginLink) {
        mostrar("section-login");
      } else {
        window.location.href = "login.html";
      }
    });
  }
}


window.addEventListener("load", () => {
  if (localStorage.getItem("token")) {
    mostrar("section-home");
  }
});

window.addEventListener("DOMContentLoaded", setupLogoutButton);
