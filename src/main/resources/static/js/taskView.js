import FetchAPI from "./utility/FetchAPI.js";

const fetchAPI = new FetchAPI();

let _pageStatusObj = {
    "isFirstLoad": false,
    "isNextPage": false,
    "nextPageSpectrumInit": 0,
    "fetchUrl": null,
    "isFetching": false,
    initialPara() {
        this.isFirstLoad = false
        this.isNextPage = false
        this.nextPageSpectrumInit = 0
        this.fetchUrl = null
        this.isFetching = false

    }
};


main();
asyncMain();


function main() {
    _pageStatusObj.initialPara();

}

async function asyncMain() {
    let taskList = await getTaskListByApiAndUpdatePageStatus();
    taskList.forEach((task) => {
            console.log(task);
            createTableByTaskListData(task)
        }
    )

}


function taskStatusMap(statusCode) {
    statusCode = parseInt(statusCode);
    switch (statusCode) {
        case 0:
            return [0,"No submit"];
        case 1:
            return [1,"Waiting"];
        case 2:
            return [2,"Processing"];
        case 3:
            return [3,"Finished"];
        case 4:
            return [4,"Task error"];
        default:
            return [4,"Task error"];
    }
}


async function getTaskListByApiAndUpdatePageStatus() {

    _pageStatusObj.fetchUrl = "/api/batchSearch/task";
    let paraObj = {
        "authodId": "0",
        "taskInit": _pageStatusObj.nextPageSpectrumInit,
        "taskOffset": 30
    }
    let url = fetchAPI.generateGetUrlByParameter(paraObj, _pageStatusObj.fetchUrl);
    _pageStatusObj.isFetching = true;
    _pageStatusObj.isFirstLoad = true;
    let data = await fetchAPI.fetchSpectrumDataByGetMethod(url, {});
    _pageStatusObj.isFetching = false;

    if (data == null) {
        return []
    }
    if (data.length === 0) {
        return []
    }
    if (data.length >= 30) {
        _pageStatusObj.isNextPage = true;
        _pageStatusObj.nextPageSpectrumInit = _pageStatusObj.nextPageSpectrumInit + data.length;
    }

    return data;


}

function createTableByTaskListData(taskObj) {

    function preparationData(taskObj) {
        let keys = Object.keys(taskObj);
        for (let key of keys) {
            if (taskObj[key] == null) {
                taskObj[key] = "N/A";
            }
        }
    }

    preparationData(taskObj);
    taskObj["taskStatus"] = taskStatusMap(taskObj["taskStatus"]);

    let tableBody = document.getElementById("table-body");
    let tr = document.createElement("tr");

    let tdId = document.createElement("td")
    tdId.innerText = taskObj.id;

    let tdTaskStatus = document.createElement("td")
    tdTaskStatus.innerText = taskObj.taskStatus[1];
    switch (taskObj.taskStatus[0]){
        // case 0:
        //     return [0,"No submit"];
        // case 1:
        //     return [1,"Submit in waiting"];
        // case 2:
        //     return [2,"Processing"];
        // case 3:
        //     return [3,"Task finished"];
        // case 4:
        //     return [4,"Task error"];
        // default:
        //     return [4,"Task error"];
        case 0:
            tdTaskStatus.style.color = "Black";
            break
        case 1:
            tdTaskStatus.style.color = "Green";
            break
        case 2:
           tdTaskStatus.style.color = "Green";
           break
        case 3:
           tdTaskStatus.style.color = "Blue";
           break
        case 4:
           tdTaskStatus.style.color = "Red";
           break

    }

    let tdPeakList = document.createElement("td")
    tdPeakList.innerHTML = `<a href=${taskObj.s3PeakListSrc}>${taskObj.s3PeakListSrc.split(".net/")[1]}</a>`

    let tdMs2File = document.createElement("td")
    tdMs2File.innerHTML = `<a href=${taskObj.s3Ms2FileSrc}>${taskObj.s3Ms2FileSrc.split(".net/")[1]}</a>`

    let tdResult = document.createElement("td")
    if (taskObj.s3ResultsSrc === "N/A") {
        tdResult.innerText = taskObj.s3ResultsSrc
    }else{
        tdResult.innerHTML = ` <a href=${taskObj.s3ResultsSrc}>${taskObj.s3ResultsSrc.split(".net/")[1]}</a>`
    }


    let tdIonMode = document.createElement("td")
    tdIonMode.innerText = taskObj.ionMode;

    let tdEmail = document.createElement("td")
    tdEmail.innerText = taskObj.mail;

    let tdDataSource = document.createElement("td")
    tdDataSource.innerText = taskObj.ms2spectrumDataSource;

    let tdCreateTime = document.createElement("td")
    tdCreateTime.innerText = taskObj.createTime;

    let tdFinishTime = document.createElement("td")
    tdFinishTime.innerText = taskObj.finishTime;

    let tdDelete = document.createElement("td")
    let deleteButton = document.createElement("button")
    tdDelete.appendChild(deleteButton);
    deleteButton.id = taskObj.id;
    deleteButton.innerText = "Delete"
    deleteButton.onclick = onclickDeleteTaskById;

    tr.appendChild(tdId);
    tr.appendChild(tdTaskStatus);
    tr.appendChild(tdPeakList);
    tr.appendChild(tdMs2File);
    tr.appendChild(tdResult);
    tr.appendChild(tdIonMode);
    tr.appendChild(tdEmail);
    tr.appendChild(tdDataSource);
    tr.appendChild(tdCreateTime);
    tr.appendChild(tdFinishTime);
    tr.appendChild(tdDelete);

    tableBody.appendChild(tr);


    // tr.innerHTML = `
    //   <tr>
    //     <td>${taskObj.id}</td>
    //     <td>${taskObj.taskStatus}</td>
    //     <td><a href=${taskObj.s3PeakListSrc}>${taskObj.s3PeakListSrc.split(".net/")[1]}</a></td>
    //     <td><a href=${taskObj.s3Ms2FileSrc}>${taskObj.s3Ms2FileSrc.split(".net/")[1]}</a></td>
    //     <td><a href=${taskObj.s3ResultsSrc}>${taskObj.s3ResultsSrc.split(".net/")[1]}</a></td>
    //     <td>${taskObj.ionMode}</td>
    //     <td>${taskObj.email}</td>
    //     <td>${taskObj.ms2spectrumDataSource}</td>
    //     <td>${taskObj.createTime}</td>
    //     <td>${taskObj.finishTime}</td>
    //   </tr>
    // `
    // tableBody.appendChild(tr);


}


async function onclickDeleteTaskById(event) {
    let url = "/api/batchSearch/task/" + event.target.id;
    let response = await fetchAPI.sentDeleteRequest(url, {});
    if (response.status === 200) {
        window.location.reload();

    }
}