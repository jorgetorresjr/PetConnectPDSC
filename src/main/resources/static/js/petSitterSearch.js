const BASE_URL = "http://localhost:8080";

document.getElementById("btnVoltar").onclick = () => history.back();

document.getElementById("btnBuscar").addEventListener("click", async () => {
    const serviceId = document.getElementById("servicoFiltro").value;
    const token = localStorage.getItem("token");

    const url = serviceId
        ? `${BASE_URL}/petsitters/filter?serviceId=${serviceId}`
        : `${BASE_URL}/petsitters`;

    const res = await fetch(url, {
        headers: { "Authorization": "Bearer " + token }
    });

    const sitters = await res.json();
    const div = document.getElementById("resultados");
    div.innerHTML = "";

    if (sitters.length === 0) {
        div.innerHTML = "<p>Nenhum pet sitter encontrado.</p>";
        return;
    }

    sitters.forEach(ps => {
        div.innerHTML += `
            <div style="border:1px solid #ccc; padding:1rem; margin:0.5rem 0; border-radius:8px;">
                <strong>${ps.name}</strong><br>
                Especialidade: ${ps.specialty || "-"}<br>
                Disponibilidade: ${ps.availability || "-"}<br>
                <button onclick="window.location.href='petSitterProfile.html?id=${ps.id}'">Ver perfil</button>
            </div>`;
    });
});