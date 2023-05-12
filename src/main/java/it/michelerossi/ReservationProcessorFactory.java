package it.michelerossi;

/** used to obtain an instance of {@link it.michelerossi.ReservationProcessor} for the specified reservation specified in free text */
public interface ReservationProcessorFactory {
    ReservationProcessor getReservationProcessor(String reservationFreeText);
}
