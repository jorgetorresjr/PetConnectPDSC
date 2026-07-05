const token = localStorage.getItem("token");

document.addEventListener('DOMContentLoaded', () => {
    // Capturar termo de busca da URL
    const params = new URLSearchParams(window.location.search);
    const termoBusca = params.get('search');
    
    if (termoBusca) {
        document.getElementById('buscaInput').value = termoBusca;
        buscar(); // Executa busca automaticamente
    }

    document.getElementById('btnBuscar').addEventListener('click', buscar);
});

async function buscar() {
    const termo = document.getElementById('buscaInput')?.value.trim();
    const div = document.getElementById('resultados');
    div.innerHTML = '<p>Buscando...</p>';

    let url = `${BASE_URL}/petsitters`;
    if (termo) {
        url += `?search=${encodeURIComponent(termo)}`;
    }

    try {
        const res = await fetch(url, {
            headers: { "Authorization": "Bearer " + token }
        });

        const sitters = await res.json();
        div.innerHTML = "";

        if (!sitters || sitters.length === 0) {
            div.innerHTML = "<p class='page-subtitle'>Nenhum pet sitter encontrado.</p>";
            return;
        }

        sitters.forEach(ps => {
            let precoTexto = "Sob consulta";
            if (ps.servicePrices) {
                try {
                    const precosObj = JSON.parse(ps.servicePrices);
                    const valores = Object.values(precosObj);
                    if (valores.length > 0) precoTexto = `R$ ${parseFloat(valores[0]).toFixed(2)} / hora`;
                } catch (e) {}
            }

            const card = document.createElement("div");
            card.className = "content-card";
            card.innerHTML = `
                <div>
                    <h4>${ps.name}</h4>
                    <p class="mt-8">${ps.specialty || 'Disponível para cuidados'}</p>
                    <p class="price-tag">${precoTexto}</p>
                    <p><strong>Cidade:</strong> ${ps.address?.city || "-"}</p>
                    <p><strong>Bairro:</strong> ${ps.address?.neighborhood || "-"}</p>
                </div>
                <button onclick="window.location.href='petSitterProfile.html?id=${ps.id}'">Agendar Serviço</button>
            `;
            div.appendChild(card);
        });
    } catch (e) {
        console.error(e);
        div.innerHTML = '<p class="page-subtitle">Erro ao buscar pet sitteres.</p>';
    }
}