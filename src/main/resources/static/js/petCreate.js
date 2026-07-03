document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("petCreateForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        if (!token) {
            alert("Você precisa estar logado.");
            window.location.href = "login.html";
            return;
        }

        // Validar foto
        const photoInput = document.getElementById("photo");
        if (photoInput.files.length > 0) {
            const file = photoInput.files[0];
            const tiposValidos = ["image/jpeg", "image/png", "image/gif", "image/webp"];
            
            if (!tiposValidos.includes(file.type)) {
                alert("Apenas imagens JPEG, PNG, GIF ou WebP são permitidas.");
                return;
            }
            
            if (file.size > 5 * 1024 * 1024) {
                alert("A foto deve ter no máximo 5MB.");
                return;
            }
        }

        const formData = new FormData(form);
        const erroDiv = document.getElementById("pet-create-errors");
        erroDiv.style.display = "none";
        erroDiv.textContent = "";

        try {
            const response = await fetch(`${BASE_URL}/pets`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                alert("Pet cadastrado com sucesso!");
                window.location.href = "petOwnerHome.html";
            } else {
                let mensagens = [];

                // Mapa de tradução dos nomes dos campos
                const traducaoCampos = {
                    "nome": "Nome",
                    "especie": "Espécie",
                    "raca": "Raça",
                    "idade": "Idade",
                    "observacoes": "Observações",
                    "ownerId": "Proprietário"
                };

                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    const data = await response.json();

                    if (data.errors && Array.isArray(data.errors)) {
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

        } catch (error) {
            console.error(error);
            const erroDiv = document.getElementById("pet-create-errors");
            erroDiv.textContent = "Erro de conexão com o servidor.";
            erroDiv.style.display = "block";
        }
    });
});