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

    setTimeout(function () {
        document.getElementById("submitStatusLoading").classList.add("hidden")
    }, 3000);

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
