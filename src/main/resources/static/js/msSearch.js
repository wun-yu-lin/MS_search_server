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
        return

    }
    searchDivDeFaultPara();


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
    let fetchUrl = fetchAPI.generateGetUrlByParameter(getParameterObj, "/api/spectrum")
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

function searchDivDeFaultPara() {
    document.getElementById("compound_name_para").value = "tetracycline"
    document.getElementById("formula").value = "C22H24N2O8"
    document.getElementById("exact_mass").value = "444.153"
    document.getElementById("precursor_mz").value = "443.146"
    document.getElementById("tolerance").value = "20"
    document.getElementById("ms2Spectrum").innerHTML = `65.0397:1.632979
65.9986:5.718588 
68.9982:4.494347 
83.0503:1.145107 
84.0091:30.786080 
86.0248:5.028993 
87.0088:3.734123 
93.0346:2.106641 
96.0091:2.026262 
96.9931:5.828214 
99.0087:6.859444 
100.0767:5.077220 
109.0295:13.387728 
111.0088:2.607061 
111.0452:19.154336 
119.0137:1.196135 
119.0499:1.102481 
121.0294:2.041301 
123.0451:4.538011 
124.0041:1.070952 
124.0164:4.551079 
125.072:29.207033 
126.0196:4.483457 
126.0561:19.839471 
133.0297:8.994295 
134.0248:3.681851 
135.0088:15.641228 
135.0452:100.000000 
136.0402:5.006176 
137.0246:1.794358 
139.0149:6.120792 
142.0146:8.569897 
142.9986:6.951542 
145.0294:6.189243 
146.0371:1.454694 
147.0451:5.466045 
149.0242:6.463048 
152.0353:2.061422 
153.0194:1.124572 
159.0451:1.176533 
161.0243:98.135324 
162.0197:10.310012 
163.0034:4.047547 
163.0399:7.390252 
169.0619:3.738271 
172.053:1.462058 
173.0606:7.535037 
175.0399:3.544844 
180.0301:6.426437 
183.0815:1.350980 
187.0402:1.771229 
187.0766:18.960494 
189.0552:1.255252 
201.0923:3.768867 
211.0758:1.848289 
214.0634:2.049080`
    document.getElementById("ms2_para").value = "0.5"
}