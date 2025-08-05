import {a, div} from "../elementDsl.js"
import { Breadcrumb } from "../components.js"

export function renderHomeView() {
  return div({}, () => {
    Breadcrumb();
    a({ href: "#clubs", class: "link" }, "Clubs");
  });
}
