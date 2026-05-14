const btnLogout = document.getElementById("btnLogout");

if (btnLogout) {
  btnLogout.addEventListener("click", async () => {
    const token = localStorage.getItem("token");

    try {
      await fetch(`${BASE_URL}/auth/logout`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

    } catch (err) {
      console.error(err);
    }

    localStorage.removeItem("token");

    window.location.href = "login.html";
  });
}