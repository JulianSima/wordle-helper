package com.tacs2022.wordlehelper.dtos.tournaments;

import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutputTournamentsDto {
    List<Tournament> tournaments;
}
