import { formatHoursToTime } from "../spa/helpers.js";

describe("formatHoursToTime", function() {
    it("formats whole hours with leading zero", function() {
        formatHoursToTime(9).should.equal("09:00");
    });

    it("formats fractional hours correctly", function() {
        formatHoursToTime(10.5).should.equal("10:30");
        formatHoursToTime(14.75).should.equal("14:45");
    });
});