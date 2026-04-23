package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PDFExporterTest {

    @Test
    @DisplayName("exportMoves deve criar um PDF novo quando o ficheiro não existe")
    void exportMovesShouldCreateNewPdf(@TempDir Path tempDir) throws Exception {
        Path pdfPath = tempDir.resolve("moves.pdf");

        assertFalse(Files.exists(pdfPath), "Error: o ficheiro ainda não devia existir.");

        PDFExporter.exportMoves(List.of("A1", "B2"), pdfPath.toString());

        assertTrue(Files.exists(pdfPath), "Error: o PDF devia ter sido criado.");

        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            assertEquals(1, document.getNumberOfPages(),
                    "Error: um PDF novo com poucas jogadas devia ter 1 página.");

            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("Partida jogada em:"),
                    "Error: o cabeçalho com data/hora devia existir.");
            assertTrue(text.contains("1 - A1"),
                    "Error: a primeira jogada devia estar no PDF.");
            assertTrue(text.contains("2 - B2"),
                    "Error: a segunda jogada devia estar no PDF.");
        }
    }

    @Test
    @DisplayName("exportMoves deve adicionar uma nova página quando o ficheiro já existe")
    void exportMovesShouldAppendNewPageToExistingPdf(@TempDir Path tempDir) throws Exception {
        Path pdfPath = tempDir.resolve("moves.pdf");

        PDFExporter.exportMoves(List.of("A1"), pdfPath.toString());

        try (PDDocument before = PDDocument.load(pdfPath.toFile())) {
            assertEquals(1, before.getNumberOfPages(),
                    "Error: após a primeira exportação devia existir 1 página.");
        }

        PDFExporter.exportMoves(List.of("B2"), pdfPath.toString());

        try (PDDocument after = PDDocument.load(pdfPath.toFile())) {
            assertEquals(2, after.getNumberOfPages(),
                    "Error: a segunda exportação devia adicionar uma nova página.");

            String text = new PDFTextStripper().getText(after);
            assertTrue(text.contains("1 - A1"),
                    "Error: o conteúdo da primeira exportação devia continuar no PDF.");
            assertTrue(text.contains("1 - B2"),
                    "Error: o conteúdo da segunda exportação devia estar no PDF.");
        }
    }

    @Test
    @DisplayName("exportMoves deve criar um PDF válido mesmo com lista vazia")
    void exportMovesShouldHandleEmptyMoveList(@TempDir Path tempDir) throws Exception {
        Path pdfPath = tempDir.resolve("empty.pdf");

        PDFExporter.exportMoves(List.of(), pdfPath.toString());

        assertTrue(Files.exists(pdfPath), "Error: o PDF devia ser criado mesmo com lista vazia.");

        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            assertEquals(1, document.getNumberOfPages(),
                    "Error: um PDF com lista vazia devia ter 1 página.");
        }
    }
}