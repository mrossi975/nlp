package it.michelerossi;

/** Used to obtain a {@link it.michelerossi.Reservation} object from a textual representation, typically in natural language */
public interface ReservationProcessor {

    Reservation getReservation(String reservationDetails);
}
