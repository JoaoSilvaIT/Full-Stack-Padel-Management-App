import { getRentalById } from "../data/rentals.js";
import { renderRentalUpdateView } from "../views/rentals.js";

export default {
  path: "clubs/:cid/courts/:crid/rentals/:rid/update",
  handler: (content, params) => {
    console.log("Rental update route handler called");

    getRentalById(params.rid, params.cid, params.crid).then((rental) => {
      const element = renderRentalUpdateView(params.cid, params.crid, rental);
      content.replaceChildren(element);
    });
  },
};
