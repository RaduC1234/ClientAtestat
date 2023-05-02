class SubjectsPage {
  constructor(elementId, jsonData) {
    this.elementId = elementId;
    this.jsonData = jsonData;
    this.createCollapse();
  }

  createCollapse() {
    // Get the parent element where we'll append the new collapse
    const parentEl = document.getElementById(this.elementId);

    // Loop through each subject in the JSON data
    for (let i = 0; i < this.jsonData.length; i++) {
      const subject = this.jsonData[i];

      // Find the teacher's name
      let teacherName = "";
      for (let j = 0; j < subject.users.length; j++) {
        const user = subject.users[j];
        if (user.type === "TEACHER") {
          teacherName = user.firstName + " " + user.lastName;
          break;
        }
      }

      //Console.info("teacher name is " + teacherName);

      // Create the HTML for the collapse
      const collapseHTML = `
              <div class="card card-collapse">
                <div class="card-header" role="tab" id="heading-${subject.id}">
                  <h5 class="mb-0">
                    <a data-toggle="collapse" href="#collapse-${subject.id}" aria-expanded="true" aria-controls="collapse-${subject.id}">
                      <h4 class="title">${subject.name.slice(1, -1)} <i class="bi bi-caret-down"></i></h4>
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
                          <button class="btn btn-secondary btn-sm ml-2">${subject.users.length - 1}</button>
                        </div>
                      </div>
                      <div class="col-md-3 d-flex justify-content-end">
                        <button class="btn btn-danger btn-sm" onclick = "Gui.requestAdminDeleteSubjects(${subject.id})"><i class="bi bi-trash"></i> Delete</button>
                      </div>
                    </div>
    
                    <div class="row mt-5">
                      <div class="col-md-5 d-flex align-items-center">
                        <h5 class="mr-3 mb-0"><i class="bi bi-mortarboard-fill"></i> Students:</h5>
                        <div class="student-list btn-group" id="student-list-${subject.id}">
                          <!-- students will be added here dynamically -->
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
          `;

      // Append the new collapse HTML to the parent element
      parentEl.insertAdjacentHTML("beforeend", collapseHTML);

      // Get the student list element
      const studentList = document.querySelector(`#student-list-${subject.id}`);


      for (let j = 0; j < subject.users.length; j++) {
        const user = subject.users[j];
        if (user.type === "STUDENT") {
          // Create HTML for the student
          const studentHTML = `
        <div class="btn-group mr-2 btn-group-sm" role="group" aria-label="${user.id} group">
          <button type="button" class="btn btn-warning">${user.username}</button>
          <button type="button" class="btn btn-warning" onclick="Gui.AdminRemoveUserFromSubject(${subject.id}, ${user.id})">x</button>
        </div>
    `;

          // Append the HTML for the student to the student list
          studentList.insertAdjacentHTML("beforeend", studentHTML);
        }
      }


      // Add a button to add a new student to the subject
      const addStudentButton = `
      <div class ="btn-group mr-2 btn-group-sm role="group">
      <button type="button" class="btn btn-default" data-toggle="modal" data-target="#exampleModalLong" onClick="Gui.requestAdminGetStudents(${subject.id})"Gui.>+</button>
      </div>
`;

      // Append the add student button to the student list
      studentList.insertAdjacentHTML("beforeend", addStudentButton);
    }
  }

  deleteCollapse() {
    const collapseEl = document.getElementById(this.elementId);
    collapseEl.parentNode.removeChild(collapseEl);
  }
}


function getCollapseInstance(elementId) {
  const collapseEl = document.getElementById(elementId);
  if (!collapseEl) {
    Console.error(`Colapse element with ID '${tableId}' not found.`);
  }

  return collapseEl.jsonSubjectsCollapsible;
}

function refreshCollapse(collapseID, newData) {
  const collapseElement = document.getElementById(collapseID);
  if (collapseElement) {
    collapseElement.innerHTML = '';
  }
  const collapse = new SubjectsPage(collapseID, newData);
}

let teacherId = -1;

function dropDown(data) {

  let users = data;

  // assuming usersDropdownMenu is the DOM element for the dropdown menu
  let usersDropdownMenu = document.getElementById("usersDropdownMenu");
  usersDropdownMenu.innerHTML = '';

  // generate HTML for each user
  let usersHtml = "";
  for (let i = 0; i < users.length; i++) {
    let user = users[i];
    usersHtml += `<a class="dropdown-item" href="#" onclick="selectTeacher(${user.id}, '${user.username}')">${user.firstName} ${user.lastName}</a>`;
  }

  // populate the dropdown menu with the generated HTML
  usersDropdownMenu.innerHTML = usersHtml;

  //Console.info("dasdsa");
}

function selectTeacher(id, name) {

  teacherId = id;
  document.getElementById("dropdownMenuButton").innerText = name;
}

function addNewSubject() {

  let subjectName = document.getElementById('nameForm');

  if (teacherId != -1 || subjectName.value != '') {
    Gui.requestAdminAddSubjects(subjectName.value, teacherId);
  }
}

class StudentsTable {
  constructor(elementId, jsonData) {
    this.elementId = elementId;
    this.jsonData = jsonData;
    this.createTable();
  }

  createTable() {
    let tableHTML = `
      <table class="table" id= "studentsTable">
        <thead>
          <tr>
            <th></th>
            <th>ID</th>
            <th>Username</th>
            <th>First Name</th>
            <th>Last Name</th>
          </tr>
        </thead>
        <tbody>
      `;
    for (let i = 0; i < this.jsonData.length; i++) {
      const student = this.jsonData[i];
      tableHTML += `
        <tr>
          <td class="d-flex justify-content-center">
            <input class="form-check-input justify-content-center" type="checkbox" value="" id="flexCheck${i}">
          </td>
          <td>${student.id}</td>
          <td>${student.username}</td>
          <td>${student.firstName}</td>
          <td>${student.lastName}</td>
        </tr>
      `;
    }
    tableHTML += `
        </tbody>
      </table>
    `;
    document.getElementById(this.elementId).innerHTML = tableHTML;
  }


  deleteTable() {
    document.getElementById(this.elementId).innerHTML = "";
  }
}

function getAddModalInstance(elementId) {
  return new StudentsTable(elementId);
}

function refreshAddModal(modalID, newData) {
  const AddElement = document.getElementById(modalID);
  if (AddElement) {
    AddElement.innerHTML = '';
  }
  const collapse = new StudentsTable(modalID, newData);
}

function onSave() {

  Console.info("hahahah");
  console.info("hahahah");

  const table = document.getElementById("studentsTable");

  for (let i = 0; i < table.rows.length; i++) {
    const row = table.rows[i];
    const checkbox = row.querySelector("input[type='checkbox']");
    if (checkbox.checked) {
      Gui.requestAdminAddStudentToSubject(3, 4);
    }
  }
}

