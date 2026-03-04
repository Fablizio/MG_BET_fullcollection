package com.betting.rest;

import com.betting.dto.SiteInsertRequest;
import com.betting.service.ISiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/site")
public class SiteController {

    @Autowired
    private ISiteService siteService;


    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody SiteInsertRequest request){
        siteService.save(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/enabled/{siteId}")
    public ResponseEntity<Void> enabled(@PathVariable(value = "siteId")String siteId,
                                        @RequestBody SiteInsertRequest request){

        siteService.enabled(siteId,request);
        return ResponseEntity.ok().build();
    }



}
