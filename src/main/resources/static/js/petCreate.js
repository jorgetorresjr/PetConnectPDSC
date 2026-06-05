const BASE_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("petCreateForm").addEventListener("submit", async function (e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const token = localStorage.getItem("token");

    try {
      const response = await fetch(`${BASE_URL}/pets`, {
        method: "POST",
        headers: token ? { Authorization: "Bearer " + token } : {},
        body: formData
      });

      if (response.ok) {
        alert("Pet criado com sucesso!");
        form.reset();
        window.location.href = "petOwnerHome.html";
      } else {
        const error = await response.text();
        alert("Error: " + error);
      }
    } catch (err) {
      alert("Error: " + err);
    }
  });

  if (typeof setupLogoutButton === "function") setupLogoutButton();
});