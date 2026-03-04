package it.smibet.controller;

import it.smibet.dto.DailyBetDTO;
import it.smibet.dto.OddDTO;
import it.smibet.dto.UserListDTO;
import it.smibet.enumeration.BetType;
import it.smibet.service.DailyBetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web")
public class DailyBetController {

    @Autowired
    private DailyBetService dailyBetService;


    @GetMapping(value = "/dailyBet")
    public String getDailyBet(ModelMap modelMap) {
        modelMap.addAttribute("dailyBet",null);
        return "views/dailyBet";
    }
    @GetMapping(value = "/dailyBet/initial")
    public String initial(ModelMap modelMap, @RequestParam(value = "rows")String rows) {

        DailyBetDTO dailyBetDTO = new DailyBetDTO();
        for(int i = 0; i < Integer.parseInt(rows); i++)
            dailyBetDTO.getOdds().add(new OddDTO(" "," "," "," "));

        modelMap.addAttribute("dailyBet",null);
        modelMap.addAttribute("bets",dailyBetDTO);
        return getDailyBet(modelMap);
    }
    @PostMapping(value = "/dailyBet")
    public String save(ModelMap modelMap, @ModelAttribute("bets") DailyBetDTO dailyBetDTO) {

        dailyBetService.save(dailyBetDTO, BetType.DAILY_BET);
        return getDailyBet(modelMap);
    }

    @GetMapping(value = "/dailyBet/all")
    public String getAll(ModelMap modelMap){
        modelMap.addAttribute("dailyBets",dailyBetService.getAll(BetType.DAILY_BET));
        return "views/dailyBetAll";
    }



}
