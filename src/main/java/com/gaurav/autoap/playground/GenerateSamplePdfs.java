package com.gaurav.autoap.playground;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GenerateSamplePdfs {

    public static void main(String[] args) throws IOException {
        List<String> files = List.of("invoice1", "invoice2", "invoice3", "invoice4", "invoice5");

        for (String name : files) {
            String text = Files.readString(Path.of("src/main/resources/sample-invoices/" + name + ".txt"));
            createPdf(text, "src/main/resources/sample-invoices/" + name + ".pdf");
            System.out.println("Created " + name + ".pdf");
        }
    }

    private static void createPdf(String text, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 750);

            for (String line : text.split("\r?\n")) {
                contentStream.showText(line);
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.close();

            document.save(outputPath);
        }
    }
}