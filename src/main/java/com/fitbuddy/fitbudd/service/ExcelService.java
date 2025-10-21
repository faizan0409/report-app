package com.fitbuddy.fitbudd.service;

import com.fitbuddy.fitbudd.entity.Report;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    private static final int CHART_WIDTH = 25;
    private static final int CHART_HEIGHT = 15;
    private static final String[] CHART_COLORS = {
            "4472C4", "ED7D31", "A5A5A5", "FFC000", "5B9BD5", "70AD47", "264478",
            "9E480E", "636363", "997300", "FF0000", "00FF00", "0000FF", "FF00FF",
            "00FFFF", "800000", "008000", "000080", "800080", "008080", "FF6B6B",
            "4ECDC4", "45B7D1", "96CEB4", "FFEEAD", "D4A4EB", "A8E6CF", "FFD3B6"
    };

    public byte[] generateExcelReport(List<Report> reportData, String weekId) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet dataSheet = workbook.createSheet("Data");
            XSSFSheet chartsSheet = workbook.createSheet("Charts");
            XSSFCellStyle headerStyle = createHeaderStyle(workbook);
            XSSFCellStyle dataStyle = createDataStyle(workbook);
            createHeader(workbook, dataSheet, weekId);
            createDataSheet(dataSheet, reportData, headerStyle, dataStyle);


            if (!reportData.isEmpty()) {
                createEnhancedBarChart(workbook, chartsSheet, dataSheet, reportData);
                createEnhancedPieChart(workbook, chartsSheet, dataSheet, reportData);
            } else {
                XSSFRow row = chartsSheet.createRow(0);
                row.createCell(0).setCellValue("No data available for charts");
            }

            setChartSheetColumnWidths(chartsSheet);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
    }

    private XSSFCellStyle createDataStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createHeader(XSSFWorkbook workbook, XSSFSheet sheet, String weekId) {
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Weekly Gym Performance Report - Week " + weekId);
        headerCell.setCellStyle(headerStyle);


    }

    private void createDataSheet(XSSFSheet sheet, List<Report> reportData,
                                 XSSFCellStyle headerStyle, XSSFCellStyle dataStyle) {
        XSSFRow headerRow = sheet.createRow(0);
        String[] columns = {
                 "Coach Name", "Week ID", "Start Date", "End Date",
                "Gym Location", "Average Feedback", "Average Hours Led By Coach",
                "Delta of Coach Workouts Percent", "Delta of Minimum Feedback Percent",
                "Minimum Feedback", "Number of Unique Coaches", "Total Hours",
                "Total Hours By Coach", "Total Rating", "Total Workouts",
                "Workouts Led By Each Coach"
        };

        for (int i = 0; i < columns.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Report report : reportData) {
            XSSFRow row = sheet.createRow(rowNum++);
            createDataRow(row, report, dataStyle);
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDataRow(XSSFRow row, Report report, XSSFCellStyle dataStyle) {
        createCell(row, 0, report.getCoachName(), dataStyle);
        createCell(row, 1, report.getWeekId(), dataStyle);
        createCell(row, 2, report.getTimePeriodStart().toString(), dataStyle);
        createCell(row, 3, report.getTimePeriodEnd().toString(), dataStyle);
        createCell(row, 4, report.getGymLocation(), dataStyle);
        createCell(row, 5, report.getAverageFeedback(), dataStyle);
        createCell(row, 6, report.getAverageHoursLeadByCoach(), dataStyle);
        createCell(row, 7, report.getDeltaOfCoachWorkoutsPercent(), dataStyle);
        createCell(row, 8, report.getDeltaOfMinimumFeedbackPercent(), dataStyle);
        createCell(row, 9, report.getMinimumFeedback(), dataStyle);
        createCell(row, 10, report.getNoOfUniqueCoaches(), dataStyle);
        createCell(row, 11, report.getTotalHours(), dataStyle);
        createCell(row, 12, report.getTotalHoursByCoach(), dataStyle);
        createCell(row, 13, report.getTotalRating(), dataStyle);
        createCell(row, 14, report.getTotalWorkouts(), dataStyle);
        createCell(row, 15, report.getWorkoutsLedByEachCoach(), dataStyle);
    }

    private void createEnhancedBarChart(XSSFWorkbook workbook, XSSFSheet chartsSheet,
                                        XSSFSheet dataSheet, List<Report> reportData) {
        XSSFDrawing drawing = chartsSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0,
                2, 1, CHART_WIDTH - 2, CHART_HEIGHT - 1);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Average Feedback by Coach");
        chart.setTitleOverlay(false);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Coaches");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Average Feedback");
        leftAxis.setMinimum(0.0);
        leftAxis.setMaximum(5.0);
        leftAxis.setMajorUnit(0.5);
        leftAxis.setNumberFormat("0.00");

        XDDFDataSource<String> coaches = XDDFDataSourcesFactory.fromStringCellRange(dataSheet,
                new CellRangeAddress(1, reportData.size(), 1, 1));
        XDDFNumericalDataSource<Double> feedback = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet,
                new CellRangeAddress(1, reportData.size(), 6, 6));
        XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        data.setBarDirection(BarDirection.COL);
        XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(coaches, feedback);
        series.setTitle("Average Feedback", null);
        chart.plot(data);

        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(hexToBytes(CHART_COLORS[0])));
        if (series.getShapeProperties() == null) {
            series.setShapeProperties(new XDDFShapeProperties());
        }
        series.getShapeProperties().setFillProperties(fill);
    }

    private void createEnhancedPieChart(XSSFWorkbook workbook, XSSFSheet chartsSheet,
                                        XSSFSheet dataSheet, List<Report> reportData) {
        XSSFDrawing drawing = chartsSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, CHART_HEIGHT + 2,
                CHART_WIDTH, CHART_HEIGHT * 2 + 2);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Workouts Led by Each Coach");
        chart.setTitleOverlay(false);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.RIGHT);
        legend.setOverlay(false);

        XDDFDataSource<String> coaches = XDDFDataSourcesFactory.fromStringCellRange(dataSheet,
                new CellRangeAddress(1, reportData.size(), 0, 0)); // Changed to column 0 for coach names
        XDDFNumericalDataSource<Double> workouts = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet,
                new CellRangeAddress(1, reportData.size(), 15, 15)); // Assuming this is still the correct column for workouts

        XDDFPieChartData data = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
        XDDFPieChartData.Series series = (XDDFPieChartData.Series) data.addSeries(coaches, workouts);
        series.setTitle("Workouts Led", null);

        chart.plot(data);

        // Add data labels
        if (chart.getCTChart().getPlotArea().getPieChartArray(0).getDLbls() == null) {
            chart.getCTChart().getPlotArea().getPieChartArray(0).addNewDLbls();
        }
        var dLbls = chart.getCTChart().getPlotArea().getPieChartArray(0).getDLbls();
        if (dLbls.getShowVal() == null) dLbls.addNewShowVal();
        dLbls.getShowVal().setVal(true);
        if (dLbls.getShowLeaderLines() == null) dLbls.addNewShowLeaderLines();
        dLbls.getShowLeaderLines().setVal(true);

        // Set colors for each slice
        var ctPieChart = chart.getCTChart().getPlotArea().getPieChartArray(0);
        var serList = ctPieChart.getSerArray(0);
        for (int i = 0; i < reportData.size(); i++) {
            if (serList.getDPtList().size() <= i) {
                serList.addNewDPt();
                serList.getDPtArray(i).addNewIdx().setVal(i);
                serList.getDPtArray(i).addNewSpPr();
            }
            var spPr = serList.getDPtArray(i).getSpPr();
            var solidFill = spPr.addNewSolidFill();
            var color = solidFill.addNewSrgbClr();
            color.setVal(hexToBytes(CHART_COLORS[i % CHART_COLORS.length]));
        }
    }

    private void setChartSheetColumnWidths(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 256 * 6);
        for (int i = 1; i < 15; i++) {
            sheet.setColumnWidth(i, 256 * 7);
        }
        sheet.setDefaultRowHeight((short)(20 * 20));
    }

    private void createCell(XSSFRow row, int column, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        cell.setCellStyle(style);
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}