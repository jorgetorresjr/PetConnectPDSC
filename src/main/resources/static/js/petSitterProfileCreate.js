document.addEventListener('DOMContentLoaded', function() {

    // Serviços e preços dinâmicos com checkboxes
    const servicesCheckboxesDiv = document.getElementById('servicesCheckboxes');
    const servicePricesDiv = document.getElementById('servicePrices');
    if (servicesCheckboxesDiv && servicePricesDiv) {
        servicesCheckboxesDiv.querySelectorAll('input[type="checkbox"]').forEach(cb => {
            cb.addEventListener('change', function() {
                const service = cb.value;
                const priceFieldId = `preco_field_${service}`;
                if (cb.checked) {
                    // Adiciona campo de preço
                    const label = document.createElement('label');
                    label.textContent = `Preço para ${service}:`;
                    label.setAttribute('for', `preco_${service}`);
                    label.id = `label_${service}`;
                    const input = document.createElement('input');
                    input.type = 'number';
                    input.name = `preco_${service}`;
                    input.id = `preco_${service}`;
                    input.min = 0;
                    input.step = 0.01;
                    input.required = true;
                    const div = document.createElement('div');
                    div.id = priceFieldId;
                    div.appendChild(label);
                    div.appendChild(input);
                    servicePricesDiv.appendChild(div);
                } else {
                    // Remove campo de preço
                    const div = document.getElementById(priceFieldId);
                    if (div) servicePricesDiv.removeChild(div);
                }
            });
        });
    }

    // Garante que o botão de logout funcione
    if (typeof setupLogoutButton === 'function') setupLogoutButton();

    const form = document.getElementById('petSitterProfileForm');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
                        const formData = new FormData(form);
                        // Formatar CEP se existir campo de CEP
                        const cepInput = form.querySelector('[name="cep"], [id*="cep"]');
                        if (cepInput) {
                            let rawCep = cepInput.value.replace(/\D/g, "");
                            if (rawCep.length === 8) rawCep = rawCep.replace(/(\d{5})(\d{3})/, "$1-$2");
                            formData.set(cepInput.name || 'cep', rawCep);
                        }

            // Serviços selecionados
            const selectedServices = Array.from(document.querySelectorAll('#servicesCheckboxes input[type="checkbox"]:checked')).map(cb => cb.value);
            formData.set('services', JSON.stringify(selectedServices));
            // Preços dos serviços como JSON
            const servicePrices = {};
            selectedServices.forEach(service => {
                const priceInput = form.querySelector(`[name='preco_${service}']`);
                if (priceInput) {
                    servicePrices[service] = priceInput.value;
                }
            });
            formData.set('servicePrices', JSON.stringify(servicePrices));
            // Disponibilidade
            const dias = Array.from(document.querySelectorAll('input[name="dias"]:checked')).map(cb => cb.value);
            const horarioInicio = document.getElementById('horarioInicio').value;
            const horarioFim = document.getElementById('horarioFim').value;
            formData.set('dias', JSON.stringify(dias));
            formData.set('horarioInicio', horarioInicio);
            formData.set('horarioFim', horarioFim);

            const token = localStorage.getItem('token');
            try {
                const response = await fetch('/petsitters/profile', {
                    method: 'PUT',
                    headers: token ? { 'Authorization': 'Bearer ' + token } : {},
                    body: formData
                });
                if (response.ok) {
                    alert('Perfil criado com sucesso!');
                    form.reset();
                    servicePricesDiv.innerHTML = '';
                } else {
                    const error = await response.text();
                    alert('Error: ' + error);
                }
            } catch (err) {
                alert('Error: ' + err);
            }
        });
    }
});
