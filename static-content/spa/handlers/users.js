import { div, p } from "../elementDsl.js";
import { getUsers } from "../data/users.js"
import { renderUsersView } from "../views/users.js";

export default {
  path: "users",
  handler: (content) => {
    getUsers()
      .then((paginatedUsers) => {
        console.log(paginatedUsers)
        const element = renderUsersView(paginatedUsers)
        content.replaceChildren(element)
      })
      .catch((err) => {
        console.error("Error fetching users", err);
        content.replaceChildren(
          div({}, () => {
            p({}, `Error loading users: ${err.message}`);
          }),
        );
      });
  },
};
