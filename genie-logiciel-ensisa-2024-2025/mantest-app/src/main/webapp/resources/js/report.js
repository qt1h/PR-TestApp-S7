function applyFilters() {
    let searchTerm = document.getElementById("searchInput").value.trim().toLowerCase();
    let searchTerms = searchTerm ? searchTerm.split(/\s+/) : [];

    let modificationStartDateInput = document.getElementById("modificationStartDate");
    let modificationEndDateInput = document.getElementById("modificationEndDate");
    let modificationStartDate = modificationStartDateInput.value ? new Date(modificationStartDateInput.value) : null;
    let modificationEndDate = modificationEndDateInput.value ? new Date(modificationEndDateInput.value) : null;

    if (modificationStartDate && modificationEndDate && modificationStartDate > modificationEndDate) {
        alert("The start date cannot be later than the end date.");
        return;
    }

    let testItems = document.querySelectorAll(".test-item");
    let hasVisibleResults = false;

    testItems.forEach(testItem => {
        let testNameElement = testItem.querySelector(".name");
        let testName = testNameElement.textContent.toLowerCase();

        let testDescriptionElement = testItem.querySelector(".description");
        let testDescription = testDescriptionElement ? testDescriptionElement.textContent.toLowerCase() : "";

        let latestExecutionElement = testItem.querySelector(".execution");
        let latestExecutionDate = latestExecutionElement ? new Date(latestExecutionElement.textContent.trim().split(" ")[0]) : null;

        let matchesSearchTerm = searchTerms.length === 0 || searchTerms.some(term =>
            testName.includes(term) || testDescription.includes(term)
        );

        let matchesLatestExecutionDate = true;

        if (latestExecutionDate) {
            if (modificationStartDate && latestExecutionDate < modificationStartDate) {
                matchesLatestExecutionDate = false;
            }
            if (modificationEndDate && latestExecutionDate > modificationEndDate) {
                matchesLatestExecutionDate = false;
            }
        }

        if (matchesSearchTerm && matchesLatestExecutionDate) {
            testItem.style.display = "flex";
            hasVisibleResults = true;

            testNameElement.innerHTML = testNameElement.textContent;
            testDescriptionElement.innerHTML = testDescriptionElement.textContent;

            if (searchTerms.length > 0) {
                searchTerms.forEach(term => {
                    if (term) {
                        let regex = new RegExp(`(${term})`, "gi");
                        testNameElement.innerHTML = testNameElement.innerHTML.replace(regex, "<span class='highlight active'>$1</span>");
                        testDescriptionElement.innerHTML = testDescriptionElement.innerHTML.replace(regex, "<span class='highlight active'>$1</span>");
                    }
                });

                setTimeout(() => {
                    testItem.querySelectorAll(".highlight.active").forEach(el => el.classList.remove("active"));
                }, 500);
            }
        } else {
            testItem.style.display = "none";
            testNameElement.innerHTML = testNameElement.textContent;
            testDescriptionElement.innerHTML = testDescriptionElement.textContent;
        }
    });

    let noResultsMessage = document.getElementById("noResultsMessage");
    if (!hasVisibleResults) {
        if (!noResultsMessage) {
            noResultsMessage = document.createElement("p");
            noResultsMessage.id = "noResultsMessage";
            noResultsMessage.textContent = "No tests match the filter criteria.";
            document.querySelector("main").appendChild(noResultsMessage);
        }
    } else {
        noResultsMessage?.remove();
    }
}

document.getElementById("resetFilters").addEventListener("click", () => {
    document.getElementById("modificationStartDate").value = "";
    document.getElementById("modificationEndDate").value = "";
    document.getElementById("searchInput").value = "";

    document.querySelectorAll(".test-item").forEach(testItem => {
        testItem.style.display = "flex";
        let testNameElement = testItem.querySelector(".name");
        let testDescriptionElement = testItem.querySelector(".description");

        if (testNameElement) testNameElement.innerHTML = testNameElement.textContent;
        if (testDescriptionElement) testDescriptionElement.innerHTML = testDescriptionElement.textContent;
    });

    document.getElementById("noResultsMessage")?.remove();
});

document.getElementById("applyFilters").addEventListener("click", applyFilters);
document.getElementById("searchInput").addEventListener("input", applyFilters);
