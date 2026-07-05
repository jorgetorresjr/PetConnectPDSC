document.addEventListener("DOMContentLoaded", async () => {
    const MAX_PHOTO_SIZE = 2 * 1024 * 1024; // 2MB
    const form = document.getElementById("petCreateForm");
    if (!form) return;

    const params = new URLSearchParams(window.location.search);
    const petId = params.get("id");
    const pageTitle = document.querySelector("h2.mb-25");
    const submitButton = form.querySelector("button[type='submit']");
    const erroDiv = document.getElementById("pet-create-errors");
    const photoInput = document.getElementById("photo");
    const photoPreview = document.getElementById("photoPreview");
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Você precisa estar logado.");
        window.location.href = "login.html";
        return;
    }

    if (petId) {
        if (pageTitle) pageTitle.textContent = "Editar Pet";
        if (submitButton) submitButton.textContent = "Salvar Alterações";

        try {
            const response = await fetch(`${BASE_URL}/pets/${petId}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (response.ok) {
                const pet = await response.json();
                document.getElementById("nome").value = pet.name || "";
                document.getElementById("especie").value = pet.specie || "";
                document.getElementById("raca").value = pet.breed || "";
                document.getElementById("idade").value = pet.age || "";
                document.getElementById("observacoes").value = pet.observations || "";
                if (photoPreview) {
                photoPreview.src = "../assets/image.png"; // Default image
                    try {
                        const photoResponse = await fetch(`${BASE_URL}/pets/${petId}/photo`, {
                            headers: {
                                Authorization: `Bearer ${token}`
                            }
                        });
                        if (photoResponse.ok) {
                            const blob = await photoResponse.blob();
                            photoPreview.src = URL.createObjectURL(blob);
                        }
                    } catch (photoError) {
                        console.warn("Não foi possível carregar a foto do pet:", photoError);
                    }
                }
            } else if (response.status === 404) {
                erroDiv.textContent = "Pet não encontrado para edição.";
                erroDiv.style.display = "block";
            } else {
                erroDiv.textContent = "Não foi possível carregar os dados do pet.";
                erroDiv.style.display = "block";
            }
        } catch (error) {
            console.error(error);
            erroDiv.textContent = "Erro ao carregar os dados do pet.";
            erroDiv.style.display = "block";
        }
    }

    if (photoInput && photoPreview) {
        photoInput.addEventListener("change", () => {
            if (!photoInput.files.length) {
                photoPreview.src = "../assets/image.png";
                return;
            }

            const file = photoInput.files[0];
            if (file && file.type.startsWith("image/")) {
                photoPreview.src = URL.createObjectURL(file);
            } else {
                photoPreview.src = "../assets/image.png";
            }
        });
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        if (!token) {
            alert("Você precisa estar logado.");
            window.location.href = "login.html";
            return;
        }

        // Validar foto
        if (photoInput && photoInput.files.length > 0) {
            const file = photoInput.files[0];
            const tiposValidos = ["image/jpeg", "image/png", "image/gif", "image/webp"];
            
            if (!tiposValidos.includes(file.type)) {
                erroDiv.textContent = "Apenas imagens JPEG, PNG, GIF ou WebP são permitidas.";
                erroDiv.style.display = "block";
                return;
            }
            
            if (file.size > MAX_PHOTO_SIZE) {
                erroDiv.textContent = "A foto deve ter no máximo 2MB.";
                erroDiv.style.display = "block";
                return;
            }
        }

        const formData = new FormData(form);
        const erroDiv = document.getElementById("pet-create-errors");
        erroDiv.style.display = "none";
        erroDiv.textContent = "";

        try {
            const url = petId ? `${BASE_URL}/pets/${petId}` : `${BASE_URL}/pets`;
            const method = petId ? "PUT" : "POST";
            const response = await fetch(url, {
                method,
                headers: {
                    Authorization: `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                alert(petId ? "Pet atualizado com sucesso!" : "Pet cadastrado com sucesso!");
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