document.querySelectorAll('.spectrumChart').forEach(e => {
    createChartBySpectrumData(null, null, e);
});


function createChartBySpectrumData(refSpectrumData, expSpectrumData, canvasElement) {
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