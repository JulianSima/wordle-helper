package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import com.tacs2022.wordlehelper.dtos.helper.OutputPossibleWordsDto;
import com.tacs2022.wordlehelper.service.HelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/helper")
public class HelperController {
    @Autowired
    private HelperService helperService;

    @GetMapping("/words")
    public OutputPossibleWordsDto getPossibleWords(@RequestParam(value = "grey") String greyLettersPlayed,
                                                      @RequestParam(value = "yellow") String yellowLettersPlayed,
                                                      @RequestParam(value = "green") String greenLettersPlayed) {
        //TODO: verify correct format of params => yellow and green must be five characters long, with only letters and '_'
        WordPlay attemptedPlay = new WordPlay(greyLettersPlayed, yellowLettersPlayed, greenLettersPlayed);
        //TODO: receive and validate language
        List<String> possibleWords = helperService.getWordsByPlay(attemptedPlay, Language.ES);
        return new OutputPossibleWordsDto(possibleWords);
    }
}
