package com.nzt.ticketservice.util;


import com.nzt.ticketservice.data.Row;
import com.nzt.ticketservice.data.Seat;
import com.nzt.ticketservice.enums.Section;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public final class VenueUtil {

    private static SortedSet<Seat> venueSeats;
    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static char[] alphabetArray = ALPHABETS.toCharArray();


    // Left Hand Side
    private static final int LHS_START = 1;
    private static final int LHS_END = 33;

    // Right Hand Side
    private static final int RHS_START = 68;
    private static final int RHS_END = 100;

    // Center
    private static final int CENTRE_START = 34;
    private static final int CENTRE_END = 67;

    // Alphabet Positions
    private static final int A_INDEX = 1;
    private static final int I_INDEX = 9;
    private static final int S_INDEX = 19;
    private static final int Z_INDEX = 27;


    public static final SortedSet<Row> initSectionSeats(Section section, boolean... extended) {

        int rowStart = 0;
        int rowEnd = 0;
        int seatStart = 0;
        int seatEnd = 0;

        switch (section) {
            case A:
                rowStart = I_INDEX;
                rowEnd = S_INDEX;
                seatStart = CENTRE_START;
                seatEnd = CENTRE_END;
                break;
            case B:
                rowStart = S_INDEX;
                rowEnd = Z_INDEX;
                seatStart = CENTRE_START;
                seatEnd = CENTRE_END;
                break;
            case E:
                rowStart = A_INDEX;
                rowEnd = I_INDEX;
                seatStart = CENTRE_START;
                seatEnd = CENTRE_END;
                break;
            case C:
                rowStart = I_INDEX;
                rowEnd = S_INDEX;
                if (ArrayUtils.isNotEmpty(extended) && extended[0]) {
                    seatStart = RHS_START;
                    seatEnd = RHS_END;
                } else {
                    seatStart = LHS_START;
                    seatEnd = LHS_END;
                }
                break;
            case D:
                rowStart = S_INDEX;
                rowEnd = Z_INDEX;
                if (ArrayUtils.isNotEmpty(extended) && extended[0]) {
                    seatStart = RHS_START;
                    seatEnd = RHS_END;
                } else {
                    seatStart = LHS_START;
                    seatEnd = LHS_END;
                }
                break;
            case F:
                rowStart = A_INDEX;
                rowEnd = I_INDEX;
                if (ArrayUtils.isNotEmpty(extended) && extended[0]) {
                    seatStart = RHS_START;
                    seatEnd = RHS_END;
                } else {
                    seatStart = LHS_START;
                    seatEnd = LHS_END;
                }
                break;
            default:
                break;
        }


        return addSectionSeats(section, rowStart, rowEnd, seatStart, seatEnd);
    }


    private static SortedSet<Row> addSectionSeats(Section section, int rowStart, int rowEnd, int seatStart, int seatEnd) {
        SortedSet<Row> sectionRows = new ConcurrentSkipListSet<>(
                (r1, r2) -> new CompareToBuilder().append(r1.getRowId(), r2.getRowId()).build()
        );
        boolean reverseSortOrder = false;
        int maxNumberOfSeats = (seatEnd - seatStart) + 1;
        for (int i = rowStart - 1; i < rowEnd - 1; i++) {
            String rowId = String.valueOf(alphabetArray[i]);
            final Row row = new Row(rowId, section, maxNumberOfSeats, reverseSortOrder);

            for (int j = seatStart; j <= seatEnd; j++) {
                String seatId = new StringBuilder().append(rowId).append(String.valueOf(j)).toString();
                Seat newSeat = new Seat();
                newSeat.setSeatId(seatId);
                newSeat.setNumber(j);
                newSeat.setRow(row);

                if (!row.addSeat(newSeat)) {
                    throw new IllegalStateException("Seat was not added to Row during venue initialization");
                }
            }
            sectionRows.add(row);
            reverseSortOrder = !reverseSortOrder;
        }
        return sectionRows;
    }
}
