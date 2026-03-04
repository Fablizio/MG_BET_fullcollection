package it.betting.batch.service;

import it.betting.batch.entity.Raddoppio;
import it.betting.batch.excel.ExcelService;
import it.betting.batch.repository.RaddoppioRepository;
import it.betting.batch.repository.ReportDoublingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class ReportDoublingService {


    @Autowired
    RaddoppioRepository raddoppioRepository;

    @Autowired
    ReportDoublingRepository reportDoublingRepository;

    public void createReportDoubling() {

        Calendar calendar =Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);

        List<Raddoppio> raddoppi = raddoppioRepository.findByPubblicataLessThan(calendar.getTime());
        ExcelService.createReportExcel(raddoppi);
//        reportDoublingRepository.save(
//                ReportDoubling.builder()
//                        .excel(ExcelService.createReportExcel(raddoppi))
//                        .dataCreation(new Date())
//                        .build());


    }


}
