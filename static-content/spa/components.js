import {
  a,
  div,
  ul,
  li,
  tr,
  td,
  th,
  table,
  thead,
  tbody,
  p,
} from "./elementDsl.js";
import { pageStateRef, reloadHash } from "./helpers.js";

export function HomeLink() {
  a({ href: "#home", class: "link-secondary" }, "Home");
}

export function Table(headers, elements, content) {
  table({ class: "table table-borderless table-hover table-sm" }, () => {
    thead({ class: "text-muted" }, () => {
      tr({}, () => {
        headers.forEach((header) => th({ class: "fw-bold" }, header)); // Bold headers
      });
    });
    tbody({}, () => {
      elements.forEach((element) => {
        tr({}, () => {
          content(element);
        });
      });
    });
  });
}

const state = pageStateRef();

export function PaginationControls(hasNext, hasPrevious) {
  div(
    { class: "d-flex justify-content-center align-items-center mt-4" }, // Centered and increased top margin
    () => {
      // Prev Button with custom style
      a(
        {
          href: window.location.href,
          class: "btn btn-md btn-outline-secondary rounded-3 me-3",
          style: "transition: background-color 0.3s ease, color 0.3s ease;", // Smooth hover transition
          onclick: (e) => {
            if (!hasPrevious || state.getValue() <= 0) {
              e.preventDefault();
              return;
            }

            state.setValue(state.getValue() - 1);
            reloadHash();
          },
        },
        "Prev",
      );
      // Page Number with modern text style
      p(
        {
          class: "mb-0 px-3 py-2 bg-light rounded-3 text-dark",
          style: "font-size: 1rem;",
        }, // Background and padding for page number
        "Page " + (state.getValue() + 1),
      );
      // Next Button with custom style
      a(
        {
          href: window.location.href,
          class: "btn btn-md btn-outline-secondary rounded-3 ms-3",
          style: "transition: background-color 0.3s ease, color 0.3s ease;", // Smooth hover transition
          onclick: (e) => {
            if (!hasNext) {
              e.preventDefault();
              return;
            }

            state.setValue(state.getValue() + 1);
            reloadHash();
          },
        },
        "Next",
      );
    },
  );
}

export function Breadcrumb() {
  div({ class: "d-flex align-items-center flex-wrap mb-3" }, () => {
    const MAX_VISIBLE = 2;

    const hash = window.location.hash.slice(1);
    const [path] = hash.split("?");
    const parts = path.split("/").filter((part) => part.length > 0);

    breadcrumbLink("#", "Home");

    if (parts.length > 0) {
      breadcrumbSeparator();
    }

    const shouldCollapse = parts.length > MAX_VISIBLE;
    const startIndex = shouldCollapse ? parts.length - MAX_VISIBLE : 0;

    if (shouldCollapse) {
      breadcrumbDots();
      breadcrumbSeparator();
    }

    parts.forEach((part, index) => {
      if (index < startIndex) {
        return;
      }

      const accumulatedPath = parts.slice(0, index + 1).join("/");

      breadcrumbLink("#" + accumulatedPath, formatPart(part));

      if (index < parts.length - 1) {
        breadcrumbSeparator();
      }
    });
  });
}

function breadcrumbLink(href, label) {
  a(
    {
      href,
      class: "breadcrumb-link text-decoration-none me-2 fs-5 fw-normal", // Apply the new class
    },
    label,
  );
}

function breadcrumbSeparator() {
  p(
    {
      class: "mb-0 text-muted me-2 fs-5",
    },
    ">",
  );
}

function breadcrumbDots() {
  p(
    {
      class: "mb-0 text-muted me-2 fs-5",
    },
    "..",
  );
}

function formatPart(part) {
  return part.charAt(0).toUpperCase() + part.slice(1).replace(/-/g, " ");
}
