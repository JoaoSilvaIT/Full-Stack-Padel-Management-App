import { getUserById } from "../data/users.js"
import { renderUserView } from "../views/users.js";

export default {
  path: "users/:id",
  handler: (content, params) => {

    getUserById(params.id)
      .then((user) => {
        const element = renderUserView(user)
        content.replaceChildren(element)
      })
      .catch((error) => {
        console.error(error);
      });
  }
};
