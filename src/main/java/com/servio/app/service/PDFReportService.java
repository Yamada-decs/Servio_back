package com.servio.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.servio.app.dto.ReportSalesDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PDFReportService {

    private final TemplateEngine templateEngine;

    public byte[] generateDayPdf(ReportSalesDTO report) throws IOException {

        Context context = new Context();
        context.setVariable("report", report);

        String html = templateEngine.process("report-day-sales", context);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }
}
