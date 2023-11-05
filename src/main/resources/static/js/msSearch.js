import SpectrumChart from "./utility/spectrumChart.js"

const spectrumCart = new SpectrumChart();


let data = [
    [
        115.054,
        19.371234
    ],
    [
        139.0542,
        100.0
    ],
    [
        151.0539,
        25.289117
    ],
    [
        152.0619,
        77.953417
    ],
    [
        163.054,
        20.564534
    ],
    [
        165.0699,
        27.061389
    ]];

mainAsync();

async function mainAsync() {
    let data = await fetchSpectrumData()
    console.log(data)
    console.log(data[0]["ms2SpectrumList"])
    data.forEach(function(item) {
        let canvasDiv = createSpectrumItemElementBySpectrumData(item)
        spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)
    });

}


async function fetchSpectrumData() {
    let requestParaObj = {
        methods: "GET"
    }

    let data = await fetch('/api/spectrum/?maxPrecursorMz=445.17&minPrecursorMz=445.15&ionMode=positive', requestParaObj)
    data = await data.json()
    console.log(data)

    return data
}

function createSpectrumItemElementBySpectrumData(spectrumDataObj) {


    let containerDiv = document.getElementById("spectrum_container_div");

    let itemDiv = document.createElement("div");
    itemDiv.className = "spectrum_item_div";

    let canvasDiv = document.createElement("div");
    canvasDiv.className = "spectrum_canvas_div";

    let canvas = document.createElement("canvas");
    canvas.className = "spectrumChart";

    canvasDiv.appendChild(canvas);

    let infoDiv = document.createElement("div");
    infoDiv.className = "spectrum_info_div";

    let table = document.createElement("table");

    let tableContent = [
        ["Compound Name:", `${spectrumDataObj.name}`],
        ["Exact Mass:", `${spectrumDataObj.exactMass}`],
        ["Formula:", `${spectrumDataObj.formula}`],
        ["InChi key", `${spectrumDataObj.inChiKey}`],
        ["Charge:", `${spectrumDataObj.ionMode}`],
        ["Collision Energy:", `${spectrumDataObj.collisionEnergy}`],
        ["Precursor m/z:", `${spectrumDataObj.precursorMz}`],
        ["Adduct ion:", `${spectrumDataObj.precursorType}`],
        ["Tool type:", `${spectrumDataObj.toolType}`],
        ["SMILE:", `${spectrumDataObj.smile}`]
    ];

    for (let i = 0; i < tableContent.length; i++) {
        let row = document.createElement("tr");
        let th = document.createElement("th");
        th.textContent = tableContent[i][0];
        let td = document.createElement("td");
        td.textContent = tableContent[i][1];
        row.appendChild(th);
        row.appendChild(td);
        table.appendChild(row);
    }
    infoDiv.appendChild(table);
    itemDiv.appendChild(canvasDiv);
    itemDiv.appendChild(infoDiv);
    containerDiv.appendChild(itemDiv);
    return canvas;

}