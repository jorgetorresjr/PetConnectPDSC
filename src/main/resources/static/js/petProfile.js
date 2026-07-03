document.addEventListener("DOMContentLoaded", async () => {
    // Faz o botão voltar funcionar
    const btnVoltar = document.getElementById("btnVoltar");
    if (btnVoltar) btnVoltar.onclick = () => history.back();
    
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    const token = localStorage.getItem("token");
    const div = document.getElementById("perfilPet");

    if (!id) {
        div.innerHTML = "<p class='msg-erro'>ID do pet não encontrado.</p>";
        return;
    }

    try {
        const res = await fetch(`${BASE_URL}/pets/${id}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            div.innerHTML = "<p class='msg-erro'>Ocorreu um erro ou o pet não existe.</p>";
            return;
        }

        const pet = await res.json();
        div.innerHTML = `
            <p><strong>Nome:</strong> ${pet.name}</p>
            <p><strong>Espécie:</strong> ${pet.specie}</p>
            <p><strong>Raça:</strong> ${pet.breed || "-"}</p>
            <p><strong>Idade:</strong> ${pet.age} ano(s)</p>
            <p><strong>Observações:</strong> ${pet.observations || "-"}</p>
        `;
    } catch (error) {
        div.innerHTML = "<p class='msg-erro'>Falha ao conectar com o servidor.</p>";
        console.error(error);
    }
});