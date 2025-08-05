import { getClubs } from "../data/clubs.js"
import { renderUserClubsView } from "../views/users.js";

export default {
  path: "users/:uid/clubs",
  handler: (content) => {

    //ned to change getClubs to support
    getClubs(uid)
      .then((paginatedUserClubs) => {
        const element = renderUserClubsView(paginatedUserClubs)
        content.replaceChildren(element)
      })
      .catch((err) => {
        console.error("Error fetching clubs", err);
        content.replaceChildren(
          div({}, () => {
            HomeLink();
            p({}, `Error loading clubs: ${err.message}`);
          }),
        );
      });
  },
};
