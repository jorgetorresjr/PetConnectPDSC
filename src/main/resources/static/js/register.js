const BASE_URL = "http://localhost:8080";


document.getElementById("form-register").addEventListener("submit", async (e) => {
  e.preventDefault();

  // Monta o objeto Address com CEP formatado
  let rawCep = document.getElementById("addressCep").value.replace(/\D/g, "");
  if (rawCep.length === 8) rawCep = rawCep.replace(/(\d{5})(\d{3})/, "$1-$2");
  const address = {
    street: document.getElementById("addressStreet").value,
    number: document.getElementById("addressNumber").value,
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

  const response = await fetch(`${BASE_URL}/users/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  if (response.ok) {
    alert("Cadastro realizado com sucesso! Faça login.");
    window.location.href = "login.html";
  } else {
    const erro = await response.text();
    alert("Erro ao cadastrar: " + erro);
  }
});
