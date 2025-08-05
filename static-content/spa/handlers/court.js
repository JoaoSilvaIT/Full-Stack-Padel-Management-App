import { getCourtById } from "../data/courts.js";
import { renderCourtView } from "../views/courts.js";

export default {
  path: "clubs/:cid/courts/:crid",
  handler: (content, params) => {
    console.log("Court route handler called");

    getCourtById(params.cid, params.crid)
      .then((court) => {
        const element = renderCourtView(court)
        content.replaceChildren(element);
      })
      .catch((error) => {
          console.error(error);
      });
  },
};
