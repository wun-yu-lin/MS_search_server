import SpectrumChart from "./utility/SpectrumChart.js"
import FetchAPI from "./utility/FetchAPI.js"

let scrollDownObserver;
//global variable for record page status
let _pageStatusObj = {
    "isFirstLoad": false,
    "isNextPage": false,
    "nextPageSpectrumInit": 0,
    "fetchUrl": null,
    "isFetching": false,
    "isMS2SpectrumSearch": false,
    initialPara() {
        this.isFirstLoad = false
        this.isNextPage = false
        this.nextPageSpectrumInit = 0
        this.fetchUrl = null
        this.isFetching = false
        this.isMS2SpectrumSearch = false

    }
};


const spectrumCart = new SpectrumChart();
const fetchAPI = new FetchAPI();


mainAsync();

// let canvasDiv = createSpectrumItemElementBySpectrumData(test2)
// spectrumCart.createComparisonChartBySpectrumData(test,test1, canvasDiv)


async function mainAsync() {
    document.getElementById("loadingScreen").classList.remove("hidden")

    createMessageIntoSpectrumContainer("Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum.")
    document.getElementById("search_button").onclick = onClickFunctionForSearchSpectrum
    document.getElementById("reset_button").onclick = resetParaAndSpectrumItem
    document.getElementById("demo_button").onclick = searchDivDeFaultPara
    let firstFetchData = await sentParameterToFuzzyAPI()

    //處理Fuzzy API的回傳結果
    if (firstFetchData == null) {
    } else if (firstFetchData.length === 0) {

        createMessageIntoSpectrumContainer("No spectrum data found!! Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum. ")
    } else {
        document.getElementById("spectrum_container_div").innerHTML = ""
        firstFetchData.forEach(function (item) {

            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)

        });
        document.getElementById("loadingScreen").classList.add("hidden")
        if (_pageStatusObj.isNextPage === false) insertMessageIntoSpectrumContainer("No more data.")
        createScrollDownObserverForNextPage();
        return

    }
    searchDivDeFaultPara();
    document.getElementById("loadingScreen").classList.add("hidden")
    createScrollDownObserverForNextPage();

}

async function sentParameterToFuzzyAPI() {
    _pageStatusObj.initialPara();

    function isUrlParameterValid(urlParameter) {
        if (urlParameter === undefined || urlParameter === null || urlParameter === "") {
            return false
        }
        //if exist keyword parameter, then set the keyword parameter to the search bar

        return true
    }

    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    if (params.size === 0) {
        document.getElementById("msSearch").value = ""
        return
    }
    document.getElementById("msSearch").value = params.get("keyWord")
    let paramObject = {}
    params.forEach(function (value, key) {
        if (key === "keyWord" && isUrlParameterValid(value)) {
            paramObject["keyWord"] = value
        }
    })
    if (Object.keys(paramObject).length === 0) return

    let fetchUrl = fetchAPI.generateGetUrlByParameter(paramObject, "/api/spectrum/fuzzy")
    _pageStatusObj.fetchUrl = fetchUrl
    _pageStatusObj.isFetching = true
    let response = await fetchAPI.fetchSpectrumDataByGetMethod(fetchUrl, {"method": "GET"})
    if (response.status !== 200){
        createMessageIntoSpectrumContainer("No spectrum data found!! Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum. ")
        _pageStatusObj.isNextPage === false
        document.getElementById("loadingScreen").classList.add("hidden")
    }
    let fetchData = await response.json()
    _pageStatusObj.isFetching = false
    _pageStatusObj.isFirstLoad = true
    if (typeof (fetchData) === "object") {
        _pageStatusObj.nextPageSpectrumInit = _pageStatusObj.nextPageSpectrumInit + fetchData.length
        if (fetchData.length >= 10) {
            _pageStatusObj.isNextPage = true
        } else {
            _pageStatusObj.isNextPage = false
        }
    }

    return fetchData


}

async function onClickFunctionForSearchSpectrum() {
    _pageStatusObj.initialPara();
    document.getElementById("loadingScreen").classList.remove("hidden")
    let getParameterObj = getSpectrumQueryParaFromForm()
    let fetchUrl = fetchAPI.generateGetUrlByParameter(getParameterObj, "/api/spectrum")
    _pageStatusObj.fetchUrl = fetchUrl
    _pageStatusObj.isFetching = true
    let response = await fetchAPI.fetchSpectrumDataByGetMethod(fetchUrl, {"method": "GET"})
    if(response.status !== 200){
        createMessageIntoSpectrumContainer("No spectrum data found, please check your parameter")
        _pageStatusObj.isNextPage === false
        document.getElementById("loadingScreen").classList.add("hidden")
    }
    let fetchData = await response.json()
    _pageStatusObj.isFetching = false
    _pageStatusObj.isFirstLoad = true
    if (typeof (fetchData) === "object") {
        _pageStatusObj.nextPageSpectrumInit = _pageStatusObj.nextPageSpectrumInit + fetchData.length
        if (fetchData.length >= 10) {
            _pageStatusObj.isNextPage = true
        } else {
            _pageStatusObj.isNextPage = false
        }
    }


    document.getElementById("spectrum_container_div").innerHTML = ""
    if (fetchData.length === 0 || fetchData === undefined || fetchData === null || getParameterObj === null) {
        createMessageIntoSpectrumContainer("No spectrum data found, please check your parameter")
        document.getElementById("loadingScreen").classList.add("hidden")
        return
    }

    //check ms2 spectrum or not
    if (getParameterObj.ms2Spectrum != null && getParameterObj.ms2Spectrum !== "") {
        _pageStatusObj.isMS2SpectrumSearch = true
        _pageStatusObj.isNextPage = false
    }

    if (getParameterObj.ms2Spectrum != null && getParameterObj.ms2Spectrum !== "") {
        let expMS2spectrum = ms2SpectrumStringToNestArr(getParameterObj.ms2Spectrum)
        //ms2 spectrum search
        let newExpMS2spectrum = Array.from(expMS2spectrum)
        fetchData.forEach(function (item) {
            let expMS2spectrum = ms2SpectrumStringToNestArr(getParameterObj.ms2Spectrum)
            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createComparisonChartBySpectrumData(newExpMS2spectrum, item["ms2SpectrumList"], canvasDiv)
        });
    } else {
        //no ms2 spectrum search
        fetchData.forEach(function (item) {
            let canvasDiv = createSpectrumItemElementBySpectrumData(item)
            spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)
        });

    }
    if (_pageStatusObj.isNextPage == false) insertMessageIntoSpectrumContainer("No more data.")
    document.getElementById("loadingScreen").classList.add("hidden")
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
        ms2PeakMatchTolerance: null,
        ms2SpectrumSimilarityTolerance: null,
        forwardWeight: null,
        reverseWeight: null,
        ms2SimilarityAlgorithm: null,
        isPassCheck: true
    }
    try {
        //base parameter
        getParameterObj.compoundName = document.getElementById("compound_name_para").value
        getParameterObj.formula = document.getElementById("formula").value
        getParameterObj.ionMode = document.getElementById("charge").value
        getParameterObj.ms2Spectrum = document.getElementById("ms2Spectrum").value
        let tolerance_unit = document.getElementById("tolerance_unit").value
        let tolerance_value = parseFloat(document.getElementById("tolerance").value)
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

        //if tolerance too large, is not allow to search
        if (tolerance_value > 100 && tolerance_unit === "ppm") {
            alert("Tolerance not allow to larger than 100 ppm")
            getParameterObj.isPassCheck = false
            return null
        }
        if (tolerance_value > 2 && tolerance_unit === "Da") {
            alert("Tolerance not allow to larger than 2 Da")
            getParameterObj.isPassCheck = false
            return null
        }


        //計算出ms tolerance的值 - precursor mz
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
            if (typeof (getParameterObj[keys[i]]) == "number" && isNaN(getParameterObj[keys[i]])) {
                getParameterObj[keys[i]] = null
            }
        }

        //整理 MS2 spectrum 給後端做使用
        if (getParameterObj.ms2Spectrum != null) {
            getParameterObj.ms2Spectrum = getParameterObj.ms2Spectrum.replaceAll(" ", "")
            getParameterObj.ms2Spectrum = getParameterObj.ms2Spectrum.replaceAll("\n", " ").trim()
            //get ms2 spectrum similarity related parameter
            getParameterObj.ms2SpectrumSimilarityTolerance = parseFloat(document.getElementById("ms2_similarity_para").value)
            getParameterObj.ms2PeakMatchTolerance = parseFloat(document.getElementById("ms2_tolerance_para").value)
            getParameterObj.forwardWeight = parseFloat(document.getElementById("forward_para").value)
            getParameterObj.reverseWeight = parseFloat(document.getElementById("reverse_para").value)
            getParameterObj.ms2SimilarityAlgorithm = document.getElementById("algorithm_para").value
            if (!isNaN(getParameterObj.ms2SpectrumSimilarityTolerance) && getParameterObj.ms2SpectrumSimilarityTolerance > 1) {
                alert("MS2 spectrum similarity tolerance not allow to larger than 1")
                getParameterObj.isPassCheck = false
                return null
            }
            if (!isNaN(getParameterObj.forwardWeight) && getParameterObj.forwardWeight > 1) {
                alert("sum of weight not allow to larger than 1")
                getParameterObj.isPassCheck = false
                return null
            }
            if (!isNaN(getParameterObj.reverseWeight) && getParameterObj.reverseWeight > 1) {
                alert("sum of weight not allow to larger than 1")
                getParameterObj.isPassCheck = false
                return null
            }
            if (!isNaN(getParameterObj.ms2PeakMatchTolerance) && getParameterObj.ms2PeakMatchTolerance > 100) {
                alert("MS2 peak match tolerance not allow to larger than 100")
                getParameterObj.isPassCheck = false
                return null
            }
            if (getParameterObj.minPrecursorMz === null || getParameterObj.maxPrecursorMz === null) {
                alert("Please key-in precursor m/z & MS tolerance, when you want to search by MS2 spectrum")
                getParameterObj.isPassCheck = false
                return null
            }


            if (isNaN(getParameterObj.ms2SpectrumSimilarityTolerance)) getParameterObj.ms2SpectrumSimilarityTolerance = null
            if (isNaN(getParameterObj.forwardWeight)) getParameterObj.forwardWeight = null
            if (isNaN(getParameterObj.reverseWeight)) getParameterObj.reverseWeight = null
            if (isNaN(getParameterObj.ms2PeakMatchTolerance)) getParameterObj.ms2PeakMatchTolerance = null
            //判斷 forward weight 與 reverse weight 相加是否為 1

            if (getParameterObj.forwardWeight === null && parseInt(getParameterObj.reverseWeight) === 1) {
                getParameterObj.forwardWeight = 0
                getParameterObj.reverseWeight = 1
            }
            if (getParameterObj.reverseWeight === null && parseInt(getParameterObj.forwardWeight) === 1) {
                getParameterObj.forwardWeight = 1
                getParameterObj.reverseWeight = 0
            }
            if (getParameterObj.forwardWeight !== null || getParameterObj.reverseWeight !== null) {
                if ((getParameterObj.forwardWeight + getParameterObj.reverseWeight) !== 1) {
                    alert("forward weight and reverse weight must be 1")
                    getParameterObj.isPassCheck = false
                    return null
                }
            }


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

function ms2SpectrumStringToNestArr(ms2SpectrumString) {
    let resultArr = []
    let ms2SpectrumStringArr = ms2SpectrumString.split(" ")
    for (let i = 0; i < ms2SpectrumStringArr.length; i++) {
        let ms2SpectrumStringArrElement = ms2SpectrumStringArr[i].split(":")
        let mz = parseFloat(ms2SpectrumStringArrElement[0])
        let intensity = parseFloat(ms2SpectrumStringArrElement[1])
        resultArr.push([mz, intensity])
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
        if (spectrumDataObjKeys[i] === "ms2SpectrumSimilarity") {
            typeof (spectrumDataObj[spectrumDataObjKeys[i]]) === "number" ? spectrumDataObj[spectrumDataObjKeys[i]] = spectrumDataObj[spectrumDataObjKeys[i]].toFixed(4) : spectrumDataObj[spectrumDataObjKeys[i]] = "N/A"
        }
    }
    try {
        spectrumDataObj.name = JSON.parse(spectrumDataObj.name.replace(/'/g, '"'))
    } catch (e) {
        console.log(e)
    }

    let tableContent = [
        ["Compound Name:", `${spectrumDataObj.name[0]}`],
        ["CAS No.:", `${spectrumDataObj.cas}`],
        ["MS Level:", `${spectrumDataObj.msLevel}`],
        ["Exact Mass:", `${spectrumDataObj.exactMass}`],
        ["Formula:", `${spectrumDataObj.formula}`],
        ["InChi key", `${spectrumDataObj.inChiKey}`],
        ["Charge:", `${spectrumDataObj.ionMode}`],
        ["Collision Energy:", `${spectrumDataObj.collisionEnergy}`],
        ["Precursor m/z:", `${spectrumDataObj.precursorMz}`],
        ["Adduct ion:", `${spectrumDataObj.precursorType}`],
        ["Data source:", `${spectrumDataObj.dataSourceArrayList}`],
        ["Tool type:", `${spectrumDataObj.toolType}`],
        ["SMILE:", `${spectrumDataObj.smile}`],
        ["Kind:", `${spectrumDataObj.kind}`],
        ["MS2 similarity:", `${spectrumDataObj.ms2SpectrumSimilarity}`]
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

function insertMessageIntoSpectrumContainer(message) {
    let containerDiv = document.getElementById("spectrum_container_div");
    let item_div = document.createElement("div");
    item_div.className = "spectrum_item_div";
    item_div.innerHTML = `<h1>${message}</h1>`
    containerDiv.appendChild(item_div)
}

function searchDivDeFaultPara() {
    document.getElementById("compound_name_para").value = "tetracycline"
    document.getElementById("formula").value = "C22H24N2O8"
    document.getElementById("exact_mass").value = "444.153"
    document.getElementById("precursor_mz").value = "443.146"
    document.getElementById("tolerance").value = "20"
    document.getElementById("ms2Spectrum").value = `65.0397:1.632979
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
    // document.getElementById("ms2_para").value = "0.5"
}


function createScrollDownObserverForNextPage() {
    let scrollDownObserver = new IntersectionObserver(async (entries, observe) => {
        if (entries[0].intersectionRatio < 1.1 && entries[0].isIntersecting > 0.1) {
            let targetElement = entries[0].target;
            observe.unobserve(entries[0].target);
            if (_pageStatusObj.isFetching === true) return observe.observe(targetElement);
            if (_pageStatusObj.isNextPage === false) return observe.observe(targetElement);
            if (_pageStatusObj.isFirstLoad === false) return observe.observe(targetElement);
            //add action of loading

            let response = await fetchAPI.fetchSpectrumDataByGetMethod(_pageStatusObj.fetchUrl + `&spectrumInit=${_pageStatusObj.nextPageSpectrumInit}`, {"method": "GET"})
            if (response.status !== 200){
                createMessageIntoSpectrumContainer("No spectrum data found, please check your parameter")
                _pageStatusObj.isNextPage === false
            }
            let fetchData = await response.json()
            if (typeof (fetchData) === "object") {
                _pageStatusObj.nextPageSpectrumInit = _pageStatusObj.nextPageSpectrumInit + fetchData.length
                if (fetchData.length >= 10) {
                    _pageStatusObj.isNextPage = true
                } else {
                    _pageStatusObj.isNextPage = false
                }
            }

            if (fetchData == null) {
            } else if (fetchData.length === 0) {

                insertMessageIntoSpectrumContainer("No more data.")
            } else {
                fetchData.forEach(function (item) {

                    let canvasDiv = createSpectrumItemElementBySpectrumData(item)
                    spectrumCart.createChartBySpectrumData(item["ms2SpectrumList"], canvasDiv)

                });
                document.getElementById("loadingScreen").classList.add("hidden")
                if (_pageStatusObj.isNextPage === false) insertMessageIntoSpectrumContainer("No more data.")
                return observe.observe(targetElement);

            }

            observe.observe(targetElement);
        }


    })
    scrollDownObserver.observe(document.querySelector(".footer"));
}

function resetParaAndSpectrumItem() {
    document.getElementById("loadingScreen").classList.remove("hidden")
    document.getElementById("spectrum_container_div").innerHTML = ""
    _pageStatusObj.initialPara()
    document.getElementById("msSearch").value = ""
    document.getElementById("compound_name_para").value = ""
    document.getElementById("formula").value = ""
    document.getElementById("exact_mass").value = ""
    document.getElementById("precursor_mz").value = ""
    document.getElementById("tolerance").value = ""
    document.getElementById("tolerance_unit").value = "ppm"
    document.getElementById("charge").value = ""
    document.getElementById("ms2Spectrum").innerHTML = ""
    document.getElementById("ms2Spectrum").value = ""
    document.getElementById("ms2_tolerance_para").value = ""
    document.getElementById("ms2_similarity_para").value = ""
    document.getElementById("forward_para").value = ""
    document.getElementById("reverse_para").value = ""
    document.getElementById("algorithm_para").value = "dotPlot"
    createMessageIntoSpectrumContainer("Please key-in the parameter in the search bar and press the button to query for MS/MS spectrum.")

    document.getElementById("loadingScreen").classList.add("hidden")
}