package com.licenta.bustravel.service.reports;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AdminReports {
    private Path foundFile;
    private final Logger LOGGER = LoggerFactory.getLogger(AdminReports.class);

    public void createReports() throws IOException, DocumentException {
        Document document = new Document();
        LOGGER.info("Creating report...");
        String filePath = "resources/reports/monthlyReport.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        LOGGER.info("Report created successfully.");
        document.open();
        LOGGER.info("Adding content to report...");
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Hello World", font);

        document.add(chunk);
        LOGGER.info("Content added to report.");
        document.close();
    }

    public UrlResource getFileAsResource(String fileCode) throws IOException {
        Path dirPath = Paths.get("Files-Upload");

        Files.list(dirPath).forEach(file -> {
            if (file.getFileName().toString().startsWith(fileCode)) {
                foundFile = file;
                return;
            }
        });

        if (foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }

        return null;
    }
}