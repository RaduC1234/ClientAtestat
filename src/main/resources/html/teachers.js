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
          <div class="card" style="width: 18rem;">
            <svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg"
                preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: Image cap">
                <rect width="100%" height="100%" fill="#868e96"></rect>
                <text x="50%" y="50%" fill="#dee2e6" dy=".3em" text-anchor="middle" dominant-baseline="middle" style="font-size: 24px;">${subject.name}</text>
            </svg>
  
            <div class="card-body d-flex justify-content-center align-items-center">
              <button class="btn btn-primary" data-id="${subject.id}" data-name="${subject.name}">Intra in clasa</button>
            </div>
          </div>
        `;
            // Append the new collapse HTML to the parent element
            parentEl.insertAdjacentHTML("beforeend", cardHTML);
        }

        const buttons = parentEl.querySelectorAll("button[data-id][data-name]");
        buttons.forEach((button) => {
            button.addEventListener("click", () => {
                const id = button.dataset.id;
                const name = button.dataset.name;
                Gui.teacherRequestLoadSubject(id, name);
            });
        });
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
    const collapse = new TeachersPage(cardId, newData);
}
