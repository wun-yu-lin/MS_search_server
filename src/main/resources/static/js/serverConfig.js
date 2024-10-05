import FetchAPI from "./utility/FetchAPI.js";

const fetchAPI = new FetchAPI();

const CLICK = "click"
const responseBox = document.getElementById('responseDisplay');
const serverTokenInput = document.getElementById("serverToken");
const reloadBtn = document.getElementById('reloadBtn');
const fetchConfigBtn = document.getElementById('fetchConfigBtn');
const requestInput = document.getElementById("requestJson");
const updateConfigBtn = document.getElementById("updateConfigBtn");

main()

function main() {
    loadServerConfigEventListener()
}

function loadServerConfigEventListener() {
    addListener(fetchConfigBtn, CLICK, fetchServerConfig);
    addListener(reloadBtn, CLICK, fetchForReloadServer);
    addListener(updateConfigBtn, CLICK, fetchForUpdateConfig);
}

function addListener(element, eventType, cb) {
    element.addEventListener(eventType, event => {
        cb(event);
    });
}

function checkServerToken(serverToken){
    if (!serverToken) {
        alert('Please enter your server token');
        throw new Error("Please enter your server token");
    }
}

function genServerReqBody() {
    const serverToken = serverTokenInput.value;
    checkServerToken(serverToken);
    const reqBody = {"serverTokenFromMember": serverToken};
    try {
        if (requestInput.value) {
            reqBody["serverConfig"] = JSON.parse(requestInput.value)['serverConfig'];
        }
    } catch {
        alert("Parse JSON error, please confirm JSON format! ");
        throw new Error("JSON parse error");
    }
    return reqBody
}


async function fetchForUpdateConfig(){
    const resp = await fetchAPI.fetchDataByPOSTMethod(
        "/api/config/server/update", {"body": genServerReqBody()}
    );
    await handleRespStatus(resp, false, "Update config");
}

async function fetchForReloadServer(){
    const resp = await fetchAPI.fetchDataByPOSTMethod(
        "/api/config/server/reload", {"body": genServerReqBody()}
    );
    await handleRespStatus(resp, false, "Reload server");
}

async function fetchServerConfig(event) {
    const resp = await fetchAPI.fetchDataByPOSTMethod(
        "/api/config/server/get", {"body": genServerReqBody()}
    );
    await handleRespStatus(resp, true, "Get Server config");
}

async function handleRespStatus(resp, respBodyIsJson, message){
    if (resp.status === 200) {
        if (respBodyIsJson){
            const data = await resp.json();
            responseBox.textContent = JSON.stringify(data);
        }else{
            responseBox.textContent =  message + " success! " + await resp.text();
        }
    } else {
        responseBox.textContent = message + " fail! " + await resp.text();
    }
}