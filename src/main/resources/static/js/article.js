const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        console.log("hou!!!!!!!!!!!");
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {

            method: 'DELETE'

        }).then(() => {
            alert("삭제가 되었습니다. ");
            location.replace("/articles");
        })
    })
}