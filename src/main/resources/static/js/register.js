document.getElementById("form-register").addEventListener("submit", async (e) => {
  e.preventDefault();

  let rawCep = document.getElementById("addressCep").value.replace(/\D/g, "");
  if (rawCep.length === 8) rawCep = rawCep.replace(/(\d{5})(\d{3})/, "$1-$2");

  const address = {
    street: document.getElementById("addressStreet").value,
    number: parseInt(document.getElementById("addressNumber").value),
    complement: document.getElementById("addressComplement").value,
    neighborhood: document.getElementById("addressNeighborhood").value,
    city: document.getElementById("addressCity").value,
    state: document.getElementById("addressState").value,
    cep: rawCep
  };

  const body = {
    name: document.getElementById("name").value,
    email: document.getElementById("emailRegister").value,
    password: document.getElementById("passwordRegister").value,
    cpf: document.getElementById("cpfRegister").value,
    birthDate: document.getElementById("birthDateRegister").value,
    phone: document.getElementById("phoneRegister").value,
    address: address,
    role: document.getElementById("roleRegister").value
  };

  const response = await fetch(`${BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  const erroDiv = document.getElementById("register-errors");
  erroDiv.style.display = "none";
  erroDiv.textContent = "";

  if (response.ok) {
    alert("Cadastro realizado com sucesso! Faça login.");
    window.location.href = "login.html";
  } else {
    let mensagens = [];

    // Mapa de tradução dos nomes dos campos
    const traducaoCampos = {
      "phone": "Telefone",
      "password": "Senha",
      "name": "Nome",
      "email": "E-mail",
      "cpf": "CPF",
      "birthDate": "Data de Nascimento",
      "address.street": "Logradouro",
      "address.number": "Número",
      "address.neighborhood": "Bairro",
      "address.city": "Cidade",
      "address.state": "Estado",
      "address.cep": "CEP",
      "address.complement": "Complemento"
    };

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      const data = await response.json();

      if (data.errors && Array.isArray(data.errors)) {
        // Erros de validação com binding errors habilitado
        mensagens = data.errors.map(e => {
          const nomeCampoTraduzido = traducaoCampos[e.field] || e.field;
          return `• ${nomeCampoTraduzido}: ${e.defaultMessage}`;
        });
      } else if (data.message) {
        mensagens = [data.message];
      } else {
        mensagens = ["Erro desconhecido ao processar a requisição."];
      }
    } else {
      const texto = await response.text();
      mensagens = [texto || "Erro ao processar a requisição."];
    }

    erroDiv.textContent = mensagens.join("\n");
    erroDiv.style.display = "block";
  }
});