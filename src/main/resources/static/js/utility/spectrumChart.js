

class SpectrumChart {

    createComparisonChartBySpectrumData(refSpectrumData, expSpectrumData, canvasElement) {
        let xArray, yRefArray, yExpArray = prepareArray(refSpectrumData, expSpectrumData);

        let chartObject = new Chart(canvasElement, {
            type: 'bar',
            data: {
                labels: testData.map(row => row[0]),
                datasets: [
                    {
                        label: `Reference MS/MS spectrum`,
                        data: testData.map(row => row[1]),
                    },
                    {
                        label: `Experiment MS/MS spectrum`,
                        data: testData.map(row => row[1] * -1),
                    },
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
                        text: 'MS/MS spectrum comparison'
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

        function prepareArray(refSpectrumData, expSpectrumData) {
            let xArray = [];
            let yRefArray = [];
            let yExpArray = [];

            ///not implemented yet


            return xArray, yRefArray, yExpArray;
        }


    }


    createChartBySpectrumData(refSpectrumDataArr, canvasElement) {
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



// let spectrumCart = new SpectrumChart();
//
//
// document.querySelectorAll('.spectrumChart').forEach(e => {
//     spectrumCart.createChartBySpectrumData(data,e)
// });


export default SpectrumChart;


