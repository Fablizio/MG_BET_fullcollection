package it.betting.batch.service;

import it.betting.batch.entity.Odd;

public interface OddService {
    Odd findByHomeTeamAndOutHomeTeam(String team);
    void save(Odd odd);
    void update(Odd entity);
    void createRaddoppio();
}
