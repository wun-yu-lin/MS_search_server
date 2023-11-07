import SpectrumChart from "./utility/spectrumChart.js"

const spectrumCart = new SpectrumChart();

mainAsync();

async function mainAsync() {
    createMessageIntoSpectrumContainer("Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum.")



    document.getElementById("search_button").onclick = async function () {
        let getParameterObj = getSpectrumQueryParaFromForm()
        console.log(getParameterObj)
        let fetchUrl = generateGetUrlByParameter(getParameterObj, "/api/spectrum/")
        let fetchData = await fetchSpectrumDataByGetMethod(fetchUrl)
        document.getElementById("spectrum_container_div").innerHTML = ""
        if (fetchData.length === 0) {
            createMessageIntoSpectrumContainer("No spectrum data found")
            return
        }
        fetchData.forEach(function (item) {
            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)
        });
    }

}

function generateGetUrlByParameter(getParameterObj, apiUrl) {
    if (getParameterObj.isPassCheck === false || apiUrl === null || getParameterObj === null) {
        alart("Please check your input data")
        return null
    }

    apiUrl = apiUrl + "?";
    let paraArr = Object.keys(getParameterObj)
    //generate the url
    for (let i = 0; i < paraArr.length; i++) {
        if (getParameterObj[paraArr[i]] === null || paraArr[i]==="isPassCheck") {
            continue
        }
        apiUrl += "&"
        apiUrl += paraArr[i] + "=" + getParameterObj[paraArr[i]]
    }

    return apiUrl
}


function getSpectrumQueryParaFromForm() {
    let getParameterObj = {
        spectrumInit: null,
        spectrumOffSet: null,
        compoundName: null,
        formula: null,
        authorId: null,
        msLevel: null,
        maxPrecursorMz: null,
        minPrecursorMz: null,
        maxExactMass: null,
        minExactMass: null,
        precursorType: null,
        ionMode: null,
        ms2Spectrum: null,
        ms2SpectrumSimilarityTolerance: null,
        isPassCheck: true
    }
    try {

        getParameterObj.compoundName = document.getElementById("compound_name_para").value
        getParameterObj.formula = document.getElementById("formula").value
        getParameterObj.ionMode = document.getElementById("charge").value
        getParameterObj.ms2Spectrum = document.getElementById("ms2Spectrum").value

        let tolerance_unit = document.getElementById("tolerance_unit").value
        let tolerance_value = parseFloat(document.getElementById("tolerance").value)
        let ms2_tolerance_value = parseFloat(document.getElementById("ms2_para").value)
        let exactMass = parseFloat(document.getElementById("exact_mass").value)
        let precursorMz = parseFloat(document.getElementById("precursor_mz").value)

        //計算出tolerance的值 - exact mass
        if (isNaN(exactMass) || exactMass === null || exactMass === undefined) {
            getParameterObj.maxExactMass = null
            getParameterObj.minExactMass = null
        } else {
            if (tolerance_unit === "ppm") {
                let deviationDa = exactMass * tolerance_value / 10 ** 6
                getParameterObj.maxExactMass = exactMass + deviationDa
                getParameterObj.minExactMass = exactMass - deviationDa

            } else if (tolerance_unit === "Da") {
                getParameterObj.maxExactMass = exactMass + tolerance_value
                getParameterObj.minExactMass = exactMass - tolerance_value
            }
        }

        //計算出tolerance的值 - precursor mz
        if (isNaN(precursorMz) || precursorMz === null || precursorMz === undefined) {
            getParameterObj.maxPrecursorMz = null
            getParameterObj.minPrecursorMz = null
        } else {
            if (tolerance_unit === "ppm") {
                let deviationMz = precursorMz * tolerance_value / 10 ** 6
                getParameterObj.maxPrecursorMz = precursorMz + deviationMz
                getParameterObj.minPrecursorMz = precursorMz - deviationMz

            } else if (tolerance_unit === "Da") {
                getParameterObj.maxPrecursorMz = precursorMz + tolerance_value
                getParameterObj.minPrecursorMz = precursorMz - tolerance_value
            }
        }

        let keys = Object.keys(getParameterObj)
        for (let i = 0; i < keys.length; i++) {
            if (getParameterObj[keys[i]] === "" || getParameterObj[keys[i]] === undefined) {
                getParameterObj[keys[i]] = null
            }
        }


    }catch (e) {
        console.log(e)

        //if any failed data is not valid, then set the isPassCheck to false
        getParameterObj.isPassCheck = false
        return null
    }

    if(getParameterObj.isPassCheck=== false){
        alert("Please check your input data")
    }

    return getParameterObj
}


async function fetchSpectrumDataByGetMethod(url) {
    let requestParaObj = {
        "method": "GET",
    }

    let data = await fetch(url, requestParaObj)
    let dataParse = await data.json()

    return dataParse
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

    //preprocess the data for undefined or null data
    let spectrumDataObjKeys = Object.keys(spectrumDataObj)
    for (let i = 0; i < spectrumDataObjKeys.length; i++) {
        if (spectrumDataObj[spectrumDataObjKeys[i]] == null || spectrumDataObj[spectrumDataObjKeys[i]] === undefined) {
            spectrumDataObj[spectrumDataObjKeys[i]] = "N/A"
        }
    }

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


function createMessageIntoSpectrumContainer(message) {
    let containerDiv = document.getElementById("spectrum_container_div");
    containerDiv.innerHTML = `<div class="spectrum_item_div">
			<h1>${message}</h1>
		</div>`
}