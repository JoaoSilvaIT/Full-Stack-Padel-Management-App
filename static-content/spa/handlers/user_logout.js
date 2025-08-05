import { logoutUser } from "../data/users.js";

export default {
  path: "users/logout",
  handler: () => {
    logoutUser();
    window.location.hash = "home";
  },
};
