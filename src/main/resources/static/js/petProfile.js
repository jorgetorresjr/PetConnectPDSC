const BASE_URL = "http://localhost:8080";

document.getElementById("btnVoltar").onclick = () => history.back();

(async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    const token = localStorage.getItem("token");

    const res = await fetch(`${BASE_URL}/pets/${id}`, {
        headers: { "Authorization": "Bearer " + token }
    });

    const div = document.getElementById("perfilPet");

    if (!res.ok) { div.innerHTML = "<p>Pet não encontrado.</p>"; return; }

    const pet = await res.json();
    div.innerHTML = `
        <p><strong>Nome:</strong> ${pet.name}</p>
        <p><strong>Espécie:</strong> ${pet.specie}</p>
        <p><strong>Raça:</strong> ${pet.breed}</p>
        <p><strong>Idade:</strong> ${pet.age} ano(s)</p>
        <p><strong>Observações:</strong> ${pet.observations || "-"}</p>`;
})();