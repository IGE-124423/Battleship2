# RefactoringOpportunities

| local (classe ou classe::método, conforme aplicável) | nome do cheiro no código | nome da refabricação | número d@ alun@ |
|---|---|---|---|
| `Game::printBoard` (`src/main/java/battleship/Game.java`, linha 28) | Long Method / Overly Complex Method | Extract Method | 111111 |
| `Game::printBoard` (`src/main/java/battleship/Game.java`, linha 28) | Primitive Obsession / Type May Be Weakened | Use Interface Where Possible | 111111 |
| `Game::randomEnemyFire` (`src/main/java/battleship/Game.java`, linha 216) | Primitive Obsession / Insecure Random Number Generation | Change Type | 111111 |
| `Fleet::isInsideBoard` (`src/main/java/battleship/Fleet.java`, linha 199) | Complex Conditional / Overly Complex Boolean Expression | Decompose Conditional | 111111 |
| `Fleet::getShips` (`src/main/java/battleship/Fleet.java`, linha 73) | Encapsulation / Mutable Field Exposure | Encapsulate Field | 111111 |
| `Tasks::menu` (`src/main/java/battleship/Tasks.java`, linha 42) | Long Method / Overly Complex Method | Extract Method | 112967 |
| `Tasks::menu` (`src/main/java/battleship/Tasks.java`, linha 53) | Switch Statements | Replace Conditional with Polymorphism | 112967 |
| `Tasks::readClassicPosition` (`src/main/java/battleship/Tasks.java`, linhas 238 e 242) | Duplicate Code / Dynamic Regular Expression | Introduce Constant | 112967 |
| `Tasks::readClassicPosition` (`src/main/java/battleship/Tasks.java`, linha 222) | Complex Conditional / Multiple Return Points | Decompose Conditional | 112967 |
| `ScoreboardDatabase::printScoreboard` (`src/main/java/battleship/ScoreboardDatabase.java`, linha 62) | Dead Code / Boolean Variable Always Negated | Inline Variable | 112967 |
| `Move::processEnemyFire` (`src/main/java/battleship/Move.java`, linha 66) | Long Method / Overly Complex Method | Replace Method with Method Object | 122477 |
| `Move::processEnemyFire` (`src/main/java/battleship/Move.java`, linha 66) | Complex Conditional / Method With More Than Three Negations | Decompose Conditional | 122477 |
| `Move::getShots` (`src/main/java/battleship/Move.java`, linha 46) | Encapsulation / Mutable Field Exposure | Encapsulate Field | 122477 |
| `Move::Move` (`src/main/java/battleship/Move.java`, linhas 26-27) | Encapsulation / Assignment of Mutable Parameter | Introduce Defensive Copy | 122477 |
| `Position::Position` (`src/main/java/battleship/Position.java`, linha 62) | Readability / Parameter Hides Field | Rename | 122477 |
| `PDFExporter::exportMoves` (`src/main/java/battleship/PDFExporter.java`, linha 27) | Long Method / Overly Long Method | Extract Method | 124423 |
| `PDFExporter::exportMoves` (`src/main/java/battleship/PDFExporter.java`, linha 27) | Long Method / Overly Nested Method | Decompose Conditional | 124423 |
| `PDFExporter::exportMoves` (`src/main/java/battleship/PDFExporter.java`, linhas 45, 55 e 74) | Primitive Obsession / Magic Number | Introduce Constant | 124423 |
| `PDFExporter::exportMoves` (`src/main/java/battleship/PDFExporter.java`, linhas 83 e 88) | Control Flow / Break Statement | Replace Break with Guard Clause | 124423 |
| `Ship::positions` (`src/main/java/battleship/Ship.java`, linha 100) | Encapsulation / Protected Field | Change Visibility | 124423 |
