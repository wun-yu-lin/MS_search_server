import FetchAPI from "./utility/FetchAPI.js"

const fetchAPI = new FetchAPI();

baseMain();

async function baseMain() {
    document.getElementById("msSearch").onkeydown = sentSearchKeyToAPI;
    document.getElementById("header_search_button").onclick = sentSearchKeyToAPI;
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