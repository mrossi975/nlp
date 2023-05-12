package it.michelerossi;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("Enter the reservation as free text: ");
        var reservationText = new Scanner(System.in).nextLine();
        var processorFactory = new ReservationProcessorFactoryImpl();
        var processor = processorFactory.getReservationProcessor(reservationText);
        var reservation = processor.getReservation(reservationText);
        System.out.println("Reservation details: "+reservation);
    }
}