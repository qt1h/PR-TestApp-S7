function addItem() {
  const list = document.getElementById("dynamic-list");
  const input = document.getElementById("testSteps_input");
  const value = input.value.trim();

  if (value !== "") {
    const listItem = document.createElement("li");

    // Ajouter le drag handle
    const handle = document.createElement("div");
    handle.className = "drag-handle";
    handle.innerHTML = '<i class="fa-solid fa-sort"></i>';
    listItem.appendChild(handle);

    // Utilise un <span> pour contenir le texte de l'étape
    const span = document.createElement("span");
    span.textContent = value;
    listItem.appendChild(span);

    // Bouton pour supprimer l'étape
    const button = document.createElement("button");
    button.type = "button";
    button.className = "cta-btn small";
    button.innerHTML = '<i class="fa-solid fa-trash"></i>';
    button.onclick = function () {
      removeItem(button);
    };
    listItem.appendChild(button);

    list.appendChild(listItem);
    input.value = ""; // Réinitialise le champ de saisie
  }
}

// Listener pour la touche "Entrée"
document
  .getElementById("testSteps_input")
  .addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
      event.preventDefault(); // Empêche la soumission du formulaire
      document.getElementById("addIt").click();
    }
  });

// Listener pour la touche "Delete" (Ctrl + Backspace)
document
  .getElementById("testSteps_input")
  .addEventListener("keydown", function (event) {
    if (event.key === "Backspace" && event.ctrlKey) {
      event.preventDefault();
      const list = document.getElementById("dynamic-list");
      const lastItem = list.lastElementChild;
      if (lastItem) {
        const deleteButton = lastItem.querySelector("button");
        if (deleteButton) {
          deleteButton.click();
        }
      }
    }
  });

// Fonction pour supprimer un élément de la liste
function removeItem(button) {
  const listItem = button.parentElement;
  listItem.remove();
}

// Fonction qui prépare le JSON à partir de la liste d'étapes
function prepareSteps() {
  const steps = [];
  const listItems = document.querySelectorAll("#dynamic-list li");
  listItems.forEach((li) => {
    const span = li.querySelector("span");
    if (span) {
      steps.push(span.textContent.trim());
    }
  });
  // Encode le tableau d'étapes en JSON et l'affecte au champ caché
  document.getElementById("testStepsJson").value = JSON.stringify(steps);
}

// Fonction qui valide le formulaire; retourne true si tout est OK, sinon false
function validateForm() {
  const list = document.getElementById("dynamic-list");
  if (list.children.length === 0) {
    alert("Please add at least one step before submitting the form.");
    return false;
  }
  const nameField = document.getElementById("testName");
  nameField.value = nameField.value.trim();
  if (nameField.value === "") {
    alert("Input cannot be empty or only spaces.");
    return false;
  }
  const descriptionField = document.getElementById("description");
  descriptionField.value = descriptionField.value.trim();
  return true;
}

// Fonction unique appelée lors de la soumission du formulaire
function handleSubmit(event) {
  // Prépare la liste des étapes en JSON
  prepareSteps();
  // Valide le formulaire et empêche la soumission en cas d'erreur
  if (!validateForm()) {
    event.preventDefault();
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const dynamicList = document.getElementById("dynamic-list");
  if (dynamicList) {
    new Sortable(dynamicList, {
      animation: 150,
      handle: ".drag-handle", // permet de drag uniquement via le handle
    });
  }
});
