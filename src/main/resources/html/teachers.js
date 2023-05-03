class TeachersPage {
    constructor(elementId, jsonData) {
        this.elementId = elementId;
        this.jsonData = jsonData;
        this.createCollapse();
    }

    createCollapse() {
        const parentEl = document.getElementById(this.elementId);


        for (let i = 0; i < this.jsonData.length; i++) {
            const subject = this.jsonData[i];

            const cardHTML = `
                <div class="card text-center" >
                    <div class="card-body text-secondary">
                        <h5 class="card-title">${subject.name}</h5>
                        <p class="card-text">Apasa aici pentru a intra in clasa ta</p>
                        <a href="#" onClick="" class="btn btn-primary"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" class="bi bi-arrow-down-square" viewBox="0 0 16 16">
                        <path fill-rule="evenodd" d="M15 2a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V2zM0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm8.5 2.5a.5.5 0 0 0-1 0v5.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V4.5z"/>
                      </svg></a>
                    </div>
                </div>
                `;
            // Append the new collapse HTML to the parent element
            parentEl.insertAdjacentHTML("beforeend", cardHTML);
        }
    }

    deleteCollapse() {
        const collapseEl = document.getElementById(this.elementId);
        collapseEl.parentNode.removeChild(collapseEl);
    }
}

function getCardInstance(elementId) {
    const collapseEl = document.getElementById(elementId);
    if (!collapseEl) {
        Console.error(`Colapse element with ID '${tableId}' not found.`);
    }

    return collapseEl.jsonSubjectsCollapsible;
}

function refreshCard(cardId, newData) {
    const collapseElement = document.getElementById(cardId);
    if (collapseElement) {
        collapseElement.innerHTML = '';
    }
    const collapse = new SubjectsPage(cardId, newData);
}