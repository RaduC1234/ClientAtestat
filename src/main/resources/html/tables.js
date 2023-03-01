class JSONTable {
    constructor(data, tableId) {
        this.data = data;
        this.tableId = tableId;
        this.createTable();
    }

    createTable() {
        // Get a reference to the container element
        const container = document.getElementById(this.tableId);

        // Create the table element
        const table = document.createElement('table');

        // Add the header row to the table
        const headerRow = document.createElement('tr');
        for (const field in this.data[0]) {
            const headerCell = document.createElement('th');
            headerCell.innerText = field;
            headerRow.appendChild(headerCell);
        }
        table.appendChild(headerRow);

        // Add the data rows to the table
        for (let i = 0; i < this.data.length; i++) {
            const dataRow = document.createElement('tr');
            for (const field in this.data[i]) {
                const dataCell = document.createElement('td');
                const cellValue = this.data[i][field];
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
        for (const field in rowData) {
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
        tableElement.innerHTML = "";
    }
    const table = new JSONTable(newData, tableId);
}