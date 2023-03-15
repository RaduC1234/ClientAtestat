document.addEventListener("engineReady", function () {
    Gui.loadSideBar();
});

document.addEventListener("admin-usersReady", function () {
    Gui.requestAdminReadUsers();
});