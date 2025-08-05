import { getRentalById } from "../data/rentals.js";
import { renderRentalView } from "../views/rentals.js";

export default {
  path: "clubs/:cid/courts/:crid/rentals/:rid",
  handler: (content, params) => {
    getRentalById(params.rid, params.cid, params.crid)
      .then((rental) => {
        const element = renderRentalView(rental);
        content.replaceChildren(element);
      })
      .catch((error) => {
        console.error(error);
      });
  },
};
