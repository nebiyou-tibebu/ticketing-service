package com.nzt.ticketservice.data;

import com.google.common.hash.HashCode;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Seat {


    private Integer number;
    private Row row;
    private String seatId;


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Seat) {
            Seat other = (Seat) obj;
            return new EqualsBuilder().append(this.getRow().getSection(), other.getRow().getSection()).append(this.seatId, other.seatId).build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCode.fromString(row.getSection().name() + seatId).hashCode();
    }
}
