import { div, p } from "../elementDsl.js";
import { getUserRentals } from "../data/users.js"
import { renderUserRentalsView } from "../views/users.js";

export default {
  path: "users/:uid/rentals",
  handler: (content, params) => {
    getUserRentals(params.uid)
      .then((paginatedUserRentals) => {
        console.log(paginatedUserRentals)
        const element = renderUserRentalsView(paginatedUserRentals)
        content.replaceChildren(element)
      })
      .catch((err) => {
        console.error("Error fetching user rentals", err);
        content.replaceChildren(
          div({}, () => {
            HomeLink();
            p({}, `Error loading users: ${err.message}`);
          }),
        );
      });
  },
};
