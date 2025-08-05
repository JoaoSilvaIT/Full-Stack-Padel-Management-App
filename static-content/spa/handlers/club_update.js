import { getClubById } from "../data/clubs.js";
import { renderClubUpdateView } from "../views/clubs.js";

export default {
  path: "clubs/:id/update",
  handler: (content, params) => {
    console.log("Club route handler called");

    getClubById(params.id).then((club) => {
      const element = renderClubUpdateView(club)
      content.replaceChildren(element);
    })
  },
};
