document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('petOwnerProfileForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    // Formatar CEP se existir campo de CEP
    const cepInput = form.querySelector('[name="cep"], [id*="cep"]');
    if (cepInput) {
      let rawCep = cepInput.value.replace(/\D/g, "");
      if (rawCep.length === 8) rawCep = rawCep.replace(/(\d{5})(\d{3})/, "$1-$2");
      formData.set(cepInput.name || 'cep', rawCep);
    }
    const token = localStorage.getItem('token');
    try {
      const response = await fetch('/petowners/profile', {
        method: 'PUT',
        headers: token ? { 'Authorization': 'Bearer ' + token } : {},
        body: formData
      });
      if (response.ok) {
        alert('Perfil criado com sucesso!');
        form.reset();
      } else {
        const error = await response.text();
        alert('Error: ' + error);
      }
    } catch (err) {
      alert('Error: ' + err);
    }
  });

  // Garante que o botão de logout funcione
  if (typeof setupLogoutButton === 'function') setupLogoutButton();
});