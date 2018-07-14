package com.nzt.ticketservice.data;

import com.nzt.ticketservice.enums.Section;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;


@Getter
public class Row {

    private Section section;
    private String rowId;
    private SortedSet<Seat> seats;
    private Boolean full = Boolean.FALSE;
    private boolean reverseSortOrder;
    private int maxNumberOfSeats;

    public Row(final String rowId, final Section section, final int maxNumberOfSeats, final boolean reverseSortOrder) {
        this.rowId = rowId;
        this.section = section;
        this.maxNumberOfSeats = maxNumberOfSeats;
        this.reverseSortOrder = reverseSortOrder;
        init();
    }

    public boolean addSeat(Seat s) {
        full = seats.size() == maxNumberOfSeats;
        if (BooleanUtils.isFalse(full)) {
            seats.add(s);
            return true;
        }
        return false;
    }

    private void init() {
        if (reverseSortOrder) {
            seats = new ConcurrentSkipListSet<>((s2, s1) ->
                    new CompareToBuilder().
                            append(s2.getRow().getSection(), s1.getRow().getSection()).
                            append(s2.getRow(), s1.getRow()).
                            append(s2.getNumber(), s1.getNumber()).build());
        } else {
            seats = new ConcurrentSkipListSet<>((s1, s2) ->
                    new CompareToBuilder().
                            append(s1.getRow().getSection(), s2.getRow().getSection()).
                            append(s1.getRow(), s2.getRow()).
                            append(s1.getNumber(), s2.getNumber()).build());
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Row) {
            Row rhs = (Row) obj;
            return new EqualsBuilder().append(section, rhs.getSection()).append(rowId, rhs.getRowId()).build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(section).append(rowId).build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(section).append("#").append(rowId).build();
    }
}
