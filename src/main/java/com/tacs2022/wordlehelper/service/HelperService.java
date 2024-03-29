package com.tacs2022.wordlehelper.service;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.play.LetterColor;
import com.tacs2022.wordlehelper.domain.play.LetterPlay;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HelperService {

    public static final int WORD_LENGTH = 5;

    private static final Map<Language, String> LANGUAGE_WORDS_FILE_MAP = Map.of(
            Language.EN, "src/main/resources/helper/5letter-english.list",
            Language.ES, "src/main/resources/helper/5letter-spanish.list"
    );

    /**
     * Generates a list of possible words for the given word play in the given language.
     *
     * @param wordPlay Attempted play
     * @param language Language the game was played in
     * @return Possible winning words
     */
    public List<String> getWordsByPlay(WordPlay wordPlay, Language language){

        List<String> possibleOptions = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(LANGUAGE_WORDS_FILE_MAP.get(language)))) {

            String grayRegex = buildNoneOfRegularExpression(wordPlay.getLettersByColor(LetterColor.GRAY));
            String greenRegex = buildAllInPositionRegularExpression(wordPlay.getLettersByColor(LetterColor.GREEN));

            possibleOptions = stream
                    .map(String::toLowerCase)
                    .filter(line -> line.matches(grayRegex))
                    .filter(line -> line.matches(greenRegex))
                    .filter(line -> hasEveryOfNotInPosition(line, wordPlay.getLettersByColor(LetterColor.YELLOW)))
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return possibleOptions;
    }

    //Regular Expressions doc
    //https://www.javatpoint.com/java-regex#:~:text=The%20Java%20Regex%20or%20Regular,the%20Java%20Regex%20Tester%20Tool.

    /**
     * Builds a regex string that represents words that contain all the given letters in their
     * respective positions
     *
     * @param lettersPlayed List of letters to build the regex from
     * @return 'All In Position' regular expression
     */
    private String buildAllInPositionRegularExpression(List<LetterPlay> lettersPlayed){
        StringBuilder regex = new StringBuilder();
        for (int i = 0 ; i < WORD_LENGTH ; i++){
            regex.append(getCharacterAtPositionIfExists(lettersPlayed, i));
        }
        return regex.toString();
    }

    private Character getCharacterAtPositionIfExists(List<LetterPlay> lettersPlayed, Integer p){
        for (LetterPlay letterPlay : lettersPlayed){
            if (letterPlay.getPosition().equals(p)) return letterPlay.getLetter();
        }
        return '.';//Any character for regular expressions
    }

    /**
     * Builds a regex string that represents words that contain none of the given words in any position.
     *
     * @param lettersPlayed List of letters to build the regex from
     * @return 'None Of' regular expression
     */
    private String buildNoneOfRegularExpression(List<LetterPlay> lettersPlayed){
        //string of all characters together
        String letters = lettersPlayed.stream().map(l -> l.getLetter().toString()).reduce((acc, e) -> acc  + e).orElse(".");
        //regex that represents if a character doesn't match any of letters
        String notAnyLetterRegex = !letters.equals(".") ? "[^" + letters + "]" : letters; //in case list is empty return 'any' regular expression
        //repeat five times for all positions
        return notAnyLetterRegex.repeat(WORD_LENGTH);
    }

    /**
     * Checks if the given word contains all the letters played in a position different from
     * the given one.
     *
     * @param word Word to test
     * @param lettersPlayed List of letters
     * @return true if word contains all letters but in another position, false if at least one letter is in the given position or one letter is not in the string
     */
    private boolean hasEveryOfNotInPosition(String word, List<LetterPlay> lettersPlayed){
        for (LetterPlay letter : lettersPlayed){
            if (!word.contains(letter.getLetter().toString()) //word does not contain letter
                    || (word.contains(letter.getLetter().toString()) && word.charAt(letter.getPosition()) == letter.getLetter())) //word contains letter but in rejected position
                return false;
        }
        return true;
    }
}