document.addEventListener('DOMContentLoaded', async function() {
  const btn = document.getElementById('criarPerfilBtn');
  const token = localStorage.getItem('token');
  if (!btn || !token) return;

  // Busca dados do perfil do PetSitter autenticado
  try {
    const response = await fetch('/petsitters/me', {
      headers: { 'Authorization': 'Bearer ' + token }
    });
    if (response.ok) {
      const sitter = await response.json();
      if (sitter.specialty || sitter.availability) {
        btn.style.display = 'none';
      }
    }
  } catch (err) {
    // Se der erro, não esconde o botão
  }
});