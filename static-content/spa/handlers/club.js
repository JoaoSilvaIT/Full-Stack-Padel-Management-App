import { getClubById } from "../data/clubs.js";
import { renderClubView } from "../views/clubs.js";

export default {
  path: "clubs/:id",
  handler: (content, params) => {
    console.log("Club route handler called");

    getClubById(params.id)
      .then((club) => {
        const element = renderClubView(club)
        content.replaceChildren(element);
      })
      .catch((error) => {
        console.error(error);
      });
  },
};
