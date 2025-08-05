import { getToken, updatePageState } from "./helpers.js";
import router from "./router.js";
import homeHandler from "./handlers/home.js";
import userHandler from "./handlers/user.js";
import usersHandler from "./handlers/users.js";
import courtHandler from "./handlers/court.js";
import courtsHandler from "./handlers/courts.js";
import clubHandler from "./handlers/club.js";
import clubsHandler from "./handlers/clubs.js";
import rentalHandler from "./handlers/rental.js";
import rentalsHandler from "./handlers/rentals.js";
import userClubsHandler from "./handlers/user_clubs.js";
import userRentalsHandler from "./handlers/user_rentals.js";
import clubUpdateHandler from "./handlers/club_update.js";
import courtAvailableHoursHandler from "./handlers/available_hours.js";
import rentalUpdateHandler from "./handlers/rental_update.js";
import home from "./handlers/home.js";
import userLoginHandler from "./handlers/user_login.js";
import userRegisterHandler from "./handlers/user_register.js";
import userLogoutHandler from "./handlers/user_logout.js";

// For more information on ES6 modules, see https://www.javascripttutorial.net/es6/es6-modules/ or
// https://www.w3schools.com/js/js_modules.asp

//window.addEventListener("load", loadHandler);
window.addEventListener("load", loadHandler);
window.addEventListener("hashchange", hashChangeHandler);

function loadHandler() {
  console.log("New load handler called");
  router.addRouteHandler(homeHandler.path, homeHandler.handler);
  router.addRouteHandler(userLoginHandler.path, userLoginHandler.handler);
  router.addRouteHandler(userRegisterHandler.path, userRegisterHandler.handler);
  router.addRouteHandler(userLogoutHandler.path, userLogoutHandler.handler);
  router.addRouteHandler(userHandler.path, userHandler.handler);
  router.addRouteHandler(usersHandler.path, usersHandler.handler);
  router.addRouteHandler(clubHandler.path, clubHandler.handler);
  router.addRouteHandler(clubsHandler.path, clubsHandler.handler);
  router.addRouteHandler(courtHandler.path, courtHandler.handler);
  router.addRouteHandler(courtsHandler.path, courtsHandler.handler);
  router.addRouteHandler(rentalHandler.path, rentalHandler.handler);
  router.addRouteHandler(rentalsHandler.path, rentalsHandler.handler);
  router.addRouteHandler(userClubsHandler.path, userClubsHandler.handler);
  router.addRouteHandler(clubUpdateHandler.path, clubUpdateHandler.handler);
  router.addRouteHandler(userRentalsHandler.path, userRentalsHandler.handler);
  router.addRouteHandler(rentalUpdateHandler.path, rentalUpdateHandler.handler);
  router.addRouteHandler(
    courtAvailableHoursHandler.path,
    courtAvailableHoursHandler.handler,
  );

  router.addDefaultNotFoundRouteHandler((content) => {
    window.location.hash = "";
    home.handler(content);
  });

  hashChangeHandler();
}

function hashChangeHandler() {
  if (getToken() != null) {
    // Logged in
    document.getElementById("loginLink").style.display = "none";
    document.getElementById("logoutLink").style.display = "block";
    document.getElementById("registerLink").style.display = "none";
  } else {
    document.getElementById("loginLink").style.display = "block";
    document.getElementById("logoutLink").style.display = "none";
    document.getElementById("registerLink").style.display = "block";
  }

  const hash = window.location.hash;
  updatePageState(hash);

  const mainContent = document.getElementById("mainContent");
  const path = hash.replace("#", "");

  const routeInfo = router.getRouteHandler(path);
  if (routeInfo && routeInfo.handler) {
    console.log(routeInfo);
    routeInfo.handler(mainContent, routeInfo.params, routeInfo.queries);
  } else {
    console.error("No handler found for path:", path);
    window.location.hash = "";
  }
}
