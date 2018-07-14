package com.nzt.ticketservice.util;

import com.nzt.ticketservice.data.Row;
import com.nzt.ticketservice.enums.Section;
import org.junit.Assert;
import org.junit.Test;

import java.util.SortedSet;

public class VenueUtilTest {


    private void callAsserts(SortedSet<Row> set, int size, String firstId, String lastId) {
        Assert.assertNotNull(set);

        // Verify number of seats
        Assert.assertEquals("The number of section seats is not correct", size, set.size());

        // Verify order
        String firstSeatId = set.first().getSeats().first().getSeatId();
        Assert.assertEquals("Section first Seat Id is not correct", firstId, firstSeatId);

        String lastSeatId = set.last().getSeats().first().getSeatId();
        Assert.assertEquals("Section last Seat Id is not correct", lastId, lastSeatId);
    }


    @Test
    public void verifySectionA() {
        SortedSet<Row> sectionASeats = VenueUtil.initSectionSeats(Section.A);
        callAsserts(sectionASeats, 340, "I34", "R67");
    }

    @Test
    public void verifySectionB() {
        SortedSet<Row> sectionBSeats = VenueUtil.initSectionSeats(Section.B);
        callAsserts(sectionBSeats, 272, "S34", "Z67");

    }

    @Test
    public void verifySectionC1() {
        SortedSet<Row> sectionC1Seats = VenueUtil.initSectionSeats(Section.C);
        callAsserts(sectionC1Seats, 330, "I1", "R33");
    }

    @Test
    public void verifySectionC2() {
        SortedSet<Row> sectionCSeats = VenueUtil.initSectionSeats(Section.C, true);
        callAsserts(sectionCSeats, 330, "I68", "R100");
    }

    @Test
    public void verifySectionD1() {

        SortedSet<Row> sectionDSeats = VenueUtil.initSectionSeats(Section.D);
        callAsserts(sectionDSeats, 264, "S1", "Z33");

    }

    @Test
    public void verifySectionD2() {
        SortedSet<Row> sectionDSeats = VenueUtil.initSectionSeats(Section.D, true);
        callAsserts(sectionDSeats, 264, "S68", "Z100");
    }

    @Test
    public void verifySectionE() {
        SortedSet<Row> sectionESeats = VenueUtil.initSectionSeats(Section.E);
        callAsserts(sectionESeats, 272, "A34", "H67");
    }

    @Test
    public void verifySectionF1() {
        SortedSet<Row> sectionDSeats = VenueUtil.initSectionSeats(Section.F);
        callAsserts(sectionDSeats, 264, "A1", "H33");
    }

    @Test
    public void verifySectionF2() {
        SortedSet<Row> sectionDSeats = VenueUtil.initSectionSeats(Section.F, true);
        callAsserts(sectionDSeats, 264, "A68", "H100");
    }

}