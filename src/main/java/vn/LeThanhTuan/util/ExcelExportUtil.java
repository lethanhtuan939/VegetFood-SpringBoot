package vn.LeThanhTuan.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelExportUtil<T> {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<T> list;

    public ExcelExportUtil(List<T> list) {
        workbook = new XSSFWorkbook();
        this.list = list;
    }

    public void export(HttpServletResponse response, String baseName) throws IOException {
        writeHeaderLine(baseName);
        writeDataLines();

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + baseName + " " + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

    private void writeHeaderLine(String baseName) {
        sheet = workbook.createSheet(baseName);

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        if (list != null && !list.isEmpty()) {
            T firstItem = list.get(0);
            Field[] fields = firstItem.getClass().getDeclaredFields();
            int columnCount = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                createCell(row, columnCount++, fieldName, style);
            }
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        sheet.autoSizeColumn(columnCount);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }

        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        if (list != null && !list.isEmpty()) {
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontHeight(14);
            style.setFont(font);

            int rowCount = 1;
            for (T item : list) {
                Row row = sheet.createRow(rowCount++);
                Field[] fields = item.getClass().getDeclaredFields();
                int columnCount = 0;
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(item);
                        createCell(row, columnCount++, value, style);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

