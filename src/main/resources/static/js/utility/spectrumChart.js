

class SpectrumChart {


    createComparisonChartBySpectrumData(expSpectrumDataArr,refSpectrumDataArr, canvasElement) {
        if (refSpectrumDataArr.length === 0 || refSpectrumDataArr === undefined ||refSpectrumDataArr === null ||typeof(refSpectrumDataArr) !== 'object'){return;}
        if (expSpectrumDataArr.length === 0 || expSpectrumDataArr === undefined ||expSpectrumDataArr === null ||typeof(expSpectrumDataArr) !== 'object'){return;}
        let dataRefArray, dataExpArray
        function prepareDataForChart() {
            let dataDev = 1; //定義每隔多少放一個空值
            let arrRefMax = Math.max(...refSpectrumDataArr.map(e=>e[0]));
            let arrRefMin =Math.min(...refSpectrumDataArr.map(e=>e[0]));
            let arrExpMax = Math.max(...expSpectrumDataArr.map(e=>e[0]));
            let arrExpMin = Math.min(...expSpectrumDataArr.map(e=>e[0]));
            let arrMax = Math.max(arrRefMax,arrExpMax);
            let arrRefLen = refSpectrumDataArr.length;
            let arrExpLen = expSpectrumDataArr.length;
            refSpectrumDataArr.sort(function (a,b){return a[0] - b[0]})
            expSpectrumDataArr.sort(function (a,b){return a[0] - b[0]})
            dataExpArray = new Array(  Math.ceil(arrMax/dataDev)+arrRefLen+arrExpLen)
            dataRefArray = new Array(  Math.ceil(arrMax/dataDev)+arrRefLen+arrExpLen)


            let refDataPointer = 0;
            let expDataPointer = 0
            let currExpX= expSpectrumDataArr[0][0]
            let currRefX= refSpectrumDataArr[0][0];
            let xData = 0.0;
            for (let i=0; i < dataRefArray.length; i++){
                if (xData < currRefX && xData < currExpX){
                    // < 放入 空值
                    dataRefArray[i] = [xData,0]
                    dataExpArray[i] = [xData,0]
                    xData+=dataDev
                }else if( currExpX<= currRefX && expDataPointer+1 <= expSpectrumDataArr.length){
                    //放入實驗值
                    dataExpArray[i] = expSpectrumDataArr[expDataPointer]
                    dataExpArray[i][0] = dataExpArray[i][0]
                    dataExpArray[i][1] = dataExpArray[i][1]
                    dataRefArray[i] = []
                    dataRefArray[i][0] = expSpectrumDataArr[expDataPointer][0]
                    dataRefArray[i][1] = (0)
                    expDataPointer++
                    if(expDataPointer < expSpectrumDataArr.length){
                        currExpX = expSpectrumDataArr[expDataPointer][0]
                    }

                }else if(refDataPointer+1 <= refSpectrumDataArr.length) {

                    dataRefArray[i] = refSpectrumDataArr[refDataPointer]
                    dataRefArray[i][0] = dataRefArray[i][0]
                    dataRefArray[i][1] = (dataRefArray[i][1]*-1)
                    dataExpArray[i] = []
                    dataExpArray[i][0] = refSpectrumDataArr[refDataPointer][0]
                    dataExpArray[i][1] = (0)
                    refDataPointer++
                    if(refDataPointer < refSpectrumDataArr.length){
                        currRefX = refSpectrumDataArr[refDataPointer][0]
                    }
                }
                else{
                    dataRefArray[i] = [xData,0] //剩餘放入空值
                    dataExpArray[i] = [xData,0] //剩餘放入空值
                }

            }

        }
        prepareDataForChart()
        let chartRefData = dataRefArray
        let chartExpData = dataExpArray
        new Chart(canvasElement, {
            type: 'bar',
            data: {
                labels: chartExpData.map(row => row[0].toFixed(2)),
                datasets: [
                    {
                        label: `Experiment MS/MS spectrum`,
                        data: chartExpData.map(row => row[1].toFixed(2)),
                    },
                    {
                        label: `Reference MS/MS spectrum`,
                        data: chartRefData.map(row => row[1].toFixed(2)),
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'MS/MS spectrum'
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'm/z',
                            font: {
                                size: 12
                            }
                        },
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: false
                        },
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Intensity abundance (%)',
                            font: {
                                size: 12
                            }
                        },
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: true
                        },
                    },
                }
            }
        });

    }


    createChartBySpectrumData(refSpectrumDataArr, canvasElement) {
        if (refSpectrumDataArr.length === 0 ||
            refSpectrumDataArr === undefined ||
            refSpectrumDataArr === null ||
            typeof(refSpectrumDataArr) !== 'object'
        ){
            return;
        }
        function prepareDataForChart() {
            let dataDev = 2.0; //定義每隔多少放一個空值
            let arrMax = Math.max(...refSpectrumDataArr.map(e=>e[0]));
            let arrMin =Math.min(...refSpectrumDataArr.map(e=>e[0]));
            let arrLen = refSpectrumDataArr.length;
            refSpectrumDataArr.sort(function (a,b){return a[0] - b[0]})
            let dataArray = new Array(  Math.ceil(arrMax/dataDev)+arrLen)


            let refDataPointer = 0;
            let xData = 0.0;
            for (let i=0; i < dataArray.length; i++){
                if (xData < refSpectrumDataArr[refDataPointer][0]){
                    // < 放入 空值
                    dataArray[i] = [xData.toFixed(4),0]
                    xData+=dataDev
                }else if(xData >= refSpectrumDataArr[refDataPointer][0] && refDataPointer+1 <= refSpectrumDataArr.length){

                    dataArray[i] = refSpectrumDataArr[refDataPointer]
                    dataArray[i][0] = dataArray[i][0].toFixed(4)
                    dataArray[i][1] = dataArray[i][1].toFixed(4)
                    refDataPointer++

                }else if(refDataPointer+1 > refSpectrumDataArr.length){
                    dataArray[i] = [xData.toFixed(4),0] //剩餘放入空值
                }

            }

            return dataArray
        }

        let chartData = prepareDataForChart()
        new Chart(canvasElement, {
            type: 'bar',
            data: {
                labels: chartData.map(row => row[0]),
                datasets: [
                    {
                        label: `Reference MS/MS spectrum`,
                        data: chartData.map(row => row[1]),
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'MS/MS spectrum'
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'm/z',
                            font: {
                                size: 12
                            }
                        },
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: false
                        },
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Intensity abundance (%)',
                            font: {
                                size: 12
                            }
                        },
                        ticks: {
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            display: true
                        },
                    },
                }
            }
        });

    }

}




export default SpectrumChart;


