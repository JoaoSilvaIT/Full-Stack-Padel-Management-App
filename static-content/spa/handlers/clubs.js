import { HomeLink } from "../components.js";
import { div, p, } from "../elementDsl.js";
import { getClubs } from "../data/clubs.js";
import { renderClubsView } from "../views/clubs.js";

export default {
  path: "clubs",
  handler: (content, _params, queries) => {
    getClubs(queries.name)
      .then((paginatedClubs) => {
        const element = renderClubsView(paginatedClubs, queries.name)
        content.replaceChildren(element);
      })
      .catch((err) => {
        content.replaceChildren(
          div({}, () => {
            HomeLink();
            p({}, `Error loading clubs: ${err.message}`);
          }),
        );
      });
  },
};
