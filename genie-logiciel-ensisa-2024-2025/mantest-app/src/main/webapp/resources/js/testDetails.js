document.addEventListener("DOMContentLoaded", function () {
  const editButton = document.getElementById("edit-button");
  const addStepButton = document.getElementById("add-step");
  const nameView = document.querySelector(".name-view");
  const nameEdit = document.querySelector(".name-edit");
  const descriptionView = document.querySelector(".description-view");
  const descriptionEdit = document.querySelector(".description-edit");
  const stepViews = document.querySelectorAll(".step-view");
  const stepEdits = document.querySelectorAll(".step-edit");
  const deleteStep = document.querySelectorAll(".delete-step");
  const saveButton = document.getElementById("save-button");

  // Initialisation de l'état d'édition et des flags de modification
  let isEditing = false;
  let hasChanges = false;
  let sortable = null;

  const stepList = document.getElementById("steps-list");
  if (stepList != null) {
    sortable = new Sortable(stepList, {
      animation: 150,
      disabled: true, // désactivé par défaut
      handle: ".drag-handle",
      onEnd: function () {
        hasChanges = true; // détecte le changement d’ordre
      },
    });
  }

  // Masquer les champs d'édition par défaut
  addStepButton.style.display = "none";
  saveButton.style.display = "none";
  nameEdit.style.display = "none";
  descriptionEdit.style.display = "none";
  stepEdits.forEach((edit) => (edit.style.display = "none"));
  deleteStep.forEach((button) => (button.style.display = "none"));

  // Toggle du mode édition
  editButton.addEventListener("click", function () {
    // Ne pas permettre de revenir en arrière si des modifications ont été faites
    if (hasChanges) {
      alert("You made changes and must save before continuing.");
      return;
    }

    isEditing = !isEditing; // Inverse l'état d'édition
    document.querySelectorAll(".drag-handle").forEach(h => h.style.display = isEditing ? "inline-block" : "none");

    if (isEditing) {
      // Activer le mode édition
      nameView.style.display = "none";
      nameEdit.style.display = "flex";
      descriptionView.style.display = "none";
      descriptionEdit.style.display = "flex";
      if (sortable) sortable.option("disabled", false);
      document
        .querySelectorAll(".drag-handle")
        .forEach((h) => (h.style.display = "inline-block"));

      stepViews.forEach((view) => (view.style.display = "none"));
      stepEdits.forEach((edit) => (edit.style.display = "flex"));

      // Afficher les boutons Delete et Add Step
      deleteStep.forEach((button) => (button.style.display = "flex"));
      addStepButton.style.display = "flex";
      saveButton.style.display = "flex";
    } else {
      // Désactiver le mode édition (seulement si pas de modifications)
      nameView.style.display = "flex";
      nameEdit.style.display = "none";
      descriptionView.style.display = "flex";
      descriptionEdit.style.display = "none";
      if (sortable) sortable.option("disabled", true);
      document
        .querySelectorAll(".drag-handle")
        .forEach((h) => (h.style.display = "none"));

      stepViews.forEach((view) => (view.style.display = "flex"));
      stepEdits.forEach((edit) => (edit.style.display = "none"));

      // Cacher les boutons Delete et Add Step
      deleteStep.forEach((button) => (button.style.display = "none"));
      addStepButton.style.display = "none";
      saveButton.style.display = "none";
    }
  });

  // Détecter les changements dans le champ de description
  descriptionEdit.addEventListener("input", function () {
    hasChanges = true;
  });

  // Détecter les changements dans le champ de nom
  nameEdit.addEventListener("input", function () {
    hasChanges = true;
  });

  // Ajout d'une nouvelle étape (en mode édition)
  addStepButton.addEventListener("click", function () {
    const newStep = document.createElement("li");
    newStep.classList.add("step-item");
    newStep.innerHTML = `
      <div class="step-details">
        <div class="drag-handle" style="display: ${isEditing ? "inline-block" : "none"};">
          <i class="fa-solid fa-sort"></i>
        </div>
        <div class="step-info">
          <label>Step:
            <span class="step-view" style="display: none;"></span>
            <input type="text" maxlength="128" class="step-edit form-control" name="steps" value="" required>
          </label>
        </div>
        <button type="button" class="delete-step btn-danger small" style="display: ${isEditing ? "flex" : "none"};">
            <i class="fa-solid fa-trash"></i>
        </button>
      </div>`;
  
    stepList.appendChild(newStep);
  
    // Écouteur suppression
    newStep.querySelector(".delete-step").addEventListener("click", function () {
      stepList.removeChild(newStep);
      hasChanges = true;
    });
  
    hasChanges = true;
  });
  

  // Suppression d'une étape existante
  deleteStep.forEach((button) => {
    button.addEventListener("click", function () {
      const li = this.closest(".step-item");
      li.parentElement.removeChild(li);

      // Marquer un changement dès qu'on supprime une étape
      hasChanges = true;
    });
  });

  // Sauvegarde des modifications avec validation
  saveButton.addEventListener("click", function (event) {
    if (isEditing) {
      // Vérification des étapes vides avant de fermer le mode édition
      const list = document.getElementById("steps-list");
      const items = list.querySelectorAll(".step-edit");
      let hasEmptyStep = false;

      items.forEach((input) => {
        let trimmedText = input.value.trim();
        input.value = trimmedText;
        if (trimmedText.length === 0) {
          hasEmptyStep = true;
        }
      });

      if (hasEmptyStep) {
        alert("No step can be empty");
        event.preventDefault(); // Empêche la soumission du formulaire
        return; // Stoppe la fermeture du mode édition
      }

      // Si tout est bon, fermeture du mode édition
      hasChanges = false;
      isEditing = false;

      // Désactiver le mode édition après la sauvegarde
      nameView.style.display = "flex";
      nameEdit.style.display = "none";
      descriptionView.style.display = "flex";
      descriptionEdit.style.display = "none";

      stepViews.forEach((view) => (view.style.display = "flex"));
      stepEdits.forEach((edit) => (edit.style.display = "none"));

      // Cacher les boutons Delete et Add Step
      deleteStep.forEach((button) => (button.style.display = "none"));
      addStepButton.style.display = "none";
    }
  });
});

function validateTestForm(event) {
  const list = document.getElementById("steps-list");
  const items = list.children;
  if (items.length === 0) {
    event.preventDefault();
    alert("Please add at least one step before sending.");
  }
  for (let item of items) {
    let input = item.querySelector(".step-edit");
    let trimmedText = input.value.trim();
    input.value = trimmedText;
    if (trimmedText.length === 0) {
      event.preventDefault();
      alert("No step can be empty");
    }
  }
  let nameField = document.getElementById("name");
  nameField.value = nameField.value.trim();
  if (nameField.value === "") {
    alert("Name cannot be empty or only spaces.");
    event.preventDefault();
  }
  let descriptionField = document.getElementById("description");
  descriptionField.value = descriptionField.value.trim();
}

document.addEventListener("DOMContentLoaded", function () {
  const selects = document.querySelectorAll(".select-status");

  selects.forEach((select) => {
    updateSelectBackground(select);

    select.addEventListener("change", function () {
      updateSelectBackground(select);
    });
  });

  function updateSelectBackground(select) {
    const selectedValue = select.value.toLowerCase().replace("_", "-");
    select.className =
      "step-status-edit form-control select-status " + selectedValue;
  }
});

// filtre pour les étapes

document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("input");
  const testItems = document.querySelectorAll(".step-item");
  const resultsCount = document.getElementById("resultsCount");

  resultsCount.style.display = "none";

  searchInput.addEventListener("input", function () {
    const searchTerm = searchInput.value.trim().toLowerCase();
    const searchWords = searchTerm.split(/\s+/);
    let visibleCount = 0;

    testItems.forEach((item) => {
      const nameElement = item.querySelector(".step-view");

      // Réinitialiser le contenu HTML avant chaque nouvelle recherche
      const originalText = nameElement.textContent;
      nameElement.innerHTML = originalText;

      // Vérification de la correspondance partielle avec le texte
      const nameMatches = searchWords.every((word) =>
        originalText.toLowerCase().includes(word)
      );

      if (nameMatches) {
        item.style.display = "flex"; // Afficher l'élément
        visibleCount++;

        // Mettre en surbrillance le texte correspondant en respectant les limites des mots
        if (searchTerm) {
          let regex = new RegExp(`(${searchWords.join("|")})`, "gi");
          nameElement.innerHTML = originalText.replace(
            regex,
            (match) => `<span class='highlight'>${match}</span>`
          );
        }
      } else {
        item.style.display = "none"; // Masquer l'élément
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
