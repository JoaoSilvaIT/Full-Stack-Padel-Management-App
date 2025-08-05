import { API_BASE_URL, pageStateRef, storeUser } from "../helpers.js";

let page = pageStateRef();

const limit = 5;

export function getUserById(uid) {
  return fetch(`${API_BASE_URL}/users/${uid}`).then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  });
}

export function getUsers() {
  const skip = page.getValue() * limit;

  return fetch(
    API_BASE_URL + "/users" + "?skip=" + skip + "&limit=" + limit,
  ).then((response) => {
    if (!response.ok) {
      throw new Error("Http error! Status: " + response.status);
    }
    return response.json();
  });
}

export function getUserRentals(uid) {
  const skip = page.getValue() * limit;

  return fetch(
    API_BASE_URL +
      "/users/" +
      uid +
      "/rentals" +
      "?skip=" +
      skip +
      "&limit=" +
      limit,
  ).then((response) => {
    if (!response.ok) {
      throw new Error("Http error! Status: " + response.status);
    }
    return response.json();
  });
}

export async function loginUser(email, password) {
  const URL = `${API_BASE_URL}/users/login`;
  const options = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify({
      email: email.value,
      password: password.value,
    }),
  };
  const res = await fetch(URL, options);
  if (!res.ok) {
    const text = await res.text(); // tenta ler mensagem do servidor
    alert(text || "Email ou password inválidos");
    return Promise.reject();
  }
  const user = await res.json();
  storeUser(user.token);
}

export async function registerUser(name, email, password) {
  const URL = `${API_BASE_URL}/users`;
  const options = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify({
      name: name.value,
      mail: email.value,
      password: password.value,
    }),
  };
  const res = await fetch(URL, options);

  if (!res.ok) {
    const text = await res.text(); // tenta ler mensagem do servidor
    alert(text || "Email ou password inválidos");
    return Promise.reject();
  }
  const user = await res.json();
  storeUser(user.token);
}

export function logoutUser() {
  const currUser = sessionStorage.getItem("token");
  if (currUser) {
    sessionStorage.removeItem("token");
  }
}
