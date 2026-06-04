const BASE_URL = "http://localhost:8080";

document.getElementById("btnVoltar").onclick = () => history.back();

(async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    const token = localStorage.getItem("token");

    const res = await fetch(`${BASE_URL}/petsitters/${id}`, {
        headers: { "Authorization": "Bearer " + token }
    });

    const div = document.getElementById("perfilPetSitter");

    if (!res.ok) { div.innerHTML = "<p>Pet sitter não encontrado.</p>"; return; }

    const ps = await res.json();
    div.innerHTML = `
        <p><strong>Nome:</strong> ${ps.name}</p>
        <p><strong>Email:</strong> ${ps.email}</p>
        <p><strong>Telefone:</strong> ${ps.phone || "-"}</p>
        <p><strong>Especialidade:</strong> ${ps.specialty || "-"}</p>
        <p><strong>Certificados:</strong> ${ps.certificates || "-"}</p>
        <p><strong>Disponibilidade:</strong> ${ps.availability || "-"}</p>
        <p><strong>Preços:</strong> ${ps.servicePrices || "-"}</p>`;
})();