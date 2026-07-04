document.addEventListener("DOMContentLoaded", async () => {

    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }
    const form = document.getElementById("petOwnerProfileForm");
    const inputNome = document.getElementById("name");
    const inputEmail = document.getElementById("email");
    const inputTelefone = document.getElementById("phone");
    const inputFoto = document.getElementById("photo");
    const preview = document.getElementById("preview");

    // ==========================
    // Carrega os dados do tutor
    // ==========================

    try {
        const response = await fetch(`${BASE_URL}/petowners/me`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (response.ok) {
            const tutor = await response.json();
            inputNome.value = tutor.nome || "";
            inputEmail.value = tutor.email || "";
            inputTelefone.value = tutor.telefone || "";
        }
    } catch (e) {
        console.error(e);
    }
    // ==========================
    // Preview da foto
    // ==========================
    inputFoto.addEventListener("change", () => {
        if (!inputFoto.files.length) return;
        preview.src = URL.createObjectURL(inputFoto.files[0]);
    });
    // ==========================
    // Salvar
    // ==========================
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append("phone", inputTelefone.value);
        formData.append("name", inputNome.value);
        if (inputFoto.files.length) {
            formData.append("photo", inputFoto.files[0]);
        }

        try {
            const response = await fetch(`${BASE_URL}/petowners/profile`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`
                },
                body: formData
            });
            if (response.ok) {
                alert("Perfil atualizado com sucesso!");
                window.location.href = "petOwnerHome.html";
            } else {
                const erro = await response.text();
                alert(erro);
            }

        } catch (e) {
            console.error(e);
            alert("Erro ao conectar ao servidor.");
        }
    });
});