import {API_BASE_URL, getToken, pageStateRef} from "../helpers.js";

let page = pageStateRef();

const limit = 5;

export function getClubById(id) {
  return fetch(`${API_BASE_URL}/clubs/${id}`).then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  });
}

export function getClubs(name) {
  const skip = page.getValue() * limit;

  var url = API_BASE_URL + "/clubs" + `?limit=${limit}&skip=${skip}`;
  if (name) url += "&name=" + name;

  return fetch(url).then((response) => {
    if (!response.ok) {
      throw new Error("Http error! Status: " + response.status);
    }
    return response.json();
  });
}

export function updateClub(cid, name) {
  return fetch(
    API_BASE_URL +
    "/clubs/" +
    cid,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Authorization": getToken()
      },
      body: JSON.stringify({
        name
      })
    }
  ).then((response) => {
    if (!response.ok) {
      alert("Status: " + response.status + "\n"+response.text);
      throw new Error("Http error! Status: " + response.status)
    }

    console.log("Updated!")

    return response.json()
  })
}

export function createClub(name){

  const url = API_BASE_URL + "/clubs";
  const options = {
    method : "POST",
    headers : {
      "Content-Type" : "application/json",
      "Accept" : "application/json",
      "Authorization": getToken()
    },
    body : JSON.stringify({
      name : name.value,
    })
  }
  fetch(url,options).then((response) => {
    if (!response.ok) {
      return response.text().then(errorText => {
        alert("Status: " + response.status + "\n" + errorText);
        throw new Error("Http error! Status: " + response.status);
      })
    }
    return response.json();
  })
  window.location.hash= "#clubs";
}
