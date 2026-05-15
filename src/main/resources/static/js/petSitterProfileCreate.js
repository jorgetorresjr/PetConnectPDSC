document.addEventListener('DOMContentLoaded', function() {
    const servicesCheckboxesDiv = document.getElementById('servicesCheckboxes');
    const servicePricesDiv = document.getElementById('servicePrices');
    if (servicesCheckboxesDiv && servicePricesDiv) {
        servicesCheckboxesDiv.querySelectorAll('input[type="checkbox"]').forEach(cb => {
            cb.addEventListener('change', function() {
                const serviceName = cb.getAttribute('data-name');
                const priceFieldId = `preco_field_${serviceName}`;
                
                if (cb.checked) {
                    const label = document.createElement('label');
                    label.textContent = `Preço para ${serviceName}:`;
                    label.setAttribute('for', `preco_${serviceName}`);
                    label.id = `label_${serviceName}`;
                    
                    const input = document.createElement('input');
                    input.type = 'number';
                    input.name = `preco_${serviceName}`;
                    input.id = `preco_${serviceName}`;
                    input.min = 0;
                    input.step = 0.01;
                    input.required = true;
                    
                    const div = document.createElement('div');
                    div.id = priceFieldId;
                    div.appendChild(label);
                    div.appendChild(input);
                    servicePricesDiv.appendChild(div);
                } else {
                    const div = document.getElementById(priceFieldId);
                    if (div) servicePricesDiv.removeChild(div);
                }
            });
        });
    }

    if (typeof setupLogoutButton === 'function') setupLogoutButton();

    const form = document.getElementById('petSitterProfileForm');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            const formData = new FormData(form);
            
            const cepInput = form.querySelector('[name="cep"], [id*="cep"]');
            if (cepInput) {
                let rawCep = cepInput.value.replace(/\D/g, "");
                if (rawCep.length === 8) rawCep = rawCep.replace(/(\d{5})(\d{3})/, "$1-$2");
                formData.set(cepInput.name || 'cep', rawCep);
            }

            const selectedCheckboxes = Array.from(document.querySelectorAll('#servicesCheckboxes input[type="checkbox"]:checked'));

            // Correção: enviando como 'servicesIds' para o Java entender
            selectedCheckboxes.forEach(cb => {
                formData.append('servicesIds', cb.value); 
            });

            const servicePrices = {};
            selectedCheckboxes.forEach(cb => {
                const serviceName = cb.getAttribute('data-name');
                const priceInput = form.querySelector(`[name='preco_${serviceName}']`);
                if (priceInput) {
                    servicePrices[serviceName] = priceInput.value;
                }
            });
            formData.set('servicePrices', JSON.stringify(servicePrices));

            const dias = Array.from(document.querySelectorAll('input[name="dias"]:checked')).map(cb => cb.value);
            const horarioInicio = document.getElementById('horarioInicio').value;
            const horarioFim = document.getElementById('horarioFim').value;
            formData.set('dias', JSON.stringify(dias));
            formData.set('horarioInicio', horarioInicio);
            formData.set('horarioFim', horarioFim);

            const token = localStorage.getItem('token');
            try {
                // Forçando a URL absoluta para evitar erro de rota no navegador
                const response = await fetch('http://localhost:8080/petsitters/profile', {
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
                    alert('Atenção:\n' + error);
                }
            } catch (err) {
                alert('Erro de conexão com o servidor.');
            }
        });
    }
});