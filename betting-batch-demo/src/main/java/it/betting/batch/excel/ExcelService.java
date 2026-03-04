package it.betting.batch.excel;


import it.betting.batch.constant.BettingBatchCostants;
import it.betting.batch.entity.Raddoppio;
import it.betting.batch.util.BettingUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CommonsLog
public class ExcelService {


    public static byte[] createReportExcel(List<Raddoppio> list) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Report Doubling");

        AtomicInteger indexRow = new AtomicInteger(0);
        AtomicInteger indexCell = new AtomicInteger(0);

        XSSFCellStyle border = workbook.createCellStyle();
        border.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        border.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        border.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        border.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);

        createCellAndSetValue(sheet, indexRow.get(), indexCell.getAndIncrement(), "Data", border);
        createCellAndSetValue(sheet, indexRow.get(), indexCell.getAndIncrement(), "Partita", border);
        createCellAndSetValue(sheet, indexRow.get(), indexCell.getAndIncrement(), "Prediction", border);
        createCellAndSetValue(sheet, indexRow.get(), indexCell.getAndIncrement(), "Risultato Finale", border);


        list.forEach(raddoppio -> raddoppio.getOdds().forEach(odd -> {

            if (odd != null) {

                String prediction = odd.getPrediction().split("oppure")[0].trim();
                boolean presa = BettingUtils.presa(prediction, odd.getFinalResult(), raddoppio.getType());
                XSSFCellStyle style = greenOrRed(workbook, presa);

                createCellAndSetValue(sheet, indexRow.incrementAndGet(), 0, BettingUtils.convertDateToString(raddoppio.getPubblicata()), style);
                createCellAndSetValue(sheet, indexRow.get(), 1, odd.getTeam(),style);


                if (raddoppio.getType().equals(BettingBatchCostants.SINGLE_MATCH))
                    createCellAndSetValue(sheet, indexRow.get(), 2, prediction.equals("1") ? "1" : "2", style);

                if (raddoppio.getType().equals(BettingBatchCostants.TWO_MATCHES))
                    createCellAndSetValue(sheet, indexRow.get(), 2, prediction.equals("1") ? "1" : "2", style);

                if (raddoppio.getType().equals(BettingBatchCostants.DOPPIA_CHANCE))
                    createCellAndSetValue(sheet, indexRow.get(), 2, prediction.equals("1") ? "1X" : "X2", style);

                createCellAndSetValue(sheet, indexRow.get(), 3, odd.getFinalResult(), style);


            }
        }));


        for (int i = 0; i < 4; i++)
            sheet.autoSizeColumn(i);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            workbook.write(baos);
            workbook.close();
        } catch (IOException e) {
            log.error("Errore durante la generazione del file Excel", e);
        }


        try {
            FileOutputStream fos = new FileOutputStream(new File("/test/ciccio.xlsx"));
            fos.write(baos.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    private static XSSFCellStyle greenOrRed(XSSFWorkbook workbook, boolean presa) {

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(presa ? IndexedColors.GREEN.getIndex() : IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
        return style;
    }

    private static void createCellAndSetValue(XSSFSheet sheet, int indexRow, int indexCell, String value, XSSFCellStyle style) {
        XSSFRow row = sheet.getRow(indexRow);
        if (row == null) row = sheet.createRow(indexRow);
        XSSFCell cell = row.createCell(indexCell);
        cell.setCellValue(value);
        cell.setCellStyle(style);


    }

}
