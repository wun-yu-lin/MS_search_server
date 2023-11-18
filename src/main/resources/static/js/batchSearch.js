// document.getElementById("loadingScreen").classList.remove("hidden")
// document.getElementById("loadingScreen").classList.add("hidden")
// document.getElementById("submitStatusLoading").classList.remove("hidden")
// document.getElementById("uploadStatusLoading").classList.remove("hidden")

import FetchAPI from "./utility/FetchAPI.js";

const fetchAPI = new FetchAPI();
let _taskObj = {
    "responseObj": {},
    "isFileUploaded": false,
    "isFormSubmitted": false,
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
    _taskObj.initialize();
}

async function asyncMain() {

}


async function uploadFile(event) {
    event.preventDefault();
    _taskObj.initialize();
    document.getElementById("uploadStatusResult").innerText = "";
    document.getElementById("uploadStatusLoading").classList.remove("hidden")

    let responseObj = await fetchAPI.sentFormDataByPostMethod("/api/batchSearch/file/upload", new FormData(document.getElementById("myForm")));
    let fetchData = await responseObj.json();
    _taskObj.responseObj = fetchData;
    _taskObj.isFileUploaded = true;
    console.log(fetchData)
    if (typeof(responseObj) == 'object' && responseObj.status == 200){
        document.getElementById("uploadStatusResult").innerText = "Success";
        document.getElementById("taskIdResult").innerText = fetchData.id;
    }else{
        alert("Upload failed, please check your file")
        document.getElementById("uploadStatusResult").innerText = "Fail";
    }

    document.getElementById("uploadStatusLoading").classList.add("hidden")

}


async function submitForm() {
    if (!_taskObj.isFileUploaded) {alert("請先上傳檔案"); return;}
    document.getElementById("submitStatusLoading").classList.remove("hidden")
    _taskObj.responseObj.forwardWeight = parseFloat(document.getElementById("forward_para").value);
    _taskObj.responseObj.reverseWeight = parseFloat(document.getElementById("reverse_para").value);
    _taskObj.responseObj.ionMode = document.getElementById("charge").value;
    _taskObj.responseObj.mail = document.getElementById("mail").value;
    _taskObj.responseObj.msTolerance = parseFloat(document.getElementById("msTolerance").value);
    _taskObj.responseObj.msmsTolerance = parseFloat(document.getElementById("msmsTolerance").value);
    _taskObj.responseObj.similarityAlgorithm = document.getElementById("algorithm_para").value;
    _taskObj.responseObj.similarityTolerance = parseFloat(document.getElementById("ms2_similarity_para").value);

    console.log(_taskObj.responseObj)
    let Keys = Object.keys(_taskObj.responseObj);
    for (let i = 0; i < Keys.length; i++) {

        if (_taskObj.responseObj[Keys[i]] === "") {
            _taskObj.responseObj[Keys[i]] = null;
        }
        if (isNaN(_taskObj.responseObj[Keys[i]]) && typeof(_taskObj.responseObj[Keys[i]]) != "string") {
            _taskObj.responseObj[Keys[i]] = null;
        }
    }
    if (_taskObj.responseObj.forwardWeight == null && _taskObj.responseObj.reverseWeight == null) {
        _taskObj.responseObj.forwardWeight = 0.5;
        _taskObj.responseObj.reverseWeight = 0.5;
    }
    if(_taskObj.responseObj.similarityTolerance == null){
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
    if (_taskObj.responseObj.msmsTolerance < 0 || _taskObj.responseObj.msTolerance < 0) {
        alert("MS tolerance and MS/MS tolerance must be positive");
        isFormPass = false;
    }

    if (_taskObj.responseObj.msmsTolerance > 100 || _taskObj.responseObj.msTolerance >100) {
        alert("MS tolerance and MS/MS tolerance must be less than 100");
        isFormPass = false;
    }
    if (_taskObj.responseObj.similarityTolerance < 0 || _taskObj.responseObj.similarityTolerance > 1) {
        alert("MS2 similarity tolerance must be between 0 and 1");
        isFormPass = false;
    }

    if (!isFormPass){
        document.getElementById("submitStatusLoading").classList.add("hidden")
        document.getElementById("submitStatusResult").innerText = "Fail, please check your input, and try again!";
        return;

    }

    //fetch api
    let responseObj = await fetchAPI.fetchDataByPOSTMethod("/api/batchSearch/task/submit", {"body": _taskObj.responseObj});
    if (responseObj.status !== 200){
        document.getElementById("submitStatusResult").innerText = "Fail, please check your input, and try again!";
        document.getElementById("submitStatusLoading").classList.add("hidden");
        return
    }
    document.getElementById("submitStatusResult").innerText = "Success";




    document.getElementById("submitStatusLoading").classList.add("hidden");

}


// function submitForm() {
//     var formData = new FormData(document.getElementById("myForm"));
//
//     // 在這裡可以使用 AJAX 或其他方式將表單數據送到伺服器
//
//
//
//     // 這裡只是一個簡單的示例，將結果顯示在網頁上
//     displayResult(formData);
// }

function displayResult(formData) {
    var resultDiv = document.getElementById("result");
    resultDiv.innerHTML = "<h2>表單送出的結果:</h2>";

    formData.forEach(function (value, key) {
        resultDiv.innerHTML += "<p><strong>" + key + ":</strong> " + value + "</p>";
    });
}
