

class FetchAPI {

    generateGetUrlByParameter(getParameterObj, apiUrl) {
        // type getParameterObj:object = {
        //     parameter1: null,
        //     parameter2: null,
        //     parameter3: null,
        // }
        //  type apiUrl:string

        if (getParameterObj.isPassCheck === false || apiUrl === null || getParameterObj === null) {
            alart("Please check your input data")
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
        let data = await fetch(url, requestParaObj)
        let dataParse = await data.json()
        return dataParse
    }

}



export default FetchAPI;