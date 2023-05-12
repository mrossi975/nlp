package it.michelerossi;

/** Holds details of a restaurant reservation */
public record Reservation(String name, String date, String time, int numPeople) {
}
