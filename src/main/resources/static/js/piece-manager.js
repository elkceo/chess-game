function handlePieceClick(piece) {
    const position = piece.parentElement.getAttribute("data-position");
    const pieceType = piece.getAttribute("data-piece");
    const isPlayerPiece = playerSide === "WHITE" ? pieceType === pieceType.toUpperCase() : pieceType === pieceType.toLowerCase();
    if (isPlayerPiece) {
        const selectedPiece = document.querySelector(".selected-piece");
        if (selectedPiece) {
            selectedPiece.classList.remove("selected-piece");
            hidePossibleMoves();
        }
        unselectOtherPieces();
        piece.classList.add("selected-piece");
        highlightPossibleMoves(game.id, pieceType, position);
    }
}

function unselectOtherPieces() {
    document.querySelectorAll(".piece").forEach(piece => {
        piece.classList.remove("selected-piece");
    });
}

function highlightPossibleMoves(gameId, piece, position) {
    toggleLoading(true);
    fetch(`/api/game/${gameId}/possibleMoves?position=${position}`)
        .then(response => response.json())
        .then(data => {
            data.forEach(move => {
                const square = document.querySelector(`[data-position="${move}"]`);
                square.classList.add("possible-move");
                square.addEventListener("click", () => handleMove(square));
            });
        })
        .finally(() => toggleLoading(false));
}

function hidePossibleMoves() {
    document.querySelectorAll(".possible-move").forEach(square => {
        square.classList.remove("possible-move");
        square.removeEventListener("click", handleMove);
    });
}

function toggleLoading(isLoading) {
    const loader = document.getElementById("loader");
    if (isLoading) {
        loader.classList.remove("hidden");
    } else {
        loader.classList.add("hidden");
    }
}

function handleMove(square) {
    const selectedPiece = document.querySelector(".selected-piece");
    const from = selectedPiece.parentElement.getAttribute("data-position");
    const to = square.getAttribute("data-position");
    const gameId = game.id;
    const isGeminiMove = false;
    toggleLoading(true);
    const data = { gameId, from, to, isGeminiMove };
    fetch(`/api/game/move`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Invalid move");
            }
        })
        .then(data => {
            game = data;
            renderBoard(game.currentFEN, game.playerSide);
        })
        .finally(() => {
            hidePossibleMoves();
            toggleLoading(false);
            if (!game.gameOver) getGeminiMove();
        });
}

function getGeminiMove() {
    const gameId = game.id;
    const isGeminiMove = true;
    const data = { gameId, isGeminiMove };
    toggleLoading(true);
    fetch(`/api/game/move`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                showErrorMessage("Error with Gemini service. Click to retry");
                throw new Error("Error with Gemini service");
            }
        })
        .then(data => {
            game = data;
            renderBoard(game.currentFEN, game.playerSide);
        })
        .finally(() => toggleLoading(false));
}

function showErrorMessage(message) {
    const errorMessage = document.getElementById("error");
    errorMessage.textContent = message;
    errorMessage.classList.remove("hidden");
    errorMessage.addEventListener("click", () => {
        getGeminiMove();
        errorMessage.classList.add("hidden");
        errorMessage.textContent = "";
    });
}



