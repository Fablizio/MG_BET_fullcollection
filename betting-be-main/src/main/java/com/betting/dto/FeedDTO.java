package com.betting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDTO {

    private String autore;
    private String titolo;
    private String descrizione;
    private String link;

}
