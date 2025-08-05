import { API_BASE_URL, getToken, pageStateRef } from "../helpers.js";

let page = pageStateRef();

const limit = 5;

export function getRentalById(rid, cid, crid) {
  return fetch(
    `${API_BASE_URL}/clubs/${cid}/courts/${crid}/rentals/${rid}`,
  ).then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  });
}
export function createRental(cid, crid, newDate, startHour, duration) {
  const url =`${API_BASE_URL}/clubs/${cid}/courts/${crid}/rentals`

  const options = {
    method : "POST",
    headers : {
      "Content-Type" : "application/json",
      "Accept" : "application/json",
      "Authorization": getToken()
    },
    body : JSON.stringify({
      "club" : cid,
      "court": crid,
      "date": newDate,
      "startHour": startHour,
      "duration": duration
    })
  }
  return fetch(url, options).then((response) => {
    if (!response.ok) {
      return response.text().then(errorText => {
        alert("Status: " + response.status + "\n" + errorText);
        throw new Error("Http error! Status: " + response.status);
      })
    }
    return response.json();
  })
}
export function getRentals(cid, crid, date) {
  const skip = page.getValue() * limit;

  const dateQuery = date ? "&date=" + date : "";

  return fetch(
    API_BASE_URL +
      "/clubs/" +
      cid +
      "/courts/" +
      crid +
      "/rentals" +
      "?skip=" +
      skip +
      "&limit=" +
      limit +
      dateQuery,
  ).then((response) => {
    if (!response.ok) {
      throw new Error("Http error! Status: " + response.status);
    }
    return response.json();
  });
}

export function updateRental(cid, crid, rid, date, startHour, duration) {
  return fetch(`${API_BASE_URL}/clubs/${cid}/courts/${crid}/rentals/${rid}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: getToken(),
    },
    body: JSON.stringify({
      date,
      startHour,
      duration,
    }),
  }).then((response) => {
    if (!response.ok) {
      return response.text().then(errorText => {
        alert("Status: " + response.status + "\n" + errorText);
        throw new Error("Http error! Status: " + response.status);
      })
    }
    return response.json();
  });
}

export function deleteRental(cid, crid, rid) {
  return fetch(`${API_BASE_URL}/clubs/${cid}/courts/${crid}/rentals/${rid}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Authorization: getToken(),
    },
  }).then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  });
}
