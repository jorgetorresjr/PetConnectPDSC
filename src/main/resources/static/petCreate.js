document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('petCreateForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    const token = localStorage.getItem('token');
    try {
      const response = await fetch('/pets', {
        method: 'POST',
        headers: token ? { 'Authorization': 'Bearer ' + token } : {},
        body: formData
      });
      if (response.ok) {
        alert('Pet criado com sucesso!');
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