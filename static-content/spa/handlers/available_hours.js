import { getCourtAvailableHours } from "../data/courts.js";
import { renderAvailableHoursView } from "../views/courts.js";

export default {
  path: "clubs/:cid/courts/:crid/availableHours",
  handler: (content, params, queries) => {
    console.log("Court route handler called");

    getCourtAvailableHours(params.cid, params.crid, queries.date)
      .then((availableHours) => {
        const element = renderAvailableHoursView(
          params.cid,
          params.crid,
          queries.date,
          availableHours,
        );
        content.replaceChildren(element);
      })
      .catch((error) => {
          console.error(error);
      });
  },
};
