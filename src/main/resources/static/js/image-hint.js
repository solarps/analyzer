document.addEventListener('DOMContentLoaded', function () {
    var imageContainer = document.querySelector('.image-container');

    imageContainer.style.opacity = '0';

    imageContainer.previousElementSibling.addEventListener('mouseover', function () {
        imageContainer.style.opacity = '1';
    });

    imageContainer.previousElementSibling.addEventListener('mouseout', function () {
        imageContainer.style.opacity = '0';
    });
});

var urlParams = new URLSearchParams(window.location.search);
var message = urlParams.get('message');

// Очистить URL-параметр 'message' при обновлении страницы
if (message) {
    history.replaceState({}, document.title, window.location.pathname);
    alert(message);
}
