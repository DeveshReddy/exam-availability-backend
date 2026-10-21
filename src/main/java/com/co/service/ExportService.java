package com.co.service;

import com.co.model.Student;
import com.co.model.ExamSubmission;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    // --- EXCEL EXPORT ---
    public ByteArrayInputStream exportToExcel(List<Student> students) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Collisions");

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Reg No", "Name", "Subject", "Date & Session", "FA Name", "FA Mobile"};

            // Create a bold style for the header
//            Font headerFont = workbook.createFont();
//            headerFont.setBold(true);
//            CellStyle headerCellStyle = workbook.createCellStyle();
//            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
//                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);

                // Column 0 & 1: Student Details
                row.createCell(0).setCellValue(s.getRegNo() != null ? s.getRegNo() : "-");
                row.createCell(1).setCellValue(s.getName() != null ? s.getName() : "-");

                if (s.getSubmissions() != null && !s.getSubmissions().isEmpty()) {
                    // Column 2: Joined Subjects
                    String subjects = s.getSubmissions().stream()
                            .map(e -> e.getSubjectName() != null ? e.getSubjectName() : "Unknown")
                            .collect(Collectors.joining(", "));
                    row.createCell(2).setCellValue(subjects);

                    // Column 3: Joined Date + Session (FIXED)
                    String dates = s.getSubmissions().stream()
                            .map(e -> {
                                String d = (e.getExamDate() != null) ? e.getExamDate().toString() : "-";
                                String sl = (e.getExamSlot() != null) ? e.getExamSlot() : "-";
                                return d + " (" + sl + ")";
                            })
                            .collect(Collectors.joining(", "));
                    row.createCell(3).setCellValue(dates);

                } else {
                    row.createCell(2).setCellValue("No Exams");
                    row.createCell(3).setCellValue("-");
                }

                // Column 4 & 5: FA Details
                row.createCell(4).setCellValue(s.getFaName() != null ? s.getFaName() : "-");
                row.createCell(5).setCellValue(s.getFaMobile() != null ? s.getFaMobile() : "-");
            }

            // Auto-size columns so long strings are visible
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // --- PDF EXPORT ---
    public ByteArrayInputStream exportToPdf(List<Student> students) throws Exception {
        Document document = new Document(PageSize.A4.rotate()); // Rotated to landscape for better fit
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Exam Date Collision Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // Spacer

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Styling for Table Headers
        com.itextpdf.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        String[] headers = {"Reg No", "Name", "Subject", "Date & Session", "FA Name", "FA Mobile"};

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (Student s : students) {
            table.addCell(s.getRegNo() != null ? s.getRegNo() : "-");
            table.addCell(s.getName() != null ? s.getName() : "-");

            if (s.getSubmissions() != null && !s.getSubmissions().isEmpty()) {
                String subjects = s.getSubmissions().stream()
                        .map(e -> e.getSubjectName() != null ? e.getSubjectName() : "-")
                        .collect(Collectors.joining(", "));
                table.addCell(subjects);

                String dates = s.getSubmissions().stream()
                        .map(e -> {
                            String d = (e.getExamDate() != null) ? e.getExamDate().toString() : "-";
                            String sl = (e.getExamSlot() != null) ? e.getExamSlot() : "-";
                            return d + " (" + sl + ")";
                        })
                        .collect(Collectors.joining(", "));
                table.addCell(dates);

            } else {
                table.addCell("No Exams");
                table.addCell("-");
            }

            table.addCell(s.getFaName() != null ? s.getFaName() : "-");
            table.addCell(s.getFaMobile() != null ? s.getFaMobile() : "-");
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}