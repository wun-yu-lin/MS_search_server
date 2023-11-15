document.getElementById("loadingScreen").classList.remove("hidden")
document.getElementById("loadingScreen").classList.add("hidden")


function submitForm() {
    var formData = new FormData(document.getElementById("myForm"));

    // 在這裡可以使用 AJAX 或其他方式將表單數據送到伺服器
    // 這裡只是一個簡單的示例，將結果顯示在網頁上
    displayResult(formData);
}

function displayResult(formData) {
    var resultDiv = document.getElementById("result");
    resultDiv.innerHTML = "<h2>表單送出的結果:</h2>";

    formData.forEach(function(value, key) {
        resultDiv.innerHTML += "<p><strong>" + key + ":</strong> " + value + "</p>";
    });
}
