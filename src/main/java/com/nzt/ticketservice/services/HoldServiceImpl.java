package com.nzt.ticketservice.services;

import com.google.common.collect.Lists;
import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.data.Row;
import com.nzt.ticketservice.data.Seat;
import com.nzt.ticketservice.enums.Section;
import com.nzt.ticketservice.exceptions.NoMoreSeats;
import com.nzt.ticketservice.exceptions.NotEnoughSeats;
import com.nzt.ticketservice.util.VenueUtil;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @see HoldService
 * @see java.util.Observer
 */
@XSlf4j
@Service
public class HoldServiceImpl implements HoldService {

    private final ConcurrentSkipListMap<Integer, SeatHold> heldSeatsMap = new ConcurrentSkipListMap<>();


    private VenueService venueService = new VenueService();


    @Value("${hold.timeout.in.seconds}")
    private Integer holdTimeout;


    @Override
    public SeatHold holdSeats(int numSeats, String customerEmail) throws NoMoreSeats, NotEnoughSeats {

        List<Seat> seats = venueService.holdSeats(numSeats);
        SeatHold newHold = new SeatHold(holdTimeout, customerEmail, seats);
        newHold.setSeatsAvailable(Boolean.TRUE);
        newHold.addObserver(this);
        heldSeatsMap.put(newHold.getSeatHoldId(), newHold);
        return newHold;

    }

    @Override
    public Optional<SeatHold> findById(int seatHoldId) {
        if (heldSeatsMap.containsKey(seatHoldId)) {
            return Optional.of(heldSeatsMap.get(seatHoldId));
        }
        return Optional.empty();
    }

    @Override
    public int numOfAvaiableSeats() {
        return venueService.getAvailableSeats();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof List) {
            List<Seat> heldSeats = (List<Seat>) arg;
            venueService.releaseHeldSeats(heldSeats);
            SeatHold hold = (SeatHold) o;
            heldSeatsMap.remove(hold.getSeatHoldId());
        }
    }


    private static final class VenueService {

        private final Set<Row> sectionASeats = VenueUtil.initSectionSeats(Section.A);
        private final Set<Row> sectionBSeats = VenueUtil.initSectionSeats(Section.B);
        private final Set<Row> sectionESeats = VenueUtil.initSectionSeats(Section.E);
        private final Set<Row> sectionCSeats = VenueUtil.initSectionSeats(Section.C);
        private final Set<Row> sectionCSeatsExt = VenueUtil.initSectionSeats(Section.C, true);
        private final Set<Row> sectionDSeats = VenueUtil.initSectionSeats(Section.D);
        private final Set<Row> sectionDSeatsExt = VenueUtil.initSectionSeats(Section.D, true);
        private final Set<Row> sectionFSeats = VenueUtil.initSectionSeats(Section.F);
        private final Set<Row> sectionFSeatsExt = VenueUtil.initSectionSeats(Section.F, true);

        private ConcurrentSkipListMap<String, List<Seat>> reservedSeats;



        public int getAvailableSeats() {
            int numOfSeatsInSectionA = sectionASeats.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionB = sectionBSeats.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionC = sectionCSeats.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionCExt = sectionCSeatsExt.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionD = sectionDSeats.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionDExt = sectionDSeatsExt.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionE = sectionESeats.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionF = sectionFSeats.stream().mapToInt(r -> r.getSeats().size()).sum();
            int numOfSeatsInSectionFExt = sectionFSeatsExt.stream().mapToInt(r -> r.getSeats().size()).sum();

            int sum = numOfSeatsInSectionA + numOfSeatsInSectionB +
                    numOfSeatsInSectionC + numOfSeatsInSectionCExt +
                    numOfSeatsInSectionD + numOfSeatsInSectionDExt +
                    numOfSeatsInSectionE +
                    numOfSeatsInSectionF + numOfSeatsInSectionFExt;

            return sum;
        }


        public List<Seat> holdSeats(final int numSeats) throws NotEnoughSeats, NoMoreSeats {
            int availableSeats = getAvailableSeats();
            if (availableSeats == 0) throw new NoMoreSeats("No more seats available");
            if (availableSeats < numSeats) throw new NotEnoughSeats("Not enough seats avaiable");
            final List<Seat> heldSeats = Lists.newLinkedList();
            EnumSet.allOf(Section.class).stream().forEach(s -> {
                int unfilledRequest = numSeats - heldSeats.size();
                if (unfilledRequest != 0) {
                    heldSeats.addAll(getMaxSeatsFromSection(s, unfilledRequest));
                }
            });
            return heldSeats;
        }


        private void helper(final Set<Row> set, final List<Seat> heldSeats, int unfilledRequest) {
            if (set.size() >= 0) {
                int count = 0;
                Iterator<Row> itr = set.iterator();
                while (count < unfilledRequest && itr.hasNext()) {
                    Row r = itr.next();
                    Iterator<Seat> itr2 = r.getSeats().iterator();
                    while (count < unfilledRequest && itr2.hasNext()) {
                        Seat s = itr2.next();
                        heldSeats.add(s);
                        itr2.remove();
                        count++;
                    }
                }
                if (log.isTraceEnabled()) {
                    log.trace("Seats left after hold: {}", set.stream().mapToInt(r -> r.getSeats().size()).sum());
                }
            }
        }

        private List<Seat> getMaxSeatsFromSection(final Section section, int unfilledRequest) {
            final List<Seat> heldSeats = Lists.newLinkedList();
            switch (section) {
                case A:
                    helper(sectionASeats, heldSeats, unfilledRequest);
                    break;
                case B:
                    helper(sectionBSeats, heldSeats, unfilledRequest);
                    break;
                case C:
                    helper(sectionCSeats, heldSeats, unfilledRequest);
                    if (unfilledRequest > heldSeats.size()) {
                        helper(sectionCSeatsExt, heldSeats, unfilledRequest - heldSeats.size());
                    }
                    break;
                case D:
                    helper(sectionDSeats, heldSeats, unfilledRequest);
                    if (unfilledRequest > heldSeats.size()) {
                        helper(sectionDSeatsExt, heldSeats, unfilledRequest - heldSeats.size());
                    }
                    break;
                case E:
                    helper(sectionESeats, heldSeats, unfilledRequest);
                    break;
                case F:
                    helper(sectionFSeats, heldSeats, unfilledRequest);
                    if (unfilledRequest > heldSeats.size()) {
                        helper(sectionFSeatsExt, heldSeats, unfilledRequest - heldSeats.size());
                    }
                    break;
                default:
                    break;
            }
            return heldSeats;
        }


        public void releaseHeldSeats(List<Seat> heldSeats) {
            for (Seat s : heldSeats) {
                s.getRow().addSeat(s);
            }
        }

    }
}
