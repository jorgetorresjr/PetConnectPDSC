document.addEventListener('DOMContentLoaded', function() {
  // Adicione aqui funcionalidades específicas da home do Pet Owner
  const criarPerfilBtn = document.getElementById('criarPerfilBtn');
  if (criarPerfilBtn) {
    criarPerfilBtn.onclick = function() {
      window.location.href = 'petOwnerProfileCreate.html';
    };
  }
  const cadastrarPetBtn = document.getElementById('cadastrarPetBtn');
  if (cadastrarPetBtn) {
    cadastrarPetBtn.onclick = function() {
      window.location.href = 'petCreate.html';
    };
  }

  // Garante que o botão de logout funcione em todos os contextos
  if (typeof setupLogoutButton === 'function') setupLogoutButton();
});