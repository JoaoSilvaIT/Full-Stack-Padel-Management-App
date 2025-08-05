import {API_BASE_URL, getToken, pageStateRef} from "../helpers.js";

let page = pageStateRef();

const limit = 5;

export function getCourtById(cid, crid) {
  return fetch(`${API_BASE_URL}/clubs/${cid}/courts/${crid}`).then(
    (response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    },
  );
}

export function getCourts(cid) {
  const skip = page.getValue() * limit;
  return fetch(
    API_BASE_URL +
      "/clubs/" +
      cid +
      "/courts" +
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

export function getCourtAvailableHours(cid, crid, date) {
  return fetch(
    API_BASE_URL +
    "/clubs/" +
    cid +
    "/courts/" +
    crid +
    "/available_hours" +
    "?date=" +
    date
  ).then((response) => {
    if (!response.ok) {
      throw new Error("Http error! Status: " + response.status)
    }
    return response.json();
  });
}

export function createCourt(name,cid){
    const url = `${API_BASE_URL}/clubs/${cid}/courts/`
    const options = {
        method : "POST",
        headers : {
            "Content-Type" : "application/json",
            "Accept" : "application/json",
            "Authorization": getToken()
        },
        body : JSON.stringify({
            name : name.value,
            club: cid,
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
    window.location.hash= `clubs/${cid}/courts/`;
}
