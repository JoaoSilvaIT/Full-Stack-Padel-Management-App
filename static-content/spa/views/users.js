import { div, ul, li, a, h2, td } from "../elementDsl.js";
import { Breadcrumb, Table, PaginationControls } from "../components.js";
import { formatHoursToTime } from "../helpers.js";

export function renderUserView(user) {
  return div({}, () => {
    Breadcrumb();
    ul({}, () => {
      li({}, "Name: " + user.name);
      li({}, "ID: " + user.uid);
      li({}, () => {
        a({ href: `#users/${user.uid}/rentals` }, "Rentals");
      });
    });
    div({}, () => {
      h2({}, "Owned Clubs");
      Table(
        ["Name", "Id"], user.clubs, (club) => {
        td({}, () => {
          a({ href: `#clubs/${club.cid}` }, club.name);
        });
        td({}, club.cid);
      });
    });
  });
}

export function renderUsersView(paginatedUsers) {
  return div({}, () => {
    Breadcrumb();
    Table(["Name", "Id", "Email"], paginatedUsers.list, (user) => {
      console.log(user);
      td({}, () => {
        a({ href: `#users/${user.uid}` }, user.name);
      });
      td({}, user.uid);
      td({}, user.email.value);
    });
    PaginationControls(paginatedUsers.hasNext, paginatedUsers.hasPrevious);
  });
}

export function renderUserRentalsView(paginatedUserRentals) {
  return div({}, () => {
    Breadcrumb();
    Table(
      ["Id", "User", "Club", "Court", "Date", "Start Hour", "End Hour"],
      paginatedUserRentals.list,
      (userRental) => {
        td({}, () => {
          a({ href: `#rentals/${userRental.rid}` }, userRental.rid);
        });
        td({}, () => {
          a(
            { href: `#users/${userRental.uid}` },
            "User:" + userRental.uid,
          );
        });
        td({}, () => {
          a(
            { href: `#clubs/${userRental.club}` },
            "Club:" + userRental.club,
          );
        });
        td({}, () => {
          a(
            {
              href: `#clubs/${userRental.club}/courts/${userRental.court}`,
            },
            "Court:" + userRental.court,
          );
        });
        td({}, userRental.date);
        td({}, formatHoursToTime(userRental.starthour));
        td(
          {},
          formatHoursToTime(userRental.starthour + userRental.duration),
        );
      },
    );

    PaginationControls(
      paginatedUserRentals.hasNext,
      paginatedUserRentals.hasPrevious,
    );
  });
}

export function renderUserClubsView(paginatedClubs) {
  return div({}, () => {
    Breadcrumb();
    Table(["name", "id"], paginatedClubs.list, (club) => {
      console.log(club);
      td({}, () => {
        a({ href: `#clubs/${club.cid}` }, club.name);
      });
      td({}, club.cid);
    });
    PaginationControls(
      paginatedClubs.hasNext,
      paginatedClubs.hasPrevious,
    );
  });
}
