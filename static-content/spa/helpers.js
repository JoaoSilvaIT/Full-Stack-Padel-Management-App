export const API_BASE_URL =
  window.location.hostname === "localhost"
    ? "http://localhost:8080"
    : "https://service-ls-2425-2-43-g02.onrender.com";

export function formatHoursToTime(hours) {
  const formattedHours = Math.floor(hours); // Get the number of full hours
  const minutes = Math.round((hours - formattedHours) * 60); // Get the remaining minutes

  // Format as HH:mm
  return `${formattedHours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
}

var initialValue = 0;
var hash = "";

export function updatePageState(newHash) {
  console.log("New hash:", newHash);
  console.log("Old hash:", hash);

  if (newHash !== hash) {
    initialValue = 0;
  }

  hash = newHash;
}

const globalState = {
  setValue: function (newValue) {
    initialValue = newValue;
  },
  getValue: function () {
    return initialValue;
  },
};

export function pageStateRef() {
  return globalState;
}

export function reloadHash() {
  window.dispatchEvent(new HashChangeEvent("hashchange"));
}

export function getToken() {
  const user = sessionStorage.getItem("token");
  if (user == null || user == {}) {
    return null;
  }
  console.log(user);
  console.log(JSON.parse(user));
  return `Bearer ${JSON.parse(user)}`;
}
export function storeUser(token) {
  sessionStorage.setItem("token", JSON.stringify(token));
}
