package ru.aleksandrchistov.budget.pages.files.util;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import ru.aleksandrchistov.budget.common.error.DataConflictException;
import ru.aleksandrchistov.budget.common.error.NotFoundException;
import ru.aleksandrchistov.budget.pages.budget.model.BudgetMonth;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class FileFromExcelUtility {

    private static int currentBudgetItemId;
    private static int currentMonth = 0;

    public static void writeFromExcel(MultipartFile file, List<BudgetMonth> plans) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (Sheet sheet : workbook) {
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                for (int rowIndex = firstRow + 3; rowIndex <= lastRow - 1; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);

                    for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        writeFromCellValue(plans, cell, cellIndex, evaluator);
                    }
                    currentMonth = 0;
                }
            }
        } catch (IOException e) {
            throw new DataConflictException(e.getMessage());
        }
    }

    private static void writeFromCellValue(List<BudgetMonth> plans, Cell cell, int cellIndex, FormulaEvaluator evaluator) {
        CellType cellType = cell.getCellType().equals(CellType.FORMULA) ? evaluator.evaluateFormulaCell(cell) : cell.getCellType();
        if (cellType.equals(CellType.NUMERIC) && !DateUtil.isCellDateFormatted(cell)) {
            if (cellIndex == 1) {
                currentBudgetItemId = (int) cell.getNumericCellValue();
            } else {
                List<BudgetMonth> filteredPlans = filterPlansByBudgetItemId(plans, currentBudgetItemId);
                if (cellIndex == 27) {
                    BudgetMonth planByIndex = filteredPlans.get(12);
                    planByIndex.setSum(BigDecimal.valueOf(cell.getNumericCellValue()));
                } else if (cellIndex % 2 != 0 && cellIndex < 27) {
                    BudgetMonth planByIndex = findPlanByIndex(filteredPlans, currentMonth);
                    planByIndex.setSum(BigDecimal.valueOf(cell.getNumericCellValue()));
                    currentMonth = currentMonth + 1;
                }
            }
        }
    }

    private static List<BudgetMonth> filterPlansByBudgetItemId(List<BudgetMonth> plans, int budgetItemId) {
        List<BudgetMonth> found = plans.stream()
                .filter(plan -> plan.getBudgetItem().getId().equals(budgetItemId))
                .toList();
        if (found.isEmpty()) {
            throw new NotFoundException("Budget plans with budget item ID " + budgetItemId + " not found");
        }
        return found;
    }

    private static BudgetMonth findPlanByIndex(List<BudgetMonth> plans, int index) {
        BudgetMonth found = plans.stream()
                .filter(plan -> plan.getIndex() == index)
                .findAny()
                .orElse(null);
        if (found == null) {
            throw new NotFoundException("Budget plan with index " + index + " not found");
        }
        return found;
    }

}
