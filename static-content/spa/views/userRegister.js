import { div, form, input, label } from "../elementDsl.js";
import { Breadcrumb } from "../components.js";
import { registerUser } from "../data/users.js";

export function renderRegisterView() {
  return div({}, () => {
    Breadcrumb();
    form(
      {},
      () => {
        const inputName = document.querySelector("#nameId");
        const inputEmail = document.querySelector("#emailId");
        const inputPassword = document.querySelector("#passwordId");
        registerUser(inputName, inputEmail, inputPassword).then(() => {
          window.location.hash = "home";
        });
      },
      () => {
        label({}, "Name");
        input({
          type: "Name",
          id: "nameId",
        });
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
          value: "Register",
        });
      },
    );
  });
}
