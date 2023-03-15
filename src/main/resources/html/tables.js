class JSONTable {
  constructor(jsonData, tableId) {
    this.jsonData = jsonData;
    this.tableId = tableId;
    this.createTable();
  }

  createTable() {
    // Get a reference to the container element
    const container = document.getElementById(this.tableId);

    // Create the table element
    const table = document.createElement('table');
    table.classList.add('table');

    // Add the header row to the table
    const headerRow = document.createElement('tr');
    const fields = Object.keys(this.jsonData[0]);
    for (const field of fields) {
      const headerCell = document.createElement('th');
      headerCell.innerText = field;
      headerCell.classList.add('text-primary');
      headerRow.appendChild(headerCell);
    }
    table.appendChild(headerRow);

    // Add the data rows to the table
    const data = Object.values(this.jsonData);
    for (let i = 0; i < data.length; i++) {
      const dataRow = document.createElement('tr');
      for (const field of fields) {
        const dataCell = document.createElement('td');
        const cellValue = data[i][field];
        dataCell.appendChild(document.createTextNode(cellValue));
        dataRow.appendChild(dataCell);
      }
      table.appendChild(dataRow);
    }

    // Replace any existing table in the container with the new one
    container.innerHTML = '';
    container.appendChild(table);
  }

  deleteRow(rowIndex) {
    const table = document.getElementById(this.tableId);
    table.deleteRow(rowIndex);
  }

  addRow(rowData) {
    const table = document.getElementById(this.tableId);
    const dataRow = table.insertRow();
    const fields = Object.keys(rowData);
    for (const field of fields) {
      const dataCell = dataRow.insertCell();
      dataCell.innerText = rowData[field];
    }
  }

  deleteTable() {
    const table = document.getElementById(this.tableId);
    table.parentNode.removeChild(table);
  }
}

function getTableInstance(tableId) {
  const tableElement = document.getElementById(tableId);
  if (!tableElement) {
    throw new Error(`Table element with ID '${tableId}' not found.`);
  }
  return tableElement.jsonTable;
}

function refreshTable(tableId, newData) {
  const tableElement = document.getElementById(tableId);
  if (tableElement) {
    tableElement.innerHTML = '';
  }
  const table = new JSONTable(newData, tableId);
}