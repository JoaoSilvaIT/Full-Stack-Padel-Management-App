import router from "../spa/router.js";
import getHome from "../spa/handlers/home.js";
import getClubs from "../spa/handlers/clubs.js";
import getClub from "../spa/handlers/club.js";
import getUsers from "../spa/handlers/users.js";
import getUser from "../spa/handlers/user.js";
import getUserRentals from "../spa/handlers/user_rentals.js";
import getUserClubs from "../spa/handlers/user_clubs.js";
import getRental from "../spa/handlers/rental.js";
import getCourts from "../spa/handlers/courts.js";
import getCourt from "../spa/handlers/court.js";

const handlers = {
    getHome,
    getClubs,
    getClub,
    getUsers,
    getUser,
    getUserRentals,
    getUserClubs,
    getRental,
    getCourts,
    getCourt
};

describe('router', function () {

    it('should find getHome', function () {
        router.addRouteHandler("", handlers.getHome);
        const routeInfo = router.getRouteHandler("");
        chai.expect(routeInfo.handler).to.equal(handlers.getHome);
    });

    it('should find getClubs', function () {
        router.addRouteHandler("clubs", handlers.getClubs);
        const routeInfo = router.getRouteHandler("clubs");
        routeInfo.handler.should.equal(handlers.getClubs);
    });

    it('should find getClubs with query', function () {
        router.addRouteHandler("clubs", handlers.getClubs);
        const routeInfo = router.getRouteHandler("clubs?name=PadelLisboa");
        routeInfo.handler.should.equal(handlers.getClubs);
        routeInfo.queries.should.deep.equal({ name: 'PadelLisboa' });
    });

    it('should find getClub and extract club ID', function () {
        router.addRouteHandler("clubs/:id", handlers.getClub);
        const routeInfo = router.getRouteHandler("clubs/42");
        routeInfo.handler.should.equal(handlers.getClub);
        routeInfo.params.should.deep.equal({ id: '42' });
    });

    it('should find getUsers', function () {
        router.addRouteHandler("users", handlers.getUsers);
        const routeInfo = router.getRouteHandler("users");
        routeInfo.handler.should.equal(handlers.getUsers);
    });

    it('should find getUser and extract user ID', function () {
        router.addRouteHandler("users/:id", handlers.getUser);
        const routeInfo = router.getRouteHandler("users/101");
        routeInfo.handler.should.equal(handlers.getUser);
        routeInfo.params.should.deep.equal({ id: '101' });
    });

    it('should find getUserRentals and extract user ID', function () {
        router.addRouteHandler("users/:uid/rentals", handlers.getUserRentals);
        const routeInfo = router.getRouteHandler("users/101/rentals");
        routeInfo.handler.should.equal(handlers.getUserRentals);
        routeInfo.params.should.deep.equal({ uid: '101' });
    });

    it('should find getRental and extract rental ID', function () {
        router.addRouteHandler("rentals/:id", handlers.getRental);
        const routeInfo = router.getRouteHandler("rentals/505");
        routeInfo.handler.should.equal(handlers.getRental);
        routeInfo.params.should.deep.equal({ id: '505' });
    });

    it('should find getCourts for a club and extract club ID', function () {
        router.addRouteHandler("clubs/:cid/courts", handlers.getCourts);
        const routeInfo = router.getRouteHandler("clubs/77/courts");
        routeInfo.handler.should.equal(handlers.getCourts);
        routeInfo.params.should.deep.equal({ cid: '77' });
    });

    it('should find getCourt and extract club ID and court ID', function () {
        router.addRouteHandler("clubs/:cid/courts/:crid", handlers.getCourt);
        const routeInfo = router.getRouteHandler("clubs/77/courts/3");
        routeInfo.handler.should.equal(handlers.getCourt);
        routeInfo.params.should.deep.equal({ cid: '77', crid: '3' });
    });

    it('should return notFoundRouteHandler for undefined path', function () {
        let called = false;
        const mockCustomNotFound = function mockCustomNotFound() { called = true; };
        router.addDefaultNotFoundRouteHandler(mockCustomNotFound);

        const routeInfo = router.getRouteHandler("this/path/does/not/exist");
        routeInfo.handler.name.should.be.equal("mockCustomNotFound");
        routeInfo.handler();
        called.should.be.equal(true);
    });

    it('should decode URI components in path parameters', () => {
        const genericHandler = function genericHandler() {};
        router.addRouteHandler("search/:searchTerm", genericHandler);
        const routeInfo = router.getRouteHandler("search/Top%20Padel%20Club");
        routeInfo.handler.name.should.be.equal("genericHandler");
        routeInfo.params.should.deep.equal({ searchTerm: "Top Padel Club" });
    });
});