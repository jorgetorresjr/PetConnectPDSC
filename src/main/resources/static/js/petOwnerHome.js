document.addEventListener('DOMContentLoaded', async function() {
  const BASE_URL = "http://localhost:8080";
  const token = localStorage.getItem("token");

  const criarPerfilBtn = document.getElementById('criarPerfilBtn');
  const verPerfilBtn = document.getElementById('verPerfilBtn');
  const verPetPerfilBtn = document.getElementById('verPetPerfilBtn');
  const petsLista = document.getElementById('petsLista');
  const cadastrarPetBtn = document.getElementById('cadastrarPetBtn');
  const buscarPetSitterBtn = document.getElementById("buscarPetSitterBtn");

  if (criarPerfilBtn) {
    criarPerfilBtn.onclick = function() {
      window.location.href = 'petOwnerProfileCreate.html';
    };
  }

  if (verPerfilBtn) {
    verPerfilBtn.onclick = function() {
      const perfilId = verPerfilBtn.getAttribute("data-id");
      if (perfilId) {
        window.location.href = "petOwnerProfile.html?id=" + encodeURIComponent(perfilId);
      }
    };
  }

  if (verPetPerfilBtn) {
    verPetPerfilBtn.onclick = function() {
      const petId = verPetPerfilBtn.getAttribute("data-id");
      if (petId) {
        window.location.href = "petProfile.html?id=" + encodeURIComponent(petId);
      }
    };
  }

  if (cadastrarPetBtn) {
    cadastrarPetBtn.onclick = function() {
      window.location.href = 'petCreate.html';
    };
  }

  if (buscarPetSitterBtn) {
    buscarPetSitterBtn.onclick = function() {
      window.location.href = "petSitterSearch.html";
    };
  }

  if (token) {
    try {
      const perfilRes = await fetch(BASE_URL + "/petowners/me", {
        headers: { Authorization: "Bearer " + token }
      });

      if (perfilRes.ok) {
        const perfil = await perfilRes.json();
        if (criarPerfilBtn) criarPerfilBtn.style.display = "none";
        if (verPerfilBtn) {
          verPerfilBtn.style.display = "inline-block";
          verPerfilBtn.setAttribute("data-id", perfil.id);
        }
      }
    } catch (e) {
      // mantém o layout atual se falhar
    }

    try {
      const petsRes = await fetch(BASE_URL + "/pets/my", {
        headers: { Authorization: "Bearer " + token }
      });

      if (petsRes.ok) {
        const pets = await petsRes.json();
        if (petsLista) {
          petsLista.innerHTML = "";

          if (Array.isArray(pets) && pets.length > 0) {
            pets.forEach(function (pet) {
              if (!pet || pet.id == null) {
                return;
              }

              const btnPet = document.createElement("button");
              btnPet.type = "button";
              btnPet.textContent = "Ver perfil de " + (pet.name || ("Pet #" + pet.id));
              btnPet.addEventListener("click", function () {
                window.location.href = "petProfile.html?id=" + encodeURIComponent(pet.id);
              });

              petsLista.appendChild(btnPet);
            });
          } else {
            petsLista.innerHTML = "<p>Você ainda não cadastrou pets.</p>";
          }
        }
      }
    } catch (e) {
      // mantém a tela funcional se a busca de pets falhar
    }
  }

  if (typeof setupLogoutButton === 'function') setupLogoutButton();
});