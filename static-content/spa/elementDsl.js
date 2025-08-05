const parentStack = [];

function createElement(tagName, attributes = {}, scope) {
  const element = document.createElement(tagName);

  for (const [key, value] of Object.entries(attributes)) {
    if (key === "class") {
      if (Array.isArray(value)) {
        element.classList.add(...value.filter(Boolean));
      } else if (typeof value === "string" && value.trim() !== "") {
        element.classList.add(...value.split(" ").filter(Boolean));
      }
    } else if (key === "textContent") {
      element.textContent = value;
    } else if (key.startsWith("on") && typeof value === "function") {
      // Use addEventListener instead of direct assignment
      const eventName = key.substring(2).toLowerCase(); // Remove 'on' prefix and convert to lowercase
      element.addEventListener(eventName, value);
    } else if (typeof value === "boolean") {
      if (value) {
        element.setAttribute(key, "");
      }
    } else if (value !== null && value !== undefined) {
      element.setAttribute(key, value);
    }
  }

  if (typeof scope === "function") {
    parentStack.push(element);
    scope(); // Execute the builder lambda to append children
    parentStack.pop();
  } else if (typeof scope !== "undefined" && scope !== null) {
    element.textContent = scope;
  }

  if (parentStack.length > 0) {
    const parent = parentStack[parentStack.length - 1];
    parent.appendChild(element);
  }

  return element;
}

export function div(attributes, scope) {
  return createElement("div", attributes, scope);
}
export function h1(attributes, scope) {
  return createElement("h1", attributes, scope);
}
export function h2(attributes, scope) {
  return createElement("h2", attributes, scope);
}
export function p(attributes, scope) {
  return createElement("p", attributes, scope);
}
export function ul(attributes, scope) {
  return createElement("ul", attributes, scope);
}
export function li(attributes, scope) {
  return createElement("li", attributes, scope);
}
export function a(attributes, scope) {
  return createElement("a", attributes, scope);
}
export function br() {
  return createElement("br");
}
export function button(attributes, scope) {
  return createElement("button", attributes, scope);
}

export function table(attributes, scope) {
  return createElement("table", attributes, scope);
}

export function tbody(attributes, scope) {
  return createElement("tbody", attributes, scope);
}

export function thead(attributes, scope) {
  return createElement("thead", attributes, scope);
}

export function tfoot(attributes, scope) {
  return createElement("tfoot", attributes, scope);
}

export function tr(attributes, scope) {
  return createElement("tr", attributes, scope);
}

export function td(attributes, scope) {
  return createElement("td", attributes, scope);
}

export function th(attributes, scope) {
  return createElement("th", attributes, scope);
}

export function form(attributes, onSubmit, scope) {
  const form = createElement("form", attributes, scope);
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    onSubmit();
  });
}

export function input(attributes, scope) {
  return createElement("input", attributes, scope);
}

export function label(attributes, scope) {
  return createElement("label", attributes, scope);
}

export function text(content) {
  return document.createTextNode(String(content));
}
