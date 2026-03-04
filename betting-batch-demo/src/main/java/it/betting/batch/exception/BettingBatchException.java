package it.betting.batch.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BettingBatchException extends RuntimeException{

    private String message;

}
