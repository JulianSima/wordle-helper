package com.tacs2022.wordlehelper.controller;

import com.tacs2022.wordlehelper.controller.Exceptions.MissingAttributesException;
import com.tacs2022.wordlehelper.domain.tournaments.Leaderboard;
import com.tacs2022.wordlehelper.domain.tournaments.Position;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.dtos.AddParticipantDto;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.HashMapChangeSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequestMapping("/tournaments")
@RestController()
public class TournamentController {
    @Autowired
    TournamentService tournamentService;
    @Autowired
    UserService userService;

    @GetMapping()
    public Map<String, List<Tournament>> getAllTournaments(@RequestParam(required = false) String role, @RequestParam(required = false) String status) {
        Map<String, List<Tournament>> response = new HashMap<>();
        response.put(
                "tournaments", tournamentService.findAll(role, status)
        );
        return response;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Tournament create(@RequestBody Tournament tournament){
        return tournamentService.save(tournament);
    }

    @GetMapping("/{id}")
    public Tournament getTournamentById(@PathVariable(value = "id") Long id) {
        return tournamentService.findById(id);
    }

    @GetMapping("/{id}/leaderboard")
    public Map<String, List<Position>> getLeaderboardByTournamentId(@PathVariable(value = "id") Long tournamentId){
        Map<String, List<Position>> response = new HashMap<>();
        response.put(
               "leaderboard", tournamentService.getTournamentLeaderboard(tournamentId)
        );

        return response;
    }

	@PostMapping(value="/{id}/participants")
    public ResponseEntity<Map<String, String>> addParticipant(@Valid @RequestBody AddParticipantDto body, @PathVariable(value = "id") Long tournamentId){
        Long idParticipant = body.getIdParticipant();
        if(idParticipant==null){ //TODO: validar en dto
            throw new MissingAttributesException("idParticipant");
        }

        tournamentService.addParticipant(tournamentId, userService.findById(idParticipant));

        return ResponseEntity.noContent().build();
    }

}
