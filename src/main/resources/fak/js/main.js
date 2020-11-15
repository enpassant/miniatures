(function() {
    const base = {};

    function init() {
        console.log("Init");
        base.table = document.getElementById('table'),
        base.commandEl = document.getElementById('command');
        base.commandEl.addEventListener('click', clickCommand);
        const addColumn = document.getElementById('addColumn');
        addColumn.addEventListener('click', clickAddColumn);
        const addRow = document.getElementById('addRow');
        addRow.addEventListener('click', clickAddRow);
        const generateCsv = document.getElementById('generateCsv');
        generateCsv.addEventListener('click', clickGenerateCsv);
        addEventListener('click', 'td.counter', clickCell);
    }

    function clickCommand(event) {
        doCommand(
            () => base.commandEl.textContent = '-',
            () => base.commandEl.textContent = '0',
            () => base.commandEl.textContent = '+'
        );
    }

    function clickCell(event) {
        const value = parseInt(event.target.textContent);
        doCommand(
            () => event.target.textContent = value + 1,
            () => event.target.textContent = value - 1,
            () => event.target.textContent = 0
        );
    }

    function clickGenerateCsv(event) {
        let csv = [];
        for (let i = 0; i < table.rows.length; i++) {
            let row = table.rows[i];
            let csvRow = [];
            for (let j = 0; j < row.cells.length; j++) {
                let cell = row.cells[j];
                csvRow.push(cell.textContent);
            }
            csv.push(csvRow.join(', '));
        }
        let blob = new Blob([csv.join('\n')], {type: "text/plain;charset=utf-8"});
        saveAs(blob, "values.csv");
    }

    function clickAddColumn(event) {
        appendColumn(base.table);
    }

    function clickAddRow(event) {
        appendRow(base.table);
    }

    function doCommand(fnPlus, fnMinus, fnZero) {
        const command = base.commandEl.textContent;
        if (command === '+') {
            fnPlus.apply();
        } else if (command === '-') {
            fnMinus.apply();
        } else if (command === '0') {
            fnZero.apply();
        }
    }

    function appendRow(table) {
        let row = table.insertRow(table.rows.length),
            i;
        for (i = 0; i < table.rows[0].cells.length; i++) {
            let cell = row.insertCell(i);
            if (i === 0) {
                cell.textContent = (table.rows.length - 1) + '.';
            } else {
                cell.textContent = '0';
                cell.className = 'counter';
            }
        }
    }

    function appendColumn(table) {
        var headerCell = document.createElement("TH");
        headerCell.innerHTML = 'Type';
        headerCell.contentEditable = 'true';
        table.rows[0].appendChild(headerCell);

        for (let i = 1; i < table.rows.length; i++) {
            let cell = table.rows[i].insertCell(table.rows[i].cells.length);
            cell.textContent = '0';
            cell.className = 'counter';
        }
    }

    function ready(fn) {
        if (document.readyState != 'loading'){
            init();
        } else {
            document.addEventListener('DOMContentLoaded', init);
        }
    }

    function addEventListener(eventName, elementSelector, handler) {
        document.addEventListener(eventName, function(e) {
            for (var target = e.target; target && target != this; target = target.parentNode) {
                if (target.matches(elementSelector)) {
                    handler.call(target, e);
                    break;
                }
            }
        }, false);
    }

    ready();
})();
