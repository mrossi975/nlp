package it.michelerossi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationProcessorTest {
    private final ReservationProcessorFactory factory = new ReservationProcessorFactoryImpl();

    @Test
    void testReservationProcessor1() {
        testReservationProcessor(
            "Hello, please reserve a table for two people on 19.3. at 8:00 p.m., many thanks John Cooper",
            "John Cooper",
            "19.3",
            "8:00 p.m.",
            2);
    }

    @Test
    void testReservationProcessor2() {
        testReservationProcessor(
            "Dear Sir or Madam, We would like to come for brunch on April 9th at 9:45 am with six people, Kind regards Sarah Ferguson",
            "Sarah Ferguson",
            "April 9th",
            "9:45 am",
            6);
    }

    @Test
    void testReservationProcessor3() {
        testReservationProcessor(
            "Hello, a table for 8 people on 1.5. 9 p.m., Greetings from Andrew Smith",
            "Andrew Smith",
            "1.5",
            "9 p.m.",
            8);
    }



    void testReservationProcessor(
        String freeTextReservation,
        String expectedNameOnReservation,
        String expectedDateOnReservation,
        String expectedTimeOnReservation,
        int expectedNumberOfPeopleOnReservation) {

        var processor = factory.getReservationProcessor(freeTextReservation);
        var reservation = processor.getReservation(freeTextReservation);
        assertEquals(expectedNameOnReservation, reservation.name());
        assertEquals(expectedDateOnReservation, reservation.date());
        assertEquals(expectedTimeOnReservation, reservation.time());
        assertEquals(expectedNumberOfPeopleOnReservation, reservation.numPeople());
    }
}
