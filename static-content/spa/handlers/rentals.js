import { HomeLink } from "../components.js";
import { getRentals } from "../data/rentals.js";
import { div, p } from "../elementDsl.js";
import { renderRentalsView } from "../views/rentals.js";

export default {
  path: "clubs/:cid/courts/:crid/rentals",
  handler: (content, params, queries) => {
    console.log("Handling rentals view");

    getRentals(params.cid, params.crid, queries.date)
      .then((paginatedCourtRentals) => {
        const element = renderRentalsView(params.cid, params.crid, queries.date, paginatedCourtRentals)
        content.replaceChildren(element);
      })
      .catch((err) => {
        console.error("Error fetching rentals", err);
        content.replaceChildren(
          div({}, () => {
            HomeLink();
            p({}, `Error loading clubs: ${err.message}`);
          }),
        );
      });
  },
};
