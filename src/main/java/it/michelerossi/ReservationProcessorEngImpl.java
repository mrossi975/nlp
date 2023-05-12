package it.michelerossi;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;


/** Apache OpenNLP implementation of {@link it.michelerossi.ReservationProcessor} for English language */
public class ReservationProcessorEngImpl implements ReservationProcessor {
    private static final List<String> MONTHS = List.of(
        "january",
        "february",
        "march",
        "april",
        "may",
        "june",
        "july",
        "august",
        "september",
        "october",
        "november",
        "december");

    private final TokenizerME tokenizer;
    private final NameFinderME personNameFinder;
    public ReservationProcessorEngImpl() throws IOException {
        var tokenizerModel = new TokenizerModel(getClass().getResource("/en-token.bin"));
        this.tokenizer = new TokenizerME(tokenizerModel);
        var personNamesModel = new TokenNameFinderModel(getClass().getResource("/en-ner-person.bin"));
        this.personNameFinder = new NameFinderME(personNamesModel);
    }
    @Override
    public Reservation getReservation(String reservationDetails) {
        var tokens = tokenizer.tokenize(reservationDetails);

        var personName = findPersonName(tokens);
        var numPeopleOnReservation = findNumPeople(tokens);
        var reservationDate = findReservationDate(tokens);
        var reservationTime = findReservationTime(tokens);
        return new Reservation(
            personName.orElse("Unknown"),
            reservationDate,
            reservationTime,
            numPeopleOnReservation);
    }

    private String findReservationTime(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            if (getNumber(token).isPresent() && i < tokens.length - 1 && isAmOrPmSpecifier(tokens[i+1])) {
                return token + " " + tokens[i+1];
            }
            if (token.contains(":")) {
                if (i < tokens.length - 1 && isAmOrPmSpecifier(tokens[i+1])) {
                    return token + " " + tokens[i + 1];
                }
                return token;
            }
        }
        throw new IllegalArgumentException("Unable to find reservation time");
    }

    private String findReservationDate(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            if (!token.equals(".") && (token.contains("/") || token.contains("."))) {
                var splitToken = token.split("[./]");
                if (getNumber(splitToken[0]).isPresent() && getNumber(splitToken[1]).isPresent()) {
                    return token;
                }
            }
            if (isMonth(token) && i < tokens.length - 1 && isOrdinalNumber(tokens[i+1])) {
                return token + " " + tokens[i + 1];
            }
        }
        throw new IllegalArgumentException("Unable to find reservation date");
    }

    private static boolean isMonth(String token) {
        return MONTHS.contains(token.toLowerCase());
    }

    private static boolean isOrdinalNumber(String token) {
        var lcToken = token.toLowerCase();
        int ix = lcToken.indexOf("th");
        if (ix == -1) {
            ix = lcToken.indexOf("nd");
        }
        if (ix == -1) {
            ix = lcToken.indexOf("rd");
        }
        if (ix == -1) {
            return false;
        }
        var numberPart = lcToken.substring(0, ix);
        return getNumber(numberPart).isPresent();
    }

    private int findNumPeople(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            var number = getNumber(token);
            if (number.isPresent() && i < tokens.length -1 && !isAmOrPmSpecifier(tokens[i+1])) {
                return number.get();
            }
        }
        throw new IllegalArgumentException("Unable to find the number of people for the reservation");
    }

    private static boolean isAmOrPmSpecifier(String token) {
        var lc = token.toLowerCase();
        return "am".equals(lc) || "pm".equals(lc) || "a.m.".equals(lc) || "p.m.".equals(lc);
    }
    // very crude.. should be improved, maybe it could be implemented with Apache OpenNLP
    private static Optional<Integer> getNumber(String token) {
        try {
            return Optional.of(Integer.parseInt(token));
        } catch (NumberFormatException e) {
            return getIntegerFromEnglishWord(token);
        }
    }

    private static Optional<Integer> getIntegerFromEnglishWord(String token) {
        return switch (token) {
            case "one" -> Optional.of(1);
            case "two" -> Optional.of(2);
            case "three" -> Optional.of(3);
            case "four" -> Optional.of(4);
            case "five" -> Optional.of(5);
            case "six" -> Optional.of(6);
            case "seven" -> Optional.of(7);
            case "eight" -> Optional.of(8);
            case "nine" -> Optional.of(9);
            case "ten" -> Optional.of(10);
            default -> Optional.empty();
        };
    }

    private Optional<String> findPersonName(String[] reservationDetailsTokens) {
        var personNameSpans = personNameFinder.find(reservationDetailsTokens);

        var name = new StringBuilder();

        for (var span : personNameSpans) {
            if ("person".equals(span.getType()) && span.getProb() > 0.6) {
                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    name.append(reservationDetailsTokens[i]).append(" ");
                }
            }
        }
        var fullName = name.toString().trim();
        if (fullName.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(fullName);
    }
}
