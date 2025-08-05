const routes = [];
let notFoundRouteHandler = () => {
  console.error(
    "Route handler for unknown routes not defined or no route matched.",
  );
  window.location.hash = "#home";
};

function addRouteHandler(pathTemplate, handler) {
  const paramNames = [];
  const parsedTemplate = pathTemplate.replace(/:(\w+)/g, (_, name) => {
    paramNames.push(name);
    return "([^\\/]+)"; // Capture group for anything except a slash
  });

  const regex = new RegExp(`^${parsedTemplate}/?$`);
  routes.push({ regex: regex, params: paramNames, handler: handler });
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
  notFoundRouteHandler = notFoundRH;
}

function getRouteHandler(rawPath) {
  const [path, queryString] = rawPath.split("?");
  const queryParams = Object.fromEntries(
    new URLSearchParams(queryString || ""),
  );

  for (const route of routes) {
    const match = path.match(route.regex);

    if (match) {
      const pathParams = {};
      for (let i = 0; i < route.params.length; i++) {
        pathParams[route.params[i]] = decodeURIComponent(match[i + 1]);
      }
      return {
        handler: route.handler,
        params: pathParams,
        queries: queryParams,
      };
    }
  }

  return { handler: notFoundRouteHandler, params: {}, queries: {} };
}

const router = {
  addRouteHandler,
  getRouteHandler,
  addDefaultNotFoundRouteHandler,
};

export default router;
