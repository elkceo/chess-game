function renderBoard(fen, playerSide) {

    const chessboard = document.getElementById('chessboard');
    chessboard.innerHTML = '';

    const pieceSymbols = {
        'r': '♜', 'n': '♞', 'b': '♝', 'q': '♛', 'k': '♚', 'p': '♟',
        'R': '♖', 'N': '♘', 'B': '♗', 'Q': '♕', 'K': '♔', 'P': '♙'
    };

    let isDarkSquare = false;

    const rows = fen.split(' ')[0].split('/');

    if (game.gameOver) {
    const gameOverContainer = document.getElementById('game-over');
       document.getElementById('game-over').classList.remove('hidden');
       const textContent = game.winner === 'DRAW' ? 'Draw!' : `${game.winner} wins!`;
       gameOverContainer.getElementsByTagName('h2')[0].textContent = textContent;
    }

    const turn = document.getElementById('turn');
    turn.getElementsByTagName('h4')[0].textContent = game.currentPlayer === game.playerSide ? 'Your turn' : 'Google\'s turn';

    rows.forEach((row, rowIndex) => {
            let file = 0;
            [...row].forEach(char => {
                if (isFinite(char)) {
                    for (let i = 0; i < parseInt(char); i++) {
                        const square = document.createElement('div');
                        square.className = isDarkSquare ? 'dark-square' : 'light-square';

                        const position = `${String.fromCharCode(97 + file)}${8 - rowIndex}`;
                        square.setAttribute('data-position', position);
                        file++;

                        chessboard.appendChild(square);
                        isDarkSquare = !isDarkSquare;
                    }
                } else {
                    const square = document.createElement('div');
                    square.className = isDarkSquare ? 'dark-square' : 'light-square';

                    const position = `${String.fromCharCode(97 + file)}${8 - rowIndex}`;
                    square.setAttribute('data-position', position);
                    file++;

                    const piece = document.createElement('div');
                    piece.classList.add('piece');
                    piece.textContent = pieceSymbols[char];
                    piece.setAttribute('data-piece', char);
                    piece.addEventListener('click', () => handlePieceClick(piece));
                    square.appendChild(piece);

                    chessboard.appendChild(square);
                    isDarkSquare = !isDarkSquare;
                }
            });
            isDarkSquare = !isDarkSquare;
    });
}
