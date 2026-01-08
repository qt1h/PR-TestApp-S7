// Search pour test List

document.addEventListener("DOMContentLoaded", function() {
  const searchInput = document.getElementById("input");
  const testItems = document.querySelectorAll(".test-item");
  const resultsCount = document.getElementById("resultsCount");

  searchInput.addEventListener("input", function() {
    const searchTerm = searchInput.value.trim().toLowerCase();
    const searchWords = searchTerm.split(/\s+/);
    let visibleCount = 0;

    testItems.forEach(item => {
      const nameElement = item.querySelector(".name");
      const descriptionElement = item.querySelector(".description");
      const originalName = nameElement.textContent;
      const originalDecription = descriptionElement.textContent;
      nameElement.innerHTML = originalName;
      descriptionElement.innerHTML = originalDecription;

      // Réinitialiser le contenu HTML avant chaque nouvelle recherche
      nameElement.innerHTML = nameElement.textContent;
      descriptionElement.innerHTML = descriptionElement.textContent;

      // Vérification de la correspondance partielle avec le texte
      const nameMatches = searchWords.every(word => originalName.toLowerCase().includes(word));
      const descriptionMatches = searchWords.every(word => originalDecription.toLowerCase().includes(word));

      if (nameMatches || descriptionMatches) {
        item.style.display = "flex"; // Afficher l'élément
        visibleCount++;

        // Mettre en surbrillance le tbexte correspondant en respectant les limites des mots
        if (searchTerm) {
          let regex = new RegExp(`(${searchWords.join("|")})`, "gi");

          nameElement.innerHTML = originalName.replace(regex, match => `<span class='highlight'>${match}</span>`);
          descriptionElement.innerHTML = originalDecription.replace(regex, match => `<span class='highlight'>${match}</span>`);
        }
      } else {
        item.style.display = "none"; // Masquer l'élément
      }
    });

    // Mettre à jour le nombre de résultats
    if (searchTerm === "") {
      resultsCount.textContent = "";
    } else if (visibleCount > 0) {
      resultsCount.textContent = `${visibleCount} result(s) found.`;
    } else {
      resultsCount.textContent = "No Result found.";
    }
  });
});
