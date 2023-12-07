

class FetchAPI {

    generateGetUrlByParameter(getParameterObj, apiUrl) {
        // type getParameterObj:object = {
        //     parameter1: null,
        //     parameter2: null,
        //     parameter3: null,
        // }
        //  type apiUrl:string

        if (getParameterObj === null || apiUrl === null || getParameterObj.isPassCheck === false){
            alert("Please check your input data")
            return null
        }

        apiUrl = apiUrl + "?";
        let paraArr = Object.keys(getParameterObj)
        //generate the url
        for (let i = 0; i < paraArr.length; i++) {
            if (getParameterObj[paraArr[i]] === null || paraArr[i]==="isPassCheck") {
                continue
            }
            apiUrl += "&"
            apiUrl += paraArr[i] + "=" + getParameterObj[paraArr[i]]
        }

        return apiUrl
    }

    async fetchSpectrumDataByGetMethod(url, requestParaObj){
        requestParaObj["method"] = "GET"
        let data = await fetch(url, requestParaObj)
        //let dataParse = await data.json()
        return data
    }

    async fetchDataByPOSTMethod(url, requestParaObj){
        requestParaObj["method"] = "POST"
        requestParaObj["headers"] = {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
        requestParaObj["body"] = JSON.stringify(requestParaObj["body"])
        return await fetch(url, requestParaObj)
    }



    async sentFormDataByPostMethod(url, formData){
        try {
            let response = await fetch(url, {
                method: "POST",
                body: formData
            })
            return response

        } catch (e) {
            alert(e.message)
        }
    }
    async sentDeleteRequest(url, requestParaObj){
        requestParaObj["method"] = "DELETE"
        return await fetch(url, requestParaObj)
    }

}



export default FetchAPI;