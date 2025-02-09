package ru.aleksandrchistov.budget.pages.files.util;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataDto;
import ru.aleksandrchistov.budget.pages.budget.dto.BudgetDataMonthDto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.HashMap;

@UtilityClass
public class FileToExcelUtility {
    private static final Logger logger = LoggerFactory.getLogger(FileToExcelUtility.class);

    private static int currentBudgetItemId;
    private static int currentMonth = 0;

    public static void writeToExcel(Path path, HashMap<Integer, BudgetDataDto> planDtos) {
        try {
            FileInputStream inputStream = new FileInputStream(String.valueOf(path));
            Workbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet sheet : workbook) {
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                for (int rowIndex = firstRow + 3; rowIndex <= lastRow; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);

                    for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        writeToCellValue(planDtos, cell, cellIndex);
                    }
                    currentMonth = 0;
                }
            }

            try (FileOutputStream out = new FileOutputStream(path.toString())) {
                workbook.write(out);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            inputStream.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void writeToCellValue(HashMap<Integer, BudgetDataDto> plans, Cell cell, int cellIndex) {
        CellType cellType = cell.getCellType().equals(CellType.FORMULA) ? cell.getCachedFormulaResultType() : cell.getCellType();
        if (cellType.equals(CellType.NUMERIC) && !DateUtil.isCellDateFormatted(cell)) {
            if (cellIndex == 1) {
                currentBudgetItemId = (int) cell.getNumericCellValue();
            } else {
                BudgetDataDto dto = plans.get(currentBudgetItemId);
                BudgetDataMonthDto[] months = dto.getMonths();
                if (cellIndex == 27) {
                    cell.setCellValue(dto.getPlanTotal().doubleValue());
                } else if (cellIndex == 28) {
                    cell.setCellValue(dto.getActualTotal().doubleValue());
                } else if (cellIndex % 2 == 0) {
                    BigDecimal actual = months[currentMonth].getActual();
                    cell.setCellValue(actual.doubleValue());
                    currentMonth = currentMonth + 1;
                } else {
                    BigDecimal plan = months[currentMonth].getPlan();
                    cell.setCellValue(plan.doubleValue());
                }
            }
        }
    }

}
