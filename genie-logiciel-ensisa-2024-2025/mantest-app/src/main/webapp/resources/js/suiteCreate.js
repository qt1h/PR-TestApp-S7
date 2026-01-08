// Clé de stockage pour le formulaire de création de suite
const STORAGE_KEY_SUITE = "suiteCreateFormData";

// Tableau global pour stocker l'ordre des IDs des tests sélectionnés
let orderedTestIds = [];

/**
 * Sauvegarde l'état du formulaire dans localStorage.
 */
function saveFormData() {
  const formData = {
    suiteName: document.getElementById("suiteName").value,
    description: document.getElementById("description").value,
    testIds: orderedTestIds,
  };
  localStorage.setItem(STORAGE_KEY_SUITE, JSON.stringify(formData));
}

/**
 * Restaure l'état du formulaire depuis localStorage.
 */
function loadFormData() {
  const data = localStorage.getItem(STORAGE_KEY_SUITE);
  if (data) {
    const formData = JSON.parse(data);
    document.getElementById("suiteName").value = formData.suiteName || "";
    document.getElementById("description").value = formData.description || "";
    orderedTestIds = formData.testIds || [];
    updateCheckboxesFromOrderedIds();
    updateOrderDisplay();
  }
}

/**
 * Met à jour l'état (coché/décoché) des checkboxes en fonction de orderedTestIds.
 */
function updateCheckboxesFromOrderedIds() {
  document.querySelectorAll(".test-checkbox").forEach(function (checkbox) {
    const id = parseInt(checkbox.value, 10);
    checkbox.checked = orderedTestIds.includes(id);
  });
}

/**
 * Met à jour l'affichage de l'ordre à côté de chaque checkbox cochée.
 */
function updateOrderDisplay() {
  document.querySelectorAll(".test-checkbox").forEach(function (checkbox) {
    const orderDisplay = checkbox.parentElement.querySelector(".order-display");
    const id = parseInt(checkbox.value, 10);
    const index = orderedTestIds.indexOf(id);
    if (checkbox.checked && index !== -1) {
      orderDisplay.textContent = " (" + (index + 1) + ")";
    } else {
      orderDisplay.textContent = "";
    }
  });
}

/**
 * Attache un écouteur sur chaque checkbox pour gérer la mise à jour de orderedTestIds.
 */
function attachCheckboxListeners() {
  document.querySelectorAll(".test-checkbox").forEach(function (checkbox) {
    checkbox.addEventListener("change", function () {
      const id = parseInt(this.value, 10);
      if (this.checked) {
        if (!orderedTestIds.includes(id)) {
          orderedTestIds.push(id);
        }
      } else {
        const index = orderedTestIds.indexOf(id);
        if (index !== -1) {
          orderedTestIds.splice(index, 1);
        }
      }
      updateOrderDisplay();
      saveFormData();
    });
  });
}

/**
 * Au moment de la soumission du formulaire de création de suite.
 * Vérifie qu'au moins un test est sélectionné, met à jour le champ caché et soumet le formulaire.
 */
function handleSuiteSubmit(event) {
  event.preventDefault();
  if (orderedTestIds.length === 0) {
    alert("Please select at least one test.");
    return false;
  }
  document.getElementById("suiteTestsJson").value =
    JSON.stringify(orderedTestIds);
  localStorage.removeItem(STORAGE_KEY_SUITE);
  event.target.submit();
}

/**
 * Ouvre l'iframe pour créer un nouveau test sans effacer les données du formulaire.
 */
function openTestCreateModal() {
  const modal = document.getElementById("testCreateModal");
  modal.style.display = "flex";
}

/**
 * Fermeture de l'iframe de création de test et rafraîchissement du panneau disponible.
 */
function closeTestCreateModal() {
  const modal = document.getElementById("testCreateModal");
  if (modal) {
    modal.style.display = "none";
    updateAvailableTests();
  } else {
    console.error("Test creation modal not found.");
  }
}

/**
 * Calcule le prochain identifiant test disponible en se basant sur les checkboxes et orderedTestIds.
 */
function getNextAvailableTestId() {
  let maxId = 0;
  // Parcourir les checkboxes existantes
  document.querySelectorAll(".test-checkbox").forEach(function (checkbox) {
    const id = parseInt(checkbox.value, 10);
    if (!isNaN(id) && id > maxId) {
      maxId = id;
    }
  });
  // Vérifier également les identifiants déjà sélectionnés
  orderedTestIds.forEach(function (id) {
    if (id > maxId) {
      maxId = id;
    }
  });
  return maxId + 1;
}

/**
 * Fonction appelée par l'iframe de création de test lorsque le test est créé.
 * Si aucun identifiant n'est passé, il est calculé automatiquement.
 */
function testCreated(newTestId) {
  console.log("testCreated() called with newTestId:", newTestId);
  // Si aucun identifiant n'est passé (ou si la valeur est 0/NaN), le calculer automatiquement
  if (typeof newTestId === "undefined" || isNaN(newTestId) || newTestId === 0) {
    newTestId = getNextAvailableTestId();
  }
  if (!orderedTestIds.includes(newTestId)) {
    orderedTestIds.push(newTestId);
    console.log("New test id added to selection:", newTestId);
    updateOrderDisplay();
    saveFormData();
  }
  closeTestCreateModal();
  updateAvailableTests();
}

/**
 * Rafraîchit le panneau des tests disponibles via une requête Ajax.
 */
function updateAvailableTests() {
  const meta = document.querySelector('meta[name="contextPath"]');
  let contextPath = meta ? meta.getAttribute("content") : "";
  // Si contextPath est "/" ou "null", on l'interprète comme une chaîne vide
  if (contextPath === "/" || contextPath === "null") {
    contextPath = "";
  }
  const url =
    contextPath + "/suiteCreate/availableTests?t=" + new Date().getTime();
  console.log("Fetching available tests from URL:", url);
  fetch(url)
    .then((response) => {
      console.log("Response status:", response.status);
      if (!response.ok) {
        throw new Error("Failed to load available tests");
      }
      return response.text();
    })
    .then((html) => {
      console.log("Fetched HTML fragment:", html);
      const container = document.getElementById("available-tests-container");
      if (container) {
        container.innerHTML = html;
        // Réattacher les écouteurs sur les checkboxes du fragment mis à jour
        attachCheckboxListeners();
        updateCheckboxesFromOrderedIds();
        updateOrderDisplay();
      } else {
        console.error("Container 'available-tests-container' not found.");
      }
    })
    .catch((err) => console.error("Error updating available tests:", err));
}

/**
 * Dès que le DOM est chargé, restaurer les données et attacher les écouteurs.
 */
document.addEventListener("DOMContentLoaded", function () {
  console.log("DOM fully loaded. Restoring form data...");
  loadFormData();
  document.getElementById("suiteName").addEventListener("input", saveFormData);
  document
    .getElementById("description")
    .addEventListener("input", saveFormData);
  attachCheckboxListeners();
  console.log("Checkbox listeners attached.");
});

// Exposer la fonction testCreated globalement pour que l'iframe puisse l'appeler
window.testCreated = testCreated;
