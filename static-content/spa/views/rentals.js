import {
  div,
  ul,
  li,
  a,
  td,
  form,
  input,
  label,
  button,
} from "../elementDsl.js";
import { Breadcrumb, Table } from "../components.js";
import { deleteRental, updateRental } from "../data/rentals.js";
import { formatHoursToTime, reloadHash } from "../helpers.js";

export function renderRentalView(rental) {
  return div({}, () => {
    Breadcrumb();
    ul({}, () => {
      li({}, "ID: " + rental.rid);
      li({}, () => {
        a(
          { href: `#users/${rental.renter.uid}` },
          "Rental Creator: " + rental.renter.name,
        );
      });
      li({}, () => {
        a(
          {
            href: `#clubs/${rental.court.cid}/courts/${rental.court.crid}`,
          },
          "Court: " + rental.court.name,
        );
      });
      li({}, "Date: " + rental.date);
      li({}, "Start Hour: " + formatHoursToTime(rental.starthour));
      li(
        {},
        "End Hour: " + formatHoursToTime(rental.starthour + rental.duration),
      );
    });
  });
}

export function renderRentalsView(
  cid,
  crid,
  currentDate,
  paginatedCourtRentals,
) {
  return div({}, () => {
    Breadcrumb();

    form(
      {},
      () => {
        const date = document.querySelector("#dateId");
        window.location.hash = `clubs/${cid}/courts/${crid}/rentals?date=${date.value}`;
      },
      () => {
        input({
          type: "date",
          id: "dateId",
          value: currentDate ? currentDate : null,
        });
        input({ type: "submit", id: "submitId", value: "Search" });
      },
    );

    Table(
      [
        "Id",
        "User",
        "Club",
        "Court",
        "Date",
        "Start Hour",
        "End Hour",
        "Actions",
      ],
      paginatedCourtRentals.list,
      (courtRental) => {
        td({}, () => {
          a(
            {
              href: `#clubs/${courtRental.club}/courts/${courtRental.court}/rentals/${courtRental.rid}`,
            },
            courtRental.rid,
          );
        });
        td({}, () => {
          a({ href: `#users/${courtRental.uid}` }, "User:" + courtRental.uid);
        });
        td({}, () => {
          a({ href: `#clubs/${courtRental.club}` }, "Club:" + courtRental.club);
        });
        td({}, () => {
          a(
            {
              href: `#clubs/${courtRental.club}/courts/${courtRental.court}`,
            },
            "Court:" + courtRental.court,
          );
        });
        td({}, courtRental.date);
        td({}, formatHoursToTime(courtRental.starthour));
        td({}, formatHoursToTime(courtRental.starthour + courtRental.duration));
        td({}, () => {
          button(
            {
              type: "button",
              onclick: () =>
                deleteRental(
                  courtRental.club,
                  courtRental.court,
                  courtRental.rid,
                ).then(reloadHash),
            },
            "Delete",
          );
        });
        td({}, () => {
          button(
            {
              type: "button",
              onclick: () =>
                (window.location.hash = `clubs/${courtRental.club}/courts/${courtRental.court}/rentals/${courtRental.rid}/update`),
            },
            "Update",
          );
        });
      },
    );
  });
}

export function renderRentalUpdateView(cid, crid, rental) {
  return div({}, () => {
    Breadcrumb();
    form(
      {},
      () => {
        const date = document.querySelector("#dateId");
        const starthour = document.querySelector("#starthourId");
        const duration = document.querySelector("#durationId");
        console.log(rental);
        updateRental(
          cid,
          crid,
          rental.rid,
          date.value,
          starthour.value.split(":")[0],
          duration.value,
        ).then(() => {
          window.location.hash = `clubs/${rental.club}/courts/${rental.court}/rentals/${rental.rid}`;
        });
      },
      () => {
        label({}, "Date");
        input({
          type: "date",
          id: "dateId",
          value: rental.date,
        });
        label({}, "Start hour");
        input({
          type: "number",
          id: "starthourId",
          name: "Start hour",
          min: "0",
          max: "23",
          step: "1",
          value: rental.starthour,
        });
        label({}, "Duration");
        input({
          type: "number",
          id: "durationId",
          value: rental.duration,
        });
        input({
          type: "submit",
          id: "submitId",
          value: "Update",
        });
      },
    );
  });
}
