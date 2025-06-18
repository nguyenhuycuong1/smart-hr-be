package com.devcuong.smart_hr.utils;



import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for working with Excel files using Apache POI
 */
public class POIUtils {

    public static Workbook createWorkbook() {
        return new HSSFWorkbook();
    }

    public static Sheet createSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    public static Cell createCell(Row row, int columnIndex, Object value) {
        Cell cell = row.createCell(columnIndex);
        setCellValue(cell, value);
        return cell;
    }

    public static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            setCellDateFormat(cell);
        } else if (value instanceof LocalDate) {
            // Convert LocalDate to Date and apply date formatting
            LocalDate localDate = (LocalDate) value;
            cell.setCellValue(java.sql.Date.valueOf(localDate));
            setCellDateFormat(cell);
        } else if (value instanceof LocalDateTime) {
            // Convert LocalDateTime to Date and apply date formatting
            LocalDateTime localDateTime = (LocalDateTime) value;
            cell.setCellValue(java.sql.Timestamp.valueOf(localDateTime));
            setCellDateFormat(cell);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Apply date format to a cell
     */
    private static void setCellDateFormat(Cell cell) {
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        cell.setCellStyle(dateStyle);
    }

    public static void createHeaderRow(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            if (headerStyle != null) {
                cell.setCellStyle(headerStyle);
            }
        }
    }

    public static ByteArrayInputStream workbookToByteArrayInputStream(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static interface WorkbookProcessor {
        void process(Workbook workbook) throws IOException;
    }

    public static void autoSizeColumns(Sheet sheet, int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static void autoSizeAllColumns(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() > 0) {
            Row firstRow = sheet.getRow(0);
            if (firstRow != null) {
                autoSizeColumns(sheet, firstRow.getPhysicalNumberOfCells());
            }
        }
    }

    public static void processWorkbook(WorkbookProcessor processor) throws IOException {
        try (Workbook workbook = new HSSFWorkbook()) {
            processor.process(workbook);
        }
    }

    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Background
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Font
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        // Alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Border
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    public static ByteArrayInputStream createSimpleExcel(String[] headers, Object[][] data) throws IOException {
        final ByteArrayOutputStream[] result = new ByteArrayOutputStream[1];

        processWorkbook(workbook -> {
            Sheet sheet = createSheet(workbook, "Data");

            // Create header
            CellStyle headerStyle = createHeaderStyle(workbook);
            createHeaderRow(sheet, headers, headerStyle);

            // Create data rows
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + 1);
                Object[] rowData = data[i];

                for (int j = 0; j < rowData.length; j++) {
                    createCell(row, j, rowData[j]);
                }
            }

            autoSizeColumns(sheet, headers.length);

            result[0] = new ByteArrayOutputStream();
            workbook.write(result[0]);
        });

        return new ByteArrayInputStream(result[0].toByteArray());
    }

}
