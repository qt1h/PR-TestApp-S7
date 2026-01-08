document.addEventListener("DOMContentLoaded", function () {
  // Récupération des éléments principaux
  const editButton = document.getElementById("edit-button");
  //const addTestButton = document.getElementById("add-test");
  const saveButton = document.getElementById("save-button");
  const availableTestsContainer = document.getElementById("available-tests-container");
  const nameView = document.querySelector(".name-view");
  const nameEdit = document.querySelector(".name-edit");
  const descriptionView = document.querySelector(".description-view");
  const descriptionEdit = document.querySelector(".description-edit");
  const testViews = document.querySelectorAll(".test-view");
  const testEdits = document.querySelectorAll(".test-edit");
  const deleteTestButtons = document.querySelectorAll(".delete-test");

  let isEditing = false;
  let hasChanges = false;
  let sortable = null;

  // Initialisation de Sortable.js (si disponible)
  const testsList = document.getElementById("tests-list");
  if (testsList && typeof Sortable !== "undefined") {
    sortable = new Sortable(testsList, {
      animation: 150,
      disabled: true,
      handle: ".drag-handle",
      onEnd: function () {
        hasChanges = true;
      }
    });
  }

  // Masquer les éléments d'édition par défaut
  //addTestButton.style.display = "none";
  saveButton.style.display = "none";
  nameEdit.style.display = "none";
  descriptionEdit.style.display = "none";
  availableTestsContainer.style.display = "none";
  deleteTestButtons.forEach(button => button.style.display = "none");

  // Gestion du passage en mode édition / lecture
  editButton.addEventListener("click", function () {
    if (hasChanges) {
      alert("You made changes and must save before continuing.");
      return;
    }
    isEditing = !isEditing;
    if (isEditing) {
      // Passage en mode édition
      nameView.style.display = "none";
      nameEdit.style.display = "flex";
      descriptionView.style.display = "none";
      descriptionEdit.style.display = "flex";

      testViews.forEach(view => view.style.display = "none");
      testEdits.forEach(edit => edit.style.display = "flex");

      deleteTestButtons.forEach(button => button.style.display = "flex");
      //addTestButton.style.display = "flex";
      saveButton.style.display = "flex";
      availableTestsContainer.style.display = "flex";
      document.querySelectorAll(".drag-handle").forEach(h => h.style.display = "inline-block");

      if (sortable) {
        sortable.option("disabled", false);
      }
      toggleAvailableTestsPanel(true);
      initAvailableTestButtons();
    } else {
      // Retour en mode lecture
      nameView.style.display = "flex";
      nameEdit.style.display = "none";
      descriptionView.style.display = "flex";
      descriptionEdit.style.display = "none";

      testViews.forEach(view => view.style.display = "flex");
      testEdits.forEach(edit => edit.style.display = "none");

      deleteTestButtons.forEach(button => button.style.display = "none");
      //addTestButton.style.display = "none";
      saveButton.style.display = "none";
      availableTestsContainer.style.display = "none";
      document.querySelectorAll(".drag-handle").forEach(h => h.style.display = "none");

      if (sortable) {
        sortable.option("disabled", true);
      }
      toggleAvailableTestsPanel(false);
    }
  });

  // Détecter les changements dans les champs d'édition
  nameEdit.addEventListener("input", () => hasChanges = true);
  descriptionEdit.addEventListener("input", () => hasChanges = true);

  // Attacher un événement de suppression sur chaque bouton de suppression existant
  deleteTestButtons.forEach(button => {
    button.addEventListener("click", function () {
      const li = this.closest(".test-item");
      const testId = li.querySelector(".test-edit").value;
      // Récupération du nom à partir du lien
      const testName = li.querySelector("a span").textContent;
      li.remove();
      hasChanges = true;
      addTestToAvailableTests(testId, testName);
    });
  });

  // Sauvegarde : validation avant soumission du formulaire
  saveButton.addEventListener("click", function (event) {
    if (isEditing) {
      const list = document.getElementById("tests-list");
      const items = list.querySelectorAll(".test-edit");
      let hasEmptyTest = false;
      items.forEach(input => {
        const trimmed = input.value.trim();
        input.value = trimmed;
        if (!trimmed) {
          hasEmptyTest = true;
        }
      });
      if (hasEmptyTest) {
        alert("No test can be empty");
        event.preventDefault();
        return;
      }
      // Fin du mode édition
      hasChanges = false;
      isEditing = false;

      nameView.style.display = "flex";
      nameEdit.style.display = "none";
      descriptionView.style.display = "flex";
      descriptionEdit.style.display = "none";
      testViews.forEach(view => view.style.display = "flex");
      testEdits.forEach(edit => edit.style.display = "none");
      deleteTestButtons.forEach(button => button.style.display = "none");
      //addTestButton.style.display = "none";
      saveButton.style.display = "none";
      availableTestsContainer.style.display = "none";
      document.querySelectorAll(".drag-handle").forEach(h => h.style.display = "none");

      toggleAvailableTestsPanel(false);
      if (sortable) {
        sortable.option("disabled", true);
      }
    }
  });

  // Fonction pour basculer l'affichage du panneau "Available Tests"
  function toggleAvailableTestsPanel(show) {
    const panel = document.getElementById("available-tests");
    if (panel) {
      panel.style.display = show ? "block" : "none";
    }
  }

  // Initialise les boutons disponibles dans le panneau dès le chargement
  function initAvailableTestButtons() {
    const buttons = document.querySelectorAll(".add-available-test-btn");
    buttons.forEach(btn => {
      btn.addEventListener("click", () => {
        const testId = btn.getAttribute("data-id");
        const testName = btn.getAttribute("data-name");
        addTestToSuite(testId, testName);
        btn.style.display = "none";
        hasChanges = true;
      });
    });
  }

  // Ajoute un test au panneau disponible s'il n'est pas déjà présent
  function addTestToAvailableTests(testId, testName) {
    const panel = document.getElementById("available-tests");
    if (!panel) return;
    const existingButton = panel.querySelector(`button[data-id="${testId}"]`);
    if (existingButton) {
      // Si le bouton existe, on le rend visible (s'il était caché)
      if (existingButton.style.display === "none") {
        existingButton.style.display = "inline-block";
      }
      return;
    }
    const ul = panel.querySelector("ul");
    const li = document.createElement("li");
    li.innerHTML = `
      <button type="button" class="add-available-test-btn" data-id="${testId}" data-name="${testName}">
        <i class="fa-solid fa-plus"></i>
        <span>${testName}</span>
      </button>`;
    ul.appendChild(li);
    li.querySelector(".add-available-test-btn").addEventListener("click", function () {
      addTestToSuite(testId, testName);
      this.style.display = "none";
    });
  }

  // Ajoute un test à la suite en mode édition
  function addTestToSuite(testId, testName) {
    const testList = document.getElementById("tests-list");
    if (!testList) return;
    const li = document.createElement("li");
    li.classList.add("test-item");
    li.innerHTML = `
      <div class="drag-handle" style="display: inline-block;">
        <i class="fa-solid fa-sort"></i>
      </div>
      <div class="test-details">
        <div class="test-info">
          <label>
            Test:
            <span class="test-view">${testName}</span>
            <input type="hidden" class="test-edit" name="testIds" value="${testId}" required />
            <a href="/test?id=${testId}"><span>${testName}</span></a>
          </label>
        </div>
      </div>
      <div>
        <button type="button" class="delete-test btn-danger small">
          <i class="fa-solid fa-trash"></i>
        </button>
      </div>
    `;
    testList.appendChild(li);
    li.querySelector(".delete-test").addEventListener("click", function () {
      li.remove();
      hasChanges = true;
      addTestToAvailableTests(testId, testName);
    });
    hasChanges = true;
  }
});
document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("input");
  const testItems = document.querySelectorAll(".test-item");
  const resultsCount = document.getElementById("resultsCount");

  resultsCount.style.display = "none";

  searchInput.addEventListener("input", function () {
    const searchTerm = searchInput.value.trim().toLowerCase();
    const searchWords = searchTerm.split(/\s+/);
    let visibleCount = 0;

    testItems.forEach((item) => {
      const nameElement = item.querySelector(".name");
      const descriptionElement = item.querySelector(".description");

      const originalName = nameElement.dataset.name || nameElement.textContent;
      const originalDescription = descriptionElement.dataset.description || descriptionElement.textContent;

      nameElement.innerHTML = originalName;
      descriptionElement.innerHTML = originalDescription;

      const nameMatches = searchWords.every((word) => originalName.toLowerCase().includes(word));
      const descriptionMatches = searchWords.every((word) => originalDescription.toLowerCase().includes(word));

      if (nameMatches || descriptionMatches) {
        item.style.display = "flex";
        visibleCount++;

        if (searchTerm) {
          const regex = new RegExp(`(${searchWords.join("|")})`, "gi");
          nameElement.innerHTML = originalName.replace(regex, (match) => `<span class='highlight'>${match}</span>`);
          descriptionElement.innerHTML = originalDescription.replace(regex, (match) => `<span class='highlight'>${match}</span>`);
        }
      } else {
        item.style.display = "none";
      }
    });

    // Mettre à jour le nombre de résultats
    if (searchTerm) {
      resultsCount.style.display = "inline-block";
      resultsCount.textContent =
          visibleCount > 0 ? `${visibleCount} step(s) found.` : "No step found.";
    } else {
      resultsCount.style.display = "none";
    }
  });
});
