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

    const loadProtectedImage = async (imgElement, url) => {
        try {
            const resPhoto = await fetch(url, {
                headers: { "Authorization": "Bearer " + token }
            });
            if (!resPhoto.ok) return;
            const blob = await resPhoto.blob();
            imgElement.src = URL.createObjectURL(blob);
        } catch (err) {
            console.error("Erro ao carregar foto do pet:", err);
        }
    };

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
            <div class="profile-header">
                <img id="petPhoto" class="profile-photo" src="/assets/image.png" alt="Foto do pet" />
                <div class="profile-info">
                    <p><strong>Nome:</strong> ${pet.name}</p>
                    <p><strong>Espécie:</strong> ${pet.specie}</p>
                    <p><strong>Raça:</strong> ${pet.breed || "-"}</p>
                    <p><strong>Idade:</strong> ${pet.age} ano(s)</p>
                    <p><strong>Observações:</strong> ${pet.observations || "-"}</p>
                </div>
            </div>
            <button id="editarPetBtn" class="btn-block mt-20">Editar Pet</button>
        `;

        const petPhoto = document.getElementById("petPhoto");
        if (petPhoto) {
            try {
                await loadProtectedImage(petPhoto, `${BASE_URL}/pets/${id}/photo`);
            } catch (err) {
                petPhoto.src = '/assets/image.png';
            }
        }

        const editarBtn = document.getElementById("editarPetBtn");
        if (editarBtn) {
            editarBtn.addEventListener("click", () => {
                window.location.href = `petCreate.html?id=${id}`;
            });
        }
    } catch (error) {
        div.innerHTML = "<p class='msg-erro'>Falha ao conectar com o servidor.</p>";
        console.error(error);
    }
});