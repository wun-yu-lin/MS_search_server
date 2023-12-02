// document.getElementById("loadingScreen").classList.remove("hidden")
// document.getElementById("loadingScreen").classList.add("hidden")
// document.getElementById("submitStatusLoading").classList.remove("hidden")
// document.getElementById("uploadStatusLoading").classList.remove("hidden")

import FetchAPI from "./utility/FetchAPI.js";

const fetchAPI = new FetchAPI();
let _taskObj = {
    "responseObj": {},
    "isFileUploaded": false,
    "isFileUploading":false,
    "isFormSubmitted": false,
    "isFormSubmitting":false,
    "isFormPass": false,
    initialize() {
        this.responseObj = {};
        this.isFileUploaded = false;
        this.isFormSubmitted = false;
        this.isFormPass = false;
    },
}

main();
asyncMain();


function main() {
    document.getElementById("loadingScreen").classList.add("hidden")
    document.getElementById("submitStatusLoading").classList.add("hidden")
    document.getElementById("uploadStatusLoading").classList.add("hidden")
    document.getElementById("uploadFileButton").onclick = uploadFile;
    document.getElementById("submitButton").onclick = submitForm;
    document.getElementById("demoDataPosButton").onclick = setupDemoDataPos;
    _taskObj.initialize();
}

async function asyncMain() {
    //if taskID exist, fetch task data and fill in form
    let isTaskIdExit = checkTaskIdExit();
    if (isTaskIdExit) await updateFormByTaskId();
}


async function uploadFile(event) {
    if (_taskObj.isFileUploading) {
        alert("File is uploading, please wait");
        return;
    }
    event.preventDefault();
    _taskObj.initialize();
    _taskObj.isFileUploading = true;

    document.getElementById("uploadStatusResult").innerText = "";
    document.getElementById("uploadStatusLoading").classList.remove("hidden")
    let responseObj = await fetchAPI.sentFormDataByPostMethod("/api/batchSearch/file/upload", new FormData(document.getElementById("myForm")));
    let fetchData = await responseObj.json();
    _taskObj.responseObj = fetchData;

    if (typeof (responseObj) == 'object' && responseObj.status == 200) {
        document.getElementById("uploadStatusResult").innerText = "Success";
        document.getElementById("taskIdResult").innerText = fetchData.id;
        _taskObj.isFileUploaded = true;
    } else {
        alert("Upload failed, please check your file")
        document.getElementById("uploadStatusResult").innerText = "Fail";
    }

    document.getElementById("uploadStatusLoading").classList.add("hidden")
    _taskObj.isFileUploading = false;

}


async function submitForm() {
    if (!_taskObj.isFileUploaded) {
        alert("請先上傳檔案");
        return;
    }
    if (_taskObj.isFormSubmitting) {
        alert("Form is submitting, please wait");
        return;
    }
    _taskObj.isFormSubmitting = true;
    document.getElementById("submitStatusLoading").classList.remove("hidden")
    _taskObj.responseObj.forwardWeight = parseFloat(document.getElementById("forward_para").value);
    _taskObj.responseObj.reverseWeight = parseFloat(document.getElementById("reverse_para").value);
    _taskObj.responseObj.ionMode = document.getElementById("charge").value;
    _taskObj.responseObj.mail = document.getElementById("mail").value;
    _taskObj.responseObj.msTolerance = parseFloat(document.getElementById("msTolerance").value);
    _taskObj.responseObj.msmsTolerance = parseFloat(document.getElementById("msmsTolerance").value);
    _taskObj.responseObj.similarityAlgorithm = document.getElementById("algorithm_para").value;
    _taskObj.responseObj.similarityTolerance = parseFloat(document.getElementById("ms2_similarity_para").value);
    _taskObj.responseObj.ms1Ms2matchMzTolerance = parseFloat(document.getElementById("ms1ms2PairMzParameter").value);
    _taskObj.responseObj.ms1Ms2matchRtTolerance = parseFloat(document.getElementById("ms1ms2PairRtParameter").value);
    _taskObj.responseObj.taskDescription = document.getElementById("taskDescription").value;

    document.getElementById("submitStatusResult").innerText = "";
    let Keys = Object.keys(_taskObj.responseObj);
    for (let i = 0; i < Keys.length; i++) {

        if (_taskObj.responseObj[Keys[i]] === "") {
            _taskObj.responseObj[Keys[i]] = null;
        }
        if (isNaN(_taskObj.responseObj[Keys[i]]) && typeof (_taskObj.responseObj[Keys[i]]) != "string") {
            _taskObj.responseObj[Keys[i]] = null;
        }
    }
    if (_taskObj.responseObj.forwardWeight == null && _taskObj.responseObj.reverseWeight == null) {
        _taskObj.responseObj.forwardWeight = 0.5;
        _taskObj.responseObj.reverseWeight = 0.5;
    }
    if (_taskObj.responseObj.similarityTolerance == null) {
        _taskObj.responseObj.similarityTolerance = 0.5;
    }

    //check form input
    let isFormPass = true;
    if (_taskObj.responseObj.forwardWeight != null || _taskObj.responseObj.reverseWeight != null) {

        if ((_taskObj.responseObj.forwardWeight + _taskObj.responseObj.reverseWeight) !== 1) {
            alert("forward and reverse weight must be 1");
            isFormPass = false;
        }
    }


    if (_taskObj.responseObj.msmsTolerance == null || _taskObj.responseObj.msTolerance == null) {
        alert("MS tolerance and MS/MS tolerance must be filled");
        isFormPass = false;
    }
    if (_taskObj.responseObj.msmsTolerance < 0 || _taskObj.responseObj.msTolerance < 0 || _taskObj.responseObj.ms1Ms2matchMzTolerance < 0 || _taskObj.responseObj.ms1Ms2matchRtTolerance < 0) {
        alert("MS tolerance and MS/MS tolerance, and RT must be positive");
        isFormPass = false;
    }

    if (_taskObj.responseObj.msmsTolerance > 100 || _taskObj.responseObj.msTolerance > 100 || _taskObj.responseObj.ms1Ms2matchMzTolerance > 100 || _taskObj.responseObj.ms1Ms2matchRtTolerance > 100 || _taskObj.responseObj.ms1Ms2matchMzTolerance > 100 || _taskObj.responseObj.ms1Ms2matchRtTolerance > 100) {
        alert("MS tolerance and MS/MS tolerance, and RT must be less than 100");
        isFormPass = false;
    }
    if (_taskObj.responseObj.similarityTolerance < 0 || _taskObj.responseObj.similarityTolerance > 1) {
        alert("MS2 similarity tolerance must be between 0 and 1");
        isFormPass = false;
    }

    if (!isFormPass) {
        document.getElementById("submitStatusLoading").classList.add("hidden")
        document.getElementById("submitStatusResult").innerText = "Fail, please check your input, and try again!";
        return;

    }

    //fetch api
    let responseObj = await fetchAPI.fetchDataByPOSTMethod("/api/batchSearch/task/submit", {"body": _taskObj.responseObj});
    if (responseObj.status !== 200) {
        document.getElementById("submitStatusResult").innerText = "Fail, please check your input, and try again!";
        document.getElementById("submitStatusLoading").classList.add("hidden");
        return
    }
    document.getElementById("submitStatusResult").innerText = "Success";

    setTimeout(function () {
        _taskObj.isFormSubmitting = false;
        window.location.href = "./taskView";
    }, 300);

    document.getElementById("submitStatusLoading").classList.add("hidden");

}


function displayResult(formData) {
    var resultDiv = document.getElementById("result");
    resultDiv.innerHTML = "<h2>表單送出的結果:</h2>";

    formData.forEach(function (value, key) {
        resultDiv.innerHTML += "<p><strong>" + key + ":</strong> " + value + "</p>";
    });
}


function checkTaskIdExit() {
    let url = new URL(window.location.href);
    let taskId = url.searchParams.get("taskId");
    return taskId != null;

}

async function updateFormByTaskId() {
    document.getElementById("loadingScreen").classList.remove("hidden")
    let url = new URL(window.location.href);
    let taskId = url.searchParams.get("taskId");
    let fetchData = await fetchAPI.fetchSpectrumDataByGetMethod("/api/batchSearch/task/" + taskId, {});
    if (fetchData === null || fetchData === undefined) {
        alert("Task ID not found");
        document.getElementById("loadingScreen").classList.add("hidden")
        return
    }
    if (fetchData.taskStatus !== 0) {
        alert("Task already submit or failed, please upload new file");
        document.getElementById("loadingScreen").classList.add("hidden")
        window.location.href = "/batchSearch";
        return
    }
    _taskObj.initialize();
    _taskObj.responseObj = fetchData;
    _taskObj.isFileUploaded = true;
    _taskObj.isFormSubmitted = false;
    _taskObj.isFormSubmitting = false;
    _taskObj.isFileUploading = false;
    _taskObj.isFormPass = false;

    //update form
    document.getElementById("mail").value = fetchData.mail;
    document.getElementById("taskIdResult").innerText = fetchData.id;
    document.getElementById("uploadStatusResult").innerText = "Success";
    document.getElementById("submitStatusResult").innerText = "Waiting for submit";
    document.getElementById("loadingScreen").classList.add("hidden")
    if (fetchData.taskDescription === null || fetchData.taskDescription === undefined || fetchData.taskDescription === "") fetchData.taskDescription = "N/A";
    document.getElementById("taskDescription").innerText = fetchData.taskDescription;

}

async function setupDemoDataPos() {
    document.getElementById("loadingScreen").classList.remove("hidden");
    let peakListInput = document.getElementById("peakListFile");
    let ms2SpectrumInput = document.getElementById("ms2File");

    let ms2FileRes = await fetch("demoData/POS/MS2Spectrum_LCMS_POS.mgf");
    let ms2FileResBob = await ms2FileRes.blob();
    let ms2FileList = new DataTransfer();
    let ms2File = new File([ms2FileResBob], "ms2Spectrum.mgf", {type: "text/mgf"});
    ms2FileList.items.add(ms2File);


    let peakListFileRes = await fetch("demoData/POS/PeakList_LCMS_POS.csv");
    let peakListFileBob = await peakListFileRes.blob();
    let peakListFileList = new DataTransfer();
    let peakListFile = new File([peakListFileBob], "peakListFile.csv", {type: "text/csv"});
    peakListFileList.items.add(peakListFile);


    peakListInput.files = peakListFileList.files;
    ms2SpectrumInput.files = ms2FileList.files;


    _taskObj.initialize();

    document.getElementById("submitStatusResult").innerText = "Waiting for submit";
    document.getElementById("uploadStatusResult").innerText = "Waiting for upload";
    document.getElementById("taskIdResult").innerText = "";


    //update form
    document.getElementById("taskDescription").innerText = "Demo data is ready, please enter your email, click upload file and submit button";
    document.getElementById("ms1ms2PairMzParameter").value = 20;
    document.getElementById("ms1ms2PairRtParameter").value = 30;
    document.getElementById("msTolerance").value = 15;
    document.getElementById("msmsTolerance").value = 30;
    document.getElementById("charge").value = "positive";
    document.getElementById("forward_para").value = 0.5;
    document.getElementById("reverse_para").value = 0.5;
    document.getElementById("ms2_similarity_para").value = 0.8;


    document.getElementById("loadingScreen").classList.add("hidden")


}