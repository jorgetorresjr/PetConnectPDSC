document.addEventListener('DOMContentLoaded', function() {
  const btn = document.getElementById('criarPerfilBtn');
  if (btn) {
    btn.addEventListener('click', function() {
      window.location.href = 'petSitterProfileCreate.html';
    });
  }

  // Garante que o botão de logout funcione
  if (typeof setupLogoutButton === 'function') setupLogoutButton();
});