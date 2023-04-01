class JSONSubjectsCollapsible {
  constructor(jsonData, elementId) {
    this.jsonData = jsonData;
    this.elementId = elementId;
  }

  createCollapse() {
    // Get the parent element where we'll append the new collapse
    const parentEl = document.getElementById(this.elementId);

    // Loop through each subject in the JSON data
    for (let i = 0; i < this.jsonData.subjects.length; i++) {
      const subject = this.jsonData.subjects[i];

      // Find the teacher's name
      let teacherName = "";
      for (let j = 0; j < subject.users.length; j++) {
        const user = subject.users[j];
        if (user.type === "TEACHER") {
          teacherName = user.firstName + " " + user.lastName;
          break;
        }
      }

      // Create the HTML for the collapse
      const collapseHTML = `
            <div id="subject-${subject.id}" role="tablist">
              <div class="card card-collapse">
                <div class="card-header" role="tab" id="heading-${subject.id}">
                  <h5 class="mb-0">
                    <a data-toggle="collapse" href="#collapse-${subject.id}" aria-expanded="true" aria-controls="collapse-${subject.id}">
                      <h4 class="title">${subject.name} <i class="bi bi-caret-down"></i></h4>
                    </a>
                  </h5>
                </div>
    
                <div id="collapse-${subject.id}" class="collapse show" role="tabpanel" aria-labelledby="heading-${subject.id}" data-parent="#subject-${subject.id}">
                  <div class="card-body">
                    <div class="row">
                      <div class="col-md-5">
                        <div class="d-flex align-items-center">
                          <h5 class="mr-3 mb-0"><i class="bi bi-easel2-fill"></i> Teacher:</h5>
                          <div class="btn-group btn-group-sm">
                            <button class="btn btn-info">${teacherName}</button>
                          </div>
                        </div>
                      </div>
                      <div class="col-md-4">
                        <div class="d-flex align-items-center">
                          <h5 class="mb-0">Students Enroled</h5>
                          <button class="btn btn-secondary btn-sm ml-2">${subject.users.length}</button>
                        </div>
                      </div>
                      <div class="col-md-3 d-flex justify-content-end">
                        <button class="btn btn-danger btn-sm"><i class="bi bi-trash"></i> Delete</button>
                      </div>
                    </div>
    
                    <div class="row mt-5">
                      <div class="col-md-5 d-flex align-items-center">
                        <h5 class="mr-3 mb-0"><i class="bi bi-mortarboard-fill"></i> Students:</h5>
                        <div class="student-list" id="student-list-${subject.id}">
                          <!-- students will be added here dynamically -->
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          `;

      // Append the new collapse HTML to the parent element
      parentEl.insertAdjacentHTML("beforeend", collapseHTML);

      // // Get the student list element
      // const studentList = document.querySelector(`#student-list-${subject.id}`);

      // // Loop through each student in the subject
      // subject.users.forEach((user) => {
      //     // Create HTML for the student
      //     const studentHTML = `
      // <div class="student">
      // <div class="name">${user.firstName} ${user.lastName}</div>
      // <div class="username">${user.username}</div>
      // </div>
      // `;

      //     // Append the HTML for the student to the student list
      //     studentList.insertAdjacentHTML("beforeend", studentHTML);
      // });
    }
  }

  deleteCollapse() {
    const collapseEl = document.getElementById(this.elementId);
    collapseEl.parentNode.removeChild(collapseEl);
  }
}


function getCollapseInstance(elementId) {
  const collapseEl = document.getElementById(elementId);
  if (collapseEl) {
    return new JSONSubjectsCollapsible(collapseEl.dataset.json, elementId);
  } else {
    console.error(`Element with id ${elementId} not found`);
    return null;
  }
}


function refreshCollapse(elementID, newData) {
  const collapseInstance = getCollapseInstance(elementID);
  if (collapseInstance) {
    collapseInstance.deleteCollapse();
    collapseInstance.jsonData = newData;
    collapseInstance.createCollapse();
  }
}
