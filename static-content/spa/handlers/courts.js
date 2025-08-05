import { div, p } from "../elementDsl.js";
import { HomeLink } from "../components.js";
import { getCourts } from "../data/courts.js"
import { renderCourtsView } from "../views/courts.js";

export default {
  path: "clubs/:cid/courts",
  handler: (content, params) => {
    console.log("Handling courts view");

    getCourts(params.cid)
      .then((paginatedCourts) => {
        const element = renderCourtsView(paginatedCourts,params.cid);
        content.replaceChildren(element);
      })
      .catch((err) => {
        console.error("Error fetching courts", err);
        content.replaceChildren(
          div({}, () => {
            HomeLink();
            p({}, `Error loading clubs: ${err.message}`);
          }),
        );
      });
  },
};
