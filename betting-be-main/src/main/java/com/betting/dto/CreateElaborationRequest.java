package com.betting.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateElaborationRequest {

    // ID delle partite da elaborare
    private List<Long> matchIds;
}