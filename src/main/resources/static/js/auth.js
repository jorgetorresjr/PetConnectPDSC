const BASE_URL = "http://localhost:8080";

const btnLogin = document.getElementById("btnLogin");

if (btnLogin) {

  btnLogin.addEventListener("click", async () => {

    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;
    const erroLogin = document.getElementById("mensagemErro");

    erroLogin.style.display = "none";

    try {

      const response = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          email: email,
          senha
        })
      });

      if (!response.ok) {

        const mensagem = await response.text();

        erroLogin.textContent = mensagem;
        erroLogin.style.display = "block";

        return;
      }

      const data = await response.json();

      localStorage.setItem("token", data.token);

      window.location.href = "home.html";

    } catch (err) {

      erroLogin.textContent = "Erro de conexão com o servidor.";
      erroLogin.style.display = "block";
    }
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
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
      });

      if (response.ok) {

        alert("Cadastro realizado com sucesso!");

        window.location.href = "login.html";

      } else {

        const erro = await response.text();

        alert("Erro: " + erro);
      }

    } catch (err) {

      alert("Erro de conexão com o servidor.");
    }
  });
}
