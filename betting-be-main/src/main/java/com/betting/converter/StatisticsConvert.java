package com.betting.converter;

import com.betting.dto.StatisticsDTO;
import com.betting.entity.Odd;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsConvert {

    public static StatisticsDTO convertForSite(List<Odd> odds,String data){
        AtomicInteger win = new AtomicInteger(0);
        AtomicInteger lose = new AtomicInteger(0);


        odds.forEach(rs->{
            if(rs.isPresa())win.set(win.get() +1);
            if(!rs.isPresa())lose.set(lose.get() +1);
        });

        return StatisticsDTO.builder()
                .data(data)
                .win(win.get())
                .lose(lose.get())
                .build();
    }


}
