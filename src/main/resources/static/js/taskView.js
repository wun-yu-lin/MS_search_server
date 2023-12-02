import FetchAPI from "./utility/FetchAPI.js";

const fetchAPI = new FetchAPI();

let _pageStatusObj = {
    "isFirstLoad": false,
    "isNextPage": false,
    "authId": 0,
    "nextPageSpectrumInit": 0,
    "fetchUrl": null,
    "isFetching": false,
    initialPara() {
        this.isFirstLoad = false
        this.isNextPage = false
        this.nextPageSpectrumInit = 0
        this.fetchUrl = null
        this.isFetching = false
        this.authId = 0


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
            createTableByTaskListData(task)
        }
    )
    createScrollDownObserverForNextPage();

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
    _pageStatusObj.paraObj = paraObj;
    _pageStatusObj.authId = paraObj.authodId;
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
    let tdFileSource = document.createElement("td")
    let pPeakList = document.createElement("p")
    pPeakList.innerHTML = `<span>MS1 file:</span>  <a href=${taskObj.s3PeakListSrc}>${taskObj.s3PeakListSrc.split(".net/")[1]}</a>`

    let pMs2File = document.createElement("p")
    pMs2File.innerHTML = `<span>MS2 file:</span>  <a href=${taskObj.s3Ms2FileSrc}>${taskObj.s3Ms2FileSrc.split(".net/")[1]}</a>`

    let pResult = document.createElement("p")
    if (taskObj.s3ResultsSrc === "N/A") {
        pResult.innerHTML = `<span>Result:</span> ${taskObj.s3ResultsSrc}`
    }else{
        pResult.innerHTML = `<span>Result:</span> <a href=${taskObj.s3ResultsSrc}>${taskObj.s3ResultsSrc.split(".net/")[1]}</a>`
    }
    tdFileSource.appendChild(pPeakList);
    tdFileSource.appendChild(pMs2File);
    tdFileSource.appendChild(pResult);


    let tdIonMode = document.createElement("td")
    if(taskObj.ionMode == null || taskObj.ionMode === "" || taskObj.ionMode==="N/A"){
        taskObj.ionMode = "all"
    }
    tdIonMode.innerText = taskObj.ionMode;

    let tdEmail = document.createElement("td")
    tdEmail.innerText = taskObj.mail;
    let tdDes = document.createElement("td")
    if (taskObj.taskDescription === ""){
        taskObj.taskDescription = "N/A"
    }
    tdDes.innerText = taskObj.taskDescription;

    let tdDataSource = document.createElement("td")
    tdDataSource.innerText = taskObj.ms2spectrumDataSource;

    let tdCreateTime = document.createElement("td")
    if (taskObj.createTime === null || taskObj.createTime === "" ||  taskObj.createTime === "N/A") {
        tdCreateTime.innerText = taskObj.createTime;
    }else {
        tdCreateTime.innerText = new Date(taskObj.createTime).toLocaleString('zh-Hans');
    }

    let tdFinishTime = document.createElement("td")
    if (taskObj.finishTime === null || taskObj.finishTime === "" ||  taskObj.finishTime === "N/A") {
        tdFinishTime.innerText = taskObj.finishTime;
    }else {
        tdFinishTime.innerText = new Date(taskObj.finishTime).toLocaleString('zh-Hans');
    }

    let tdTaskOp = document.createElement("td")
    let deleteButton = document.createElement("button")
    tdTaskOp.appendChild(deleteButton);
    deleteButton.id = taskObj.id;
    deleteButton.innerText = "Delete"
    deleteButton.onclick = onclickDeleteTaskById;
    if (taskObj.taskStatus[0] === 0) {
        let getSubmitButton = document.createElement("button")
        getSubmitButton.id = taskObj.id;
        getSubmitButton.innerText = "Go to submit"
        getSubmitButton.onclick = function (event) {
            window.location.href = "/batchSearch?taskId=" + event.target.id;
        }
        tdTaskOp.appendChild(getSubmitButton);


    }

    tr.appendChild(tdId);
    tr.appendChild(tdTaskStatus);
    tr.appendChild(tdFileSource);
    tr.appendChild(tdIonMode);
    tr.appendChild(tdEmail);
    tr.appendChild(tdDes);
    tr.appendChild(tdDataSource);
    tr.appendChild(tdCreateTime);
    tr.appendChild(tdFinishTime);
    tr.appendChild(tdTaskOp);

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


function createScrollDownObserverForNextPage(){
    let scrollDownObserver = new IntersectionObserver(async(entries, observe)=>{
        if(entries[0].intersectionRatio < 1.1 && entries[0].isIntersecting > 0.1){
            let targetElement = entries[0].target;
            observe.unobserve(entries[0].target);
            if(_pageStatusObj.isFetching === true) return observe.observe(targetElement);
            if(_pageStatusObj.isNextPage === false) return observe.observe(targetElement);
            if(_pageStatusObj.isFirstLoad === false) return observe.observe(targetElement);
            //add action of loading
            let url = fetchAPI.generateGetUrlByParameter({"authId":_pageStatusObj.authId, "taskInit":_pageStatusObj.nextPageSpectrumInit}, _pageStatusObj.fetchUrl);
            console.log(url)
            let fetchData = await fetchAPI.fetchSpectrumDataByGetMethod(url, {"method": "GET"});
            if (typeof(fetchData) === "object") {
                _pageStatusObj.nextPageSpectrumInit = _pageStatusObj.nextPageSpectrumInit + fetchData.length
                console.log(_pageStatusObj.nextPageSpectrumInit)
                if (fetchData.length >=30) {
                    _pageStatusObj.isNextPage = true
                }else {
                    _pageStatusObj.isNextPage = false
                }
            }

            if (fetchData == null) {
            } else if (fetchData.length === 0) {
                return
            } else {
                fetchData.forEach(function (item) {
                    createTableByTaskListData(item)
                });
                return observe.observe(targetElement);

            }

            observe.observe(targetElement);
        }




    })
    scrollDownObserver.observe(document.querySelector(".footer"));
}