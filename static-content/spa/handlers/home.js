import { renderHomeView } from "../views/home.js";

export default {
  path: "",
  handler: (content) => {
    console.log("Handling home view");
    const element = renderHomeView()
    content.replaceChildren(element);
  },
};
