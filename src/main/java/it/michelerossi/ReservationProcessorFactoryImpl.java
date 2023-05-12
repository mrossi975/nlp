package it.michelerossi;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Stream;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

/**
 * Apache OpenNLP implementation of {@link it.michelerossi.ReservationProcessorFactory} which returns
 * an instance of {@link ReservationProcessor} capable of dealing with the specified reservation.
 * This factory will return a different implementation depending on the language detected by OpenNLP in the
 * specified text.
 */
public class ReservationProcessorFactoryImpl implements ReservationProcessorFactory {
    private final LanguageDetector languageDetector;
    public ReservationProcessorFactoryImpl() {
        try {
            var languageDetectorModel = new LanguageDetectorModel(getClass().getResource("/langdetect-183.bin"));
            this.languageDetector = new LanguageDetectorME(languageDetectorModel);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to init factory", ex);
        }
    }

    @Override
    public ReservationProcessor getReservationProcessor(String reservationFreeText) {
        try {
            var languages = languageDetector.predictLanguages(reservationFreeText);
            var mostLikelyLanguageOpt = Stream.of(languages).max(Comparator.comparingDouble(Language::getConfidence));
            if (mostLikelyLanguageOpt.isEmpty()) {
                throw new IllegalArgumentException("Unable to detect language");
            }
            var mostLikelyLanguage = mostLikelyLanguageOpt.get().getLang();
            return switch (mostLikelyLanguage) {
                case "eng" -> new ReservationProcessorEngImpl();
                case "deu" -> throw new IllegalArgumentException("Support for German coming soon");
                default -> throw new IllegalArgumentException(mostLikelyLanguage + " not yet supported");
            };
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception while attempting to process specified text", ex);
        }
    }
}
