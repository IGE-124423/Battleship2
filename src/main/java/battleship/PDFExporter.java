package battleship;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFExporter {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 15;
    private static final float HEADER_OFFSET = 750;
    public static final int FONT_SIZE = 16;
    private static final int BODY_FONT_SIZE = 12;

    public static void exportMoves(List<String> moves, String filename) throws IOException {
        File file = new File(filename);
        PDDocument document = loadOrCreateDocument(file);
        writeAllMoves(document, moves);
        saveAndClose(document, filename);
    }

    private static PDDocument loadOrCreateDocument(File file) throws IOException {
        if (file.exists()) {
            return PDDocument.load(file);
        }
        return new PDDocument();
    }

    private static void writeAllMoves(PDDocument document, List<String> moves) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            writeHeader(content);

            int movesPerPage = calculateMovesPerPage();
            int lineCount = 0;
            int i = 0;

            while (i < moves.size()) {
                if (isPageFull(lineCount, movesPerPage)) {
                    lineCount = 0;
                }
                content.showText((i + 1) + " - " + moves.get(i));
                content.newLineAtOffset(0, -LINE_HEIGHT);
                lineCount++;
                i++;
            }
            content.endText();
        }
    }

    private static boolean isPageFull(int currentLine, int maxLines) {
        return currentLine >= maxLines;
    }

    private static int calculateMovesPerPage() {
        return (int) ((HEADER_OFFSET - 2 * MARGIN) / LINE_HEIGHT) - 2;
    }

    private static void writeHeader(PDPageContentStream content) throws IOException {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE);
        content.newLineAtOffset(MARGIN, HEADER_OFFSET);

        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        content.showText("Partida jogada em: " + agora.format(formatter));
        content.newLineAtOffset(0, -LINE_HEIGHT * 2);

        content.setFont(PDType1Font.HELVETICA, BODY_FONT_SIZE);
    }

    private static void saveAndClose(PDDocument document, String filename) throws IOException {
        document.save(filename);
        document.close();
        System.out.println("PDF atualizado com sucesso em: " + new File(filename).getAbsolutePath());
    }
}