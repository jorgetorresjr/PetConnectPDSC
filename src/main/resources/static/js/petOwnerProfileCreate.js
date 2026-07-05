document.addEventListener("DOMContentLoaded", async () => {

    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const MAX_PHOTO_SIZE = 2 * 1024 * 1024; // 2MB
    const form = document.getElementById("petOwnerProfileForm");
    const inputNome = document.getElementById("name");
    const inputEmail = document.getElementById("email");
    const inputTelefone = document.getElementById("phone");
    const inputFoto = document.getElementById("photo");
    const photoError = document.getElementById("photoError");
    const preview = document.getElementById("preview");

    const showPhotoError = message => {
        if (photoError) {
            photoError.textContent = message;
            photoError.style.display = message ? "block" : "none";
        }
    };

    // Carrega os dados do tutor
    try {
        const response = await fetch(`${BASE_URL}/petowners/me`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (response.ok) {
            const tutor = await response.json();
            inputNome.value = tutor.nome || "";
            inputEmail.value = tutor.email || "";
            inputTelefone.value = tutor.telefone || "";

            if (tutor.id) {
                try {
                    const photoResponse = await fetch(`${BASE_URL}/users/${tutor.id}/photo`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    if (photoResponse.ok) {
                        const blob = await photoResponse.blob();
                        preview.src = URL.createObjectURL(blob);
                    } else {
                        preview.src = "../assets/image.png";
                    }
                } catch (err) {
                    console.warn("Não foi possível carregar a foto do tutor:", err);
                    preview.src = "../assets/image.png";
                }
            } else {
                preview.src = "../assets/image.png";
            }
        } else {
            // API did not return owner data
            if (preview) preview.src = "../assets/image.png";
        }
    } catch (e) {
        console.error(e);
        if (preview) preview.src = "../assets/image.png";
    }

    // Preview da foto
    if (inputFoto) {
        inputFoto.addEventListener("change", () => {
            if (!inputFoto.files.length) {
                showPhotoError("");
                if (preview) preview.src = "../assets/image.png";
                return;
            }

            const file = inputFoto.files[0];
            if (file.size > MAX_PHOTO_SIZE) {
                showPhotoError("A imagem deve ter no máximo 2MB.");
                if (preview) preview.src = "../assets/image.png";
                return;
            }

            showPhotoError("");
            if (preview) preview.src = URL.createObjectURL(file);
        });
    }

    // Salvar
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData();
            formData.append("phone", inputTelefone.value);
            formData.append("name", inputNome.value);
            if (inputFoto.files.length) {
                const file = inputFoto.files[0];
                if (file.size > MAX_PHOTO_SIZE) {
                    showPhotoError("A imagem deve ter no máximo 2MB.");
                    return;
                }
                showPhotoError("");
                formData.append("photo", file);
            }

            try {
                const response = await fetch(`${BASE_URL}/petowners/profile`, {
                    method: "PUT",
                    headers: { Authorization: `Bearer ${token}` },
                    body: formData
                });
                if (response.ok) {
                    alert("Perfil atualizado com sucesso!");
                    window.location.href = "petOwnerHome.html";
                } else {
                    const erro = await response.text();
                    alert(erro);
                }
            } catch (err) {
                console.error(err);
                alert("Erro ao conectar ao servidor.");
            }
        });
    }
});