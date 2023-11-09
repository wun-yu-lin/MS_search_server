import SpectrumChart from "./utility/SpectrumChart.js"
import FetchAPI from "./utility/FetchAPI.js"

const spectrumCart = new SpectrumChart();
const fetchAPI = new FetchAPI();

mainAsync();


// let canvasDiv = createSpectrumItemElementBySpectrumData(test2)
// spectrumCart.createComparisonChartBySpectrumData(test,test1, canvasDiv)

async function mainAsync() {
    createMessageIntoSpectrumContainer("Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum.")
    document.getElementById("search_button").onclick = OnClickFunctionForSearchSpectrum
    let firstFetchData = await sentParameterToFuzzyAPI()
    if (firstFetchData == null) {
    }else if (firstFetchData.length===0){

        createMessageIntoSpectrumContainer("No spectrum data found!! Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum. ")
    } else {
        document.getElementById("spectrum_container_div").innerHTML = ""
        firstFetchData.forEach(function (item) {

            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)
        });

    }


}

async function sentParameterToFuzzyAPI() {
    function isUrlParameterValid(urlParameter) {
        if (urlParameter === undefined || urlParameter === null || urlParameter === "") {
            return false
        }
        return true
    }

    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    if (params.size === 0) return
    let paramObject = {}
    params.forEach(function (value, key) {
        if (key === "keyWord" && isUrlParameterValid(value)) {
            paramObject["keyWord"] = value
        }
    })
    if (Object.keys(paramObject).length === 0) return

    let fetchUrl = fetchAPI.generateGetUrlByParameter(paramObject, "/api/spectrum/fuzzy")
    return await fetchAPI.fetchSpectrumDataByGetMethod(fetchUrl, {"method": "GET"})
}

async function OnClickFunctionForSearchSpectrum() {
    let getParameterObj = getSpectrumQueryParaFromForm()
    let fetchUrl = fetchAPI.generateGetUrlByParameter(getParameterObj, "/api/spectrum/")
    let fetchData = await fetchAPI.fetchSpectrumDataByGetMethod(fetchUrl, {"method": "GET"})
    document.getElementById("spectrum_container_div").innerHTML = ""
    if (fetchData.length === 0 || fetchData === undefined || fetchData === null){
        createMessageIntoSpectrumContainer("No spectrum data found, please check your parameter")
        return
    }

    if (getParameterObj.ms2Spectrum!=null && getParameterObj.ms2Spectrum!==""){
        let expMS2spectrum = ms2SpectrumStringToNestArr(getParameterObj.ms2Spectrum)
        //ms2 spectrum search
        let newExpMS2spectrum =  Array.from(expMS2spectrum)
        fetchData.forEach(function (item) {
            let expMS2spectrum = ms2SpectrumStringToNestArr(getParameterObj.ms2Spectrum)
            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createComparisonChartBySpectrumData(newExpMS2spectrum,item["ms2SpectrumList"], canvasDiv)
        });
    }else{
        //no ms2 spectrum search
        fetchData.forEach(function (item) {
            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)
        });

    }
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
        forwardWeight: null,
        reverseWeight: null,
        ms2SimilarityAlgorithm: null,
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
            if(typeof(getParameterObj[keys[i]])=="number" && isNaN(getParameterObj[keys[i]])){
                getParameterObj[keys[i]] = null
            }
        }

        //整理 MS2 spectrum 給後端做使用
        if (getParameterObj.ms2Spectrum!=null){
            getParameterObj.ms2Spectrum = getParameterObj.ms2Spectrum.replaceAll(" ", "")
            getParameterObj.ms2Spectrum = getParameterObj.ms2Spectrum.replaceAll("\n", " ").trim()

        }

    } catch (e) {
        console.log(e)

        //if any failed data is not valid, then set the isPassCheck to false
        getParameterObj.isPassCheck = false
        return null
    }

    if (getParameterObj.isPassCheck === false) {
        alert("Please check your input data")
    }

    return getParameterObj
}

function ms2SpectrumStringToNestArr(ms2SpectrumString){
    let resultArr = []
    let ms2SpectrumStringArr = ms2SpectrumString.split(" ")
    for (let i = 0; i < ms2SpectrumStringArr.length; i++) {
        let ms2SpectrumStringArrElement = ms2SpectrumStringArr[i].split(":")
        let mz = parseFloat(ms2SpectrumStringArrElement[0])
        let intensity = parseFloat(ms2SpectrumStringArrElement[1])
        resultArr.push([mz,intensity])
    }
    return resultArr
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