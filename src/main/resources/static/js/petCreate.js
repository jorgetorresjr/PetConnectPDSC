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
        const formData = new FormData(form);

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
                const erro = await response.text();
                console.error("Erro:", erro);
                alert("Erro ao salvar o pet:\n" + erro);
            }

        } catch (error) {
            console.error(error);
            alert("Erro de conexão com o servidor.");
        }
    });
});