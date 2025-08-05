import { div, form, input, label } from "../elementDsl.js";
import { Breadcrumb } from "../components.js";
import { loginUser } from "../data/users.js";

export function renderLoginView() {
  return div({}, () => {
    Breadcrumb();
    form(
      {},
      () => {
        const inputEmail = document.querySelector("#emailId");
        const inputPassword = document.querySelector("#passwordId");
        loginUser(inputEmail, inputPassword).then(() => {
          window.location.hash = "home";
        });
      },
      () => {
        label({}, "Email");
        input({
          type: "Email",
          id: "emailId",
        });
        label({}, "Password");
        input({
          type: "Password",
          id: "passwordId",
        });
        input({
          type: "submit",
          id: "submitId",
          value: "Log In",
        });
      },
    );
  });
}
