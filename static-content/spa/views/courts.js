import { div, ul, li, a, td, form, input, label } from "../elementDsl.js";
import { Breadcrumb, Table, PaginationControls } from "../components.js";
import { createCourt } from "../data/courts.js";
import { createRental } from "../data/rentals.js";
import { reloadHash } from "../helpers.js";

export function renderCourtView(court) {
  return div({}, () => {
    Breadcrumb();
    ul({}, () => {
      li({}, "Name: " + court.name);
      li({}, "ID: " + court.crid);
      li({}, () => {
        a(
          { href: `#clubs/${court.club.cid}` },
          "Club: " + court.club.name + " (" + court.club.cid + ")",
        );
      });
      li({}, () => {
        a(
          {
            href: `#clubs/${court.club.cid}/courts/${court.crid}/rentals`,
          },
          "Rentals: " + court.rentals.length,
        );
      });
      li({}, () => {
        form(
          {},
          () => {
            const date = document.querySelector("#dateId");
            window.location.hash = `#clubs/${court.club.cid}/courts/${court.crid}/availableHours?date=${date.value}`;
          },
          () => {
            input({
              type: "date",
              id: "dateId",
            });
            input({
              type: "submit",
              id: "submitId",
              value: "Available Hours",
            });
          },
        );
      });
    });
  });
}

export function renderCourtsView(paginatedCourts, cid) {
  return div({}, () => {
    Breadcrumb();
    form(
      {},
      () => {
        const inputName = document.querySelector("#nameId");
        createCourt(inputName, cid);
      },
      () => {
        label({}, "Name");
        input({
          type: "text",
          id: "nameId",
        });
        input({ type: "submit", id: "submitId", value: "Create" });
      },
    );
    Table(["id", "name", "club"], paginatedCourts.list, (court) => {
      td({}, () => {
        a({ href: `#clubs/${court.club}/courts/${court.crid}` }, court.crid);
      });
      td({}, court.name);
      td({}, () => {
        a({ href: `#clubs/${court.club}` }, court.club);
      });
    });

    PaginationControls(paginatedCourts.hasNext, paginatedCourts.hasPrevious);
  });
}

export function renderAvailableHoursView(cid, crid, date, availableHours) {
  return div({}, () => {
    Breadcrumb();
    form(
      {},
      () => {
        const startHour = document.querySelector("#startHourId");
        const duration = document.querySelector("#durationId");
        createRental(cid, crid, date, startHour.value, duration.value).then(
          () => {
            reloadHash();
          },
        );
      },
      () => {
        label({}, "Start Hour:");
        input({
          type: "integer",
          id: "startHourId",
        });
        label({}, "Duration:");
        input({
          type: "integer",
          id: "durationId",
        });
        input({
          type: "submit",
          id: "submitId",
          value: "Create",
        });
      },
    );
    form(
      {},
      () => {
        const date = document.querySelector("#dateId");
        window.location.hash = `#clubs/${cid}/courts/${crid}/availableHours?date=${date.value}`;
      },
      () => {
        input({
          type: "date",
          id: "dateId",
        });
        input({
          type: "submit",
          id: "submitId",
          value: "Update",
        });
      },
    );

    ul(
      {
        class:
          "list-unstyled row row-cols-3 row-cols-sm-4 row-cols-md-6 g-3 mt-4",
      },
      () => {
        // Generate all hours from 00:00 to 23:00
        const allHours = Array.from({ length: 24 }, (_, i) => i);

        allHours.forEach((hour) => {
          const isAvailable = availableHours.includes(hour);
          let hourClasses = "border rounded text-center py-2 ";

          if (isAvailable) {
            hourClasses += "bg-lightbg-success"; // Avai\lable hours standard look
          } else {
            // Unavailable hours: red background and white text for contrast
            hourClasses += "bg-danger text-white";
          }

          li(
            {
              class: hourClasses.trim(),
            },
            `${hour.toString().padStart(2, "0")}:00`,
          );
        });
      },
    );
  });
}
