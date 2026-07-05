document.addEventListener("DOMContentLoaded", async () => {
    const API_URL = window.BASE_URL || "http://localhost:8080";
    
    const token = localStorage.getItem("token");
    const div = document.getElementById("perfilTutor");
    
    // Pega o ID da URL
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!div) return;

    try {
        // Usamos API_URL aqui, que agora tem um valor garantido
        const res = await fetch(`${API_URL}/petowners/${id}`, {
            headers: { 
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });

        if (!res.ok) {
            div.innerHTML = `<p>Tutor não encontrado (Status: ${res.status}).</p>`;
            return;
        }

        const owner = await res.json();
        div.innerHTML = `
            <div class="profile-header">
                <img id="ownerPhoto" class="profile-photo" src="/assets/image.png" alt="Foto do tutor" />
                <div class="profile-info">
                    <p><strong>Nome:</strong> ${owner.name || owner.nome || "Não informado"}</p>
                    <p><strong>Email:</strong> ${owner.email || "Não informado"}</p>
                    <p><strong>Telefone:</strong> ${owner.phone || owner.telefone || "-"}</p>
                </div>
            </div>
        `;

        const ownerPhoto = document.getElementById("ownerPhoto");
        if (ownerPhoto) {
            try {
                const resPhoto = await fetch(`${API_URL}/users/${id}/photo`, {
                    headers: { "Authorization": "Bearer " + token }
                });
                    if (resPhoto.ok) {
                        const blob = await resPhoto.blob();
                        ownerPhoto.src = URL.createObjectURL(blob);
                    } else {
                        ownerPhoto.src = '/assets/image.png';
                    }
            } catch (err) {
                console.error("Erro ao carregar foto do tutor:", err);
            }
        }
    } catch (error) {
        console.error("Erro na busca:", error);
        div.innerHTML = "<p>Erro ao conectar com o servidor.</p>";
    }
});