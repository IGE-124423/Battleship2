package battleship;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * A interface gráfica (GUI) para visualizar o tabuleiro do jogo Battleship.
 */
public class GameGUI extends JFrame {
    private static final int CELL_SIZE = 50;

    // Imagens carregadas (podem ser null se não existirem na pasta resources)
    private Image imgWater;
    private Image imgShip;
    private Image imgHit;
    private Image imgMiss;
    private Image imgAdjacent;

    public GameGUI(Game game) {
        super("Battleship - Visualização Gráfica");
        
        // Carregar imagens da pasta src/main/resources (se existirem)
        imgWater = loadImage("/water.png");
        imgShip = loadImage("/ship.png");
        imgHit = loadImage("/hit.png");
        imgMiss = loadImage("/miss.png");
        imgAdjacent = loadImage("/adjacent.png");

        // Configurar o visual moderno usando o FlatLaf Dark Mode
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Falha ao carregar o tema visual FlatLaf");
        }
        
        // Queremos apenas fechar a janela, sem terminar a aplicação de consola
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(Game.BOARD_SIZE, Game.BOARD_SIZE, 2, 2));
        boardPanel.setBackground(Color.DARK_GRAY);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Obter os dados da frota e do jogo
        IFleet fleet = game.getMyFleet();
        List<IMove> moves = game.getAlienMoves();
        
        // Construir a representação do mapa
        char[][] map = new char[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                map[r][c] = '.';
            }
        }

        if (fleet != null) {
            for (IShip ship : fleet.getShips()) {
                for (IPosition ship_pos : ship.getPositions()) {
                    map[ship_pos.getRow()][ship_pos.getColumn()] = '#';
                }
                if (!ship.stillFloating()) {
                    for (IPosition adjacent_pos : ship.getAdjacentPositions()) {
                        if (adjacent_pos.isInside()) {
                            map[adjacent_pos.getRow()][adjacent_pos.getColumn()] = '-';
                        }
                    }
                }
            }
        }

        if (moves != null) {
            for (IMove move : moves) {
                for (IPosition shot : move.getShots()) {
                    if (shot.isInside()) {
                        int row = shot.getRow();
                        int col = shot.getColumn();
                        if (map[row][col] == '#') {
                            map[row][col] = '*'; // Tiro Certeiro
                        } else if (map[row][col] == '.' || map[row][col] == '-') {
                            map[row][col] = 'o'; // Tiro na Água
                        }
                    }
                }
            }
        }

        // Desenhar a grelha usando um Painel Customizado para suportar imagens
        for (int r = 0; r < Game.BOARD_SIZE; r++) {
            for (int c = 0; c < Game.BOARD_SIZE; c++) {
                char marker = map[r][c];
                boardPanel.add(new CellPanel(marker));
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        
        // Painel da Legenda
        JPanel legendPanel = new JPanel();
        legendPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        legendPanel.add(new JLabel("⬛ Navio  |"));
        legendPanel.add(new JLabel(" 🟥 Tiro Certeiro  |"));
        legendPanel.add(new JLabel(" 🟦 Água Intacta  |"));
        legendPanel.add(new JLabel(" 🩵 Tiro na Água"));
        add(legendPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Centrar no ecrã
    }

    /**
     * Tenta carregar uma imagem do classpath (pasta resources).
     */
    private Image loadImage(String path) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl).getImage();
        }
        return null;
    }

    /**
     * Componente visual para cada célula da grelha.
     * Desenha a cor de fundo apropriada e, se a imagem existir, desenha-a por cima.
     */
    private class CellPanel extends JPanel {
        private char marker;

        public CellPanel(char marker) {
            this.marker = marker;
            setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
            
            // Definir cor de fundo de segurança (fallback) se a imagem não existir
            switch (marker) {
                case '#': setBackground(new Color(120, 120, 120)); break; // Navio
                case '*': setBackground(new Color(220, 50, 50)); break;   // Tiro no Navio
                case 'o': setBackground(new Color(150, 200, 220)); break; // Tiro na Água
                case '-': setBackground(new Color(40, 60, 100)); break;   // Área Adjacente
                case '.': 
                default: setBackground(new Color(50, 120, 200)); break;   // Água Intacta
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Desenha a cor de fundo
            
            Image imgToDraw = null;
            switch (marker) {
                case '#': imgToDraw = imgShip; break;
                case '*': imgToDraw = imgHit; break;
                case 'o': imgToDraw = imgMiss; break;
                case '-': imgToDraw = imgAdjacent; break;
                case '.': imgToDraw = imgWater; break;
            }

            // Se conseguimos carregar a imagem do disco, desenha-a a preencher todo o quadrado!
            if (imgToDraw != null) {
                g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}