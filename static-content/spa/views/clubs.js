import {div, td, ul, li, a, form, input, label} from "../elementDsl.js";
import { Breadcrumb, Table, PaginationControls } from "../components.js";
import {createClub, updateClub} from "../data/clubs.js";


export function renderClubView(club) {
  return div({}, () => {
    Breadcrumb();
    ul({}, () => {
      li({}, "Name: " + club.name);
      li({}, "ID: " + club.cid);
      li({}, () => {
        a(
          { href: `#users/${club.owner.uid}` },
          "Owner: " + club.owner.name,
        );
      });
      li({}, () => {
        a(
          { href: `#clubs/${club.cid}/courts` },
          "Courts: " + club.courts.length,
        );
      });
    });
  });
}

export function renderClubsView(paginatedClubs, search) {
  return div({}, () => {
    Breadcrumb();
    form({}, ()=>{
      const inputName = document.querySelector("#nameId")
      createClub(inputName)
    },()=>{
      label({},"Name")
      input({
        type : "text",
        id: "nameId"
      })
      input({ type: "submit", id: "submitId", value: "Create" })
    })
    form(
      {},
      () => {
        const searchInput = document.querySelector("#searchId");
        window.location.hash = "clubs?name=" + searchInput.value;
      },
      () => {
        input({
          type: "text",
          id: "searchId",
          value: search ? search : "",
        });
        input({ type: "submit", id: "submitId", value: "Search" });
      },
    );

    Table(["Name", "Id", ""], paginatedClubs.list, (club) => {
      td({}, () => {
        a({ href: `#clubs/${club.cid}` }, club.name);
      });
      td({}, club.cid);
      td({}, () => {
        a({
          href: `#clubs/${club.cid}/update`,
          class: "btn btn-md btn-outline-secondary rounded-3"
        }, "Update")
      })
    });

    PaginationControls(
      paginatedClubs.hasNext,
      paginatedClubs.hasPrevious,
    );
  });
}

export function renderClubUpdateView(club) {
  return div({}, () => {
    Breadcrumb()
    form({},() => {
      const name = document.querySelector("#nameId")
      updateClub(club.cid, name.value).then(() => {
        window.location.hash = `clubs/${club.cid}`
      })
    }, () => {
      label({}, "Name")
      input({
        type: "text",
        id: "nameId",
        value: club.name
      })
      input({
        type: "submit",
        id: "submitId",
        value: "Update"
      })
    })
  })
}
