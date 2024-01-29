const deleteButton = document.getElementById('delete-btn');
if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {

            method: 'DELETE'

        }).then(() => {
            alert("삭제가 되었습니다. ");
            location.replace("/articles");
        })
    })
}

const modifyButton = document.getElementById('modify-btn')
if (modifyButton) {
    modifyButton.addEventListener('click', ev => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT', headers: {
                "Content-Type": "application/json",
            }, body: JSON.stringify({
                title: document.getElementById('title').value, content: document.getElementById('content').value
            })

        }).then(() => {
            alert('수정이 완료되었습니다. ');
            location.replace(`/articles/${id}`);
        });
    });
}

const createButton = document.getElementById("create-btn");
console.log("createButton")
if (createButton) {
    createButton.addEventListener("click", event => {
        console.log("click");
        fetch(`/api/articles`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value
            })
        }).then(() => {
            alert("등록 완료되었습니다.");
            location.replace("/articles");
        });
    });
}
