package com.servio.app.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.servio.app.dto.ReportSalesDTO;
import com.servio.app.service.PDFReportService;
import com.servio.app.service.ReportService;
import com.servio.app.service.SaleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/report/sales")
@RequiredArgsConstructor
public class PdfController {
	
	private final ReportService reportService;
	private final PDFReportService pdfReportService;

	@GetMapping("/day/pdf")
	public ResponseEntity<byte[]> generateDayPdf(
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate date
	) throws IOException {

		ReportSalesDTO report = reportService.generateReport(date);

	    byte[] pdf = pdfReportService.generateDayPdf(report);

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdf);
	}
	
}
