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

    /**
     * Adiciona a lista de jogadas a um PDF existente ou cria um novo se não existir.
     *
     * @param moves    Lista de movimentos do jogo
     * @param filename Nome do ficheiro PDF a criar/adicionar
     * @throws IOException Caso haja erro na escrita do ficheiro
     */
    public static void exportMoves(List<String> moves, String filename) throws IOException {
        PDDocument document;
        File file = new File(filename);

        if (file.exists()) {
            document = PDDocument.load(file);
        } else {
            document = new PDDocument();
        }

        //Cria nova página para esta partida
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        float yPosition = HEADER_OFFSET;

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(MARGIN, yPosition);

            //Cabeçalho com data/hora da partida
            LocalDateTime agora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            content.showText("Partida jogada em: " + agora.format(formatter));
            content.newLineAtOffset(0, -LINE_HEIGHT * 2);

            //Jogadas
            content.setFont(PDType1Font.HELVETICA, 12);

            int movesPerPage = (int) ((yPosition - 2 * MARGIN) / LINE_HEIGHT) - 2; // espaço para cabeçalho
            int lineCount = 0;

            for (int i = 0; i < moves.size(); i++) {

                //Se atingir limite da página, criar nova página
                if (lineCount >= movesPerPage) {
                    content.endText();
                    content.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    lineCount = 0;

                    //Novo content stream
                    try (PDPageContentStream newContent = new PDPageContentStream(document, page)) {
                        newContent.beginText();
                        newContent.setFont(PDType1Font.HELVETICA, 12);
                        newContent.newLineAtOffset(MARGIN, HEADER_OFFSET);

                        //Continuar jogadas
                        for (int j = i; j < moves.size(); j++) {
                            newContent.showText((j + 1) + " - " + moves.get(j));
                            newContent.newLineAtOffset(0, -LINE_HEIGHT);
                            lineCount++;
                            if (lineCount >= movesPerPage) {
                                break; //nova página será criada no próximo loop
                            }
                        }
                        newContent.endText();
                    }
                    break; //todas as jogadas foram adicionadas nesta iteração
                }

                content.showText((i + 1) + " - " + moves.get(i));
                content.newLineAtOffset(0, -LINE_HEIGHT);
                lineCount++;
            }

            content.endText();
        }

        document.save(filename);
        document.close();

        System.out.println("PDF atualizado com sucesso em: " + file.getAbsolutePath());
    }
}