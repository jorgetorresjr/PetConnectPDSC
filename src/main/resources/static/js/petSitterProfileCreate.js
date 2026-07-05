document.addEventListener('DOMContentLoaded', async function () {
    const servicesCheckboxesDiv = document.getElementById('servicesCheckboxes');
    const servicePricesDiv = document.getElementById('servicePrices');
    if (servicesCheckboxesDiv && servicePricesDiv) {
        servicesCheckboxesDiv.querySelectorAll('input[type="checkbox"]').forEach(cb => {
            cb.addEventListener('change', function () {
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
                    input.max = 999999.99;
                    input.step = 0.01;
                    input.required = true;
                    input.placeholder = 'R$ 0.00';

                    // Validação em tempo real
                    input.addEventListener('input', function() {
                        if (this.value && parseFloat(this.value) > 999999.99) {
                            this.value = 999999.99;
                        }
                    });

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

    const MAX_PHOTO_SIZE = 2 * 1024 * 1024; // 2MB
    const token = localStorage.getItem('token');
    const previewImage = document.getElementById('photoPreview');
    const photoInput = document.getElementById('photo');
    const photoError = document.getElementById('photoError');

    const showPhotoError = message => {
        if (photoError) {
            photoError.textContent = message;
            photoError.style.display = message ? 'block' : 'none';
        }
    };

    if (photoInput && previewImage) {
        photoInput.addEventListener('change', () => {
            if (!photoInput.files || photoInput.files.length === 0) {
                showPhotoError('');
                previewImage.src = '../assets/image.png';
                return;
            }

            const file = photoInput.files[0];
            if (file.size > MAX_PHOTO_SIZE) {
                showPhotoError('A imagem deve ter no máximo 2MB.');
                previewImage.src = '../assets/image.png';
                return;
            }

            showPhotoError('');
            previewImage.src = URL.createObjectURL(file);
        });
    }
    if (token) {
        try {
            const response = await fetch(`${BASE_URL}/petsitters/me`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.ok) {
                const sitter = await response.json();
                document.getElementById('specialty').value = sitter.specialty || '';
                document.getElementById('certificates').value = sitter.certificates || '';

                if (sitter.id && previewImage) {
                    try {
                        const photoResponse = await fetch(`${BASE_URL}/users/${sitter.id}/photo`, {
                            headers: { Authorization: `Bearer ${token}` }
                        });
                        if (photoResponse.ok) {
                            const blob = await photoResponse.blob();
                            previewImage.src = URL.createObjectURL(blob);
                        }
                    } catch (photoErr) {
                        console.warn('Erro ao carregar foto do pet sitter:', photoErr);
                    }
                }

                if (sitter.availability) {
                    const partes = sitter.availability.split('|');
                    if (partes.length === 2) {
                        try {
                            const diasSelecionados = JSON.parse(partes[0]);
                            if (Array.isArray(diasSelecionados)) {
                                diasSelecionados.forEach(dia => {
                                    const checkbox = document.querySelector(`input[name="dias"][value="${dia}"]`);
                                    if (checkbox) checkbox.checked = true;
                                });
                            }
                        } catch (err) {
                            const diasBrutos = partes[0].replace(/[[\]"]+/g, '').split(',');
                            diasBrutos.forEach(dia => {
                                const trimmed = dia.trim();
                                const checkbox = document.querySelector(`input[name="dias"][value="${trimmed}"]`);
                                if (checkbox) checkbox.checked = true;
                            });
                        }

                        const horarios = partes[1].split('-');
                        if (horarios.length === 2) {
                            document.getElementById('horarioInicio').value = horarios[0] || '';
                            document.getElementById('horarioFim').value = horarios[1] || '';
                        }
                    }
                }

                if (sitter.servicePrices) {
                    try {
                        const prices = JSON.parse(sitter.servicePrices);
                        Object.entries(prices).forEach(([nomeServico, preco]) => {
                            const checkbox = document.querySelector(`#servicesCheckboxes input[data-name='${nomeServico}']`);
                            if (checkbox) {
                                checkbox.checked = true;
                                checkbox.dispatchEvent(new Event('change'));
                                const priceInput = document.getElementById(`preco_${nomeServico}`);
                                if (priceInput) {
                                    priceInput.value = preco;
                                }
                            }
                        });
                    } catch (err) {
                        console.error('Erro ao carregar preços do pet sitter:', err);
                    }
                }
            }
        } catch (err) {
            console.error('Erro ao recuperar perfil do pet sitter:', err);
        }
    }

    const form = document.getElementById('petSitterProfileForm');
    if (form) {
        form.addEventListener('submit', async function (e) {
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

                if (photoInput && photoInput.files && photoInput.files.length > 0 && photoInput.files[0].size > MAX_PHOTO_SIZE) {
                showPhotoError('A imagem deve ter no máximo 2MB.');
                return;
            }

            if (response.ok) {
                    alert('Dados salvos com sucesso!');
                    //form.reset();
                   // servicePricesDiv.innerHTML = '';
                     window.location.href = '../html/petSitterHome.html';
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