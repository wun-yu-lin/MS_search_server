import FetchAPI from "./utility/FetchAPI.js"

const fetchAPI = new FetchAPI();

baseMain();

async function baseMain() {
    document.getElementById("loadingScreen").classList.remove("hidden")
    document.getElementById("msSearch").onkeydown = sentSearchKeyToAPI;
    document.getElementById("header_search_button").onclick = sentSearchKeyToAPI;
    document.getElementById("loadingScreen").classList.add("hidden")
    if (await isAuth()) {
        await changeToLogInStatus();
    } else {
        changeToLogOutStatus();
    }
}

async function sentSearchKeyToAPI(event) {
    if (event.key === "Enter" || event.type === "click") {
        const searchObj = {
            "keyWord": document.getElementById("msSearch").value
        }
        console.log(searchObj)
        let url = window.location.origin + "/msSearch?keyWord=" + searchObj.keyWord;
        window.location.href = url;
    }
}

async function isAuth() {
    const url = window.location.origin + "/api/member/auth";
    const response = await fetch(url, {method: "GET"})
    if (response.status === 200) {
        return true;
    } else {
        return false;
    }
}

async function changeToLogInStatus() {
    document.getElementById("logOut_ul").style.display = "block";
    let response = await fetch("/api/member", {method: "GET"})
    let memberInfo = await response.json();
    let spanName = document.createElement("span");
    let img = document.createElement("img");
    img.src = memberInfo.pictureSrc;
    spanName.innerText = memberInfo.name;
    document.getElementById("profile_div").appendChild(img);
    document.getElementById("profile_div").appendChild(spanName);

}

function changeToLogOutStatus(){
    document.getElementById("logOut_ul").style.display = "none";
    document.getElementById("profile_div").innerHTML = "";
}

