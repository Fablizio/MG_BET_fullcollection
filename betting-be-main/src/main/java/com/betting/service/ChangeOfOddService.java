package com.betting.service;

import com.betting.converter.ChangeOfOddMapper;
import com.betting.dto.ChangeOfOddResponseDTO;
import com.betting.entity.ChangeOfOdd;
import com.betting.entity.Odd;
import com.betting.repository.ChangeOfOddRepository;
import com.betting.repository.OddRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChangeOfOddService {

    @Autowired
    private ChangeOfOddRepository changeOfOddRepository;

    @Autowired
    private OddRepository oddRepository;

    public List<ChangeOfOddResponseDTO> findByOdd(String idOdd) {


        Odd odd = oddRepository.findById(Long.parseLong(idOdd)).orElse(null);

        if (odd == null) return new ArrayList<>();

        List<ChangeOfOdd> result = changeOfOddRepository.findByOdd(odd);



        return changeOfOddRepository.findByOdd(odd)
                .stream()
                .sorted(Comparator.comparing(ChangeOfOdd::getUpdateData))
                .map(ChangeOfOddMapper::fromEntity)
                .collect(Collectors.toList());


    }
}
