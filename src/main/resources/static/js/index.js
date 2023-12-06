asyncMain();



async function asyncMain() {
    let response = await fetch('./api/webStatus', {method: 'GET'});
    if (response.status === 200) {
        let data = await response.json();
        document.getElementById('registeredCount_span').innerText = data.userCount;
        document.getElementById('submittedCount_span').innerText = data.taskCount;
    }
}