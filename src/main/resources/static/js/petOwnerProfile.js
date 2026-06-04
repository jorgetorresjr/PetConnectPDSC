const BASE_URL = "http://localhost:8080";

document.getElementById("btnVoltar").onclick = () => history.back();

(async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    const token = localStorage.getItem("token");

    const res = await fetch(`${BASE_URL}/petowners/${id}`, {
        headers: { "Authorization": "Bearer " + token }
    });

    const div = document.getElementById("perfilTutor");

    if (!res.ok) { div.innerHTML = "<p>Tutor não encontrado.</p>"; return; }

    const owner = await res.json();
    div.innerHTML = `
        <p><strong>Nome:</strong> ${owner.name}</p>
        <p><strong>Email:</strong> ${owner.email}</p>
        <p><strong>Telefone:</strong> ${owner.phone || "-"}</p>`;
})();