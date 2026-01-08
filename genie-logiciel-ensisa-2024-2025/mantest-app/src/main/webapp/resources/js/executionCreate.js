document.addEventListener('DOMContentLoaded', function () {
    let iteratorValue = 0;
    const stepList = document.getElementById('steps-list');
    let steps = Array.from(stepList.children);
    const stepCount = steps.length;

    const previousButton = document.getElementById('previous-button');
    const saveButton = document.getElementById('save-button');
    const xButton = document.getElementById('x-button');
    const checkButton = document.getElementById('check-button');

    let selectedStepStatus = "UNDEFINED";
    let selectedStepComment = "";

    function updateStepView(index) {
        steps.forEach(step => step.style.display = 'none');

        let selectedStep = steps.at(index);
        selectedStep.style.display = 'flex';

        selectedStepStatus = selectedStep.querySelector(".step-status")?.value || "UNDEFINED";
        selectedStepComment = selectedStep.querySelector(".step-comment")?.value.trim() || "";

        if (selectedStepComment === "Step wasn't run"){
            selectedStep.querySelector(".step-comment").value = "";
            selectedStepComment = "";
        }

    }

    function verifyForm(index) {

        let selectedStep = steps.at(index);
        selectedStepStatus = selectedStep.querySelector(".step-status")?.value || "UNDEFINED";
        selectedStepComment = selectedStep.querySelector(".step-comment")?.value.trim() || "";

        if (selectedStepComment === "" && selectedStepStatus === "REFUSED"){
            alert("Comment cannot be empty in case of failure")
        }
        else if (selectedStepStatus === "UNDEFINED") {
            alert("Please modify this step status")
        }
        else if (selectedStepStatus === "REFUSED") {
            saveButton.click();
        }
        else if (index === stepCount - 1) {
            saveButton.click();
        }
        else if (index < stepCount - 1) {
            iteratorValue = index + 1;
            updateStepView(iteratorValue);
        }

    }

    updateStepView(iteratorValue);

    previousButton.addEventListener('click', function () {
        if (iteratorValue > 0) {
            iteratorValue--;
            updateStepView(iteratorValue);
        }
    });

    checkButton.addEventListener('click', function () {
        let selectedStep = steps.at(iteratorValue);
        let statusDropdown = selectedStep.querySelector('select.step-status');
        let acceptedOption = statusDropdown.querySelector('option[value="ACCEPTED"]');
        acceptedOption.selected = true;
        verifyForm(iteratorValue);
    });

    xButton.addEventListener('click', function () {
        let selectedStep = steps.at(iteratorValue);
        let statusDropdown = selectedStep.querySelector('select.step-status');
        let refusedOption = statusDropdown.querySelector('option[value="REFUSED"]');
        refusedOption.selected = true;
        verifyForm(iteratorValue);
    });
});
