const deleteButton = document.getElementById('delete-btn');
if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;

        function success() {
            alert("삭제가 되었습니다. ");
            location.replace("/articles");
        }

        function fail() {
            alert("삭제 실패했습니다.");
            location.replace("/articles");
        }

        httpRequest("DELETE", `/api/articles/${id}`, null, success, fail);
    });
}

const modifyButton = document.getElementById('modify-btn')
if (modifyButton) {
    modifyButton.addEventListener('click', ev => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');
        let body = JSON.stringify({
            title: document.getElementById('title').value, content: document.getElementById('content').value
        });

        function success() {
            alert('수정이 완료되었습니다. ');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('수정이 실패했습니다. ');
            location.replace(`/articles/${id}`);
        }

        httpRequest("PUT", `/api/articles/${id}`, body, success, fail);
    });
}

const createButton = document.getElementById("create-btn");
console.log("createButton")


if (createButton) {
    createButton.addEventListener("click", (event) => {
        let body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });

        function success() {
            alert("등록이 완료되었습니다.");
            location.replace("/articles");
        }

        function fail() {
            alert("등록 실패했습니다.");
            location.replace("/articles");
        }

        httpRequest("POST", "/api/articles", body, success, fail);
    });
}

function getCookies(key) {
    let result = null;
    let cookie = document.cookie.split(";");
    cookie.some(function (item) {
        item = item.replace(" ", "");

        let dic = item.split("=");

        if (key === dic[0]) {
            result = dic[1];
            return true
        }
    });

    return result;
}

function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            Authorization: "Bearer " + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body: body
    }).then((response) => {
        if (response.status === 200 || response.status === 201) {
            return success()
        }
        const refresh_token = getCookies("refresh_token");
        if (response.status === 401 && refresh_token) {
            fetch(`/api/token`, {
                method: 'POST',
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: getCookies("refresh_token"),
                })
            }).then((res) => {
                if (res.ok) {
                    return res.json();
                }
            }).then((result) => {
                localStorage.setItem("access_token", result.accessToken)
                httpRequest(method, url, body, success, fail);
            }).catch((error) => fail());
        } else {
            return fail();
        }
    });


}