import { renderLoginView } from "../views/userLogin.js";

export default {
  path: "users/login",
  handler: (content) => {
    const element = renderLoginView();
    content.replaceChildren(element);
  },
};
