const ws = new WebSocket(`ws://${window.location.host}/game`);

const statusBar = document.getElementById("status-bar");
const myScoreEl = document.getElementById("my-score");
const oppScoreEl = document.getElementById("opp-score");
const deckCountEl = document.getElementById("deck-count");
const briscolaEl = document.getElementById("briscola-card");
const tableCardEl = document.getElementById("table-card");
const handContainer = document.getElementById("hand-container");

let isMyTurn = false;

ws.onopen = () => {
    statusBar.textContent = "Connesso! In attesa del secondo giocatore...";
};

ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    updateUI(data);
};

ws.onclose = () => {
    statusBar.textContent = "Connessione chiusa.";
};

function formatCard(card) {
    if (!card) return "-";
    const rankNames = { 1: "Asso", 8: "Fante", 9: "Cavallo", 10: "Re" };
    const name = rankNames[card.rank] || card.rank;
    return `${name}<br><small class="suit-${card.suit}">${card.suit}</small>`;
}

function updateUI(state) {
    isMyTurn = state.isMyTurn;
    statusBar.textContent = state.statusMessage || (isMyTurn ? "Tocca a te!" : "Turno dell'avversario");

    myScoreEl.textContent = state.myScore;
    oppScoreEl.textContent = state.opponentScore;
    deckCountEl.textContent = state.deckSize;

    const deckPileEl = document.getElementById("deck-pile");

    if (state.groundBriscola) {
        briscolaEl.innerHTML = formatCard(state.groundBriscola);
    }

    if (state.deckSize === 0) {
        briscolaEl.style.visibility = "hidden";
        deckPileEl.style.visibility = "hidden";
    } else if (state.deckSize === 1) {
        briscolaEl.style.visibility = "visible";
        deckPileEl.style.visibility = "hidden";
    } else {
        briscolaEl.style.visibility = "visible";
        deckPileEl.style.visibility = "visible";
    }

    if (state.tableCard) {
        tableCardEl.classList.remove("empty");
        tableCardEl.innerHTML = formatCard(state.tableCard);
    } else {
        tableCardEl.classList.add("empty");
        tableCardEl.innerHTML = "Tavolo vuoto";
    }

    handContainer.innerHTML = "";
    state.myHand.forEach((card, index) => {
        const cardDiv = document.createElement("div");
        cardDiv.className = `card hand-card ${!isMyTurn ? "disabled" : ""}`;
        cardDiv.innerHTML = formatCard(card);

        if (isMyTurn) {
            cardDiv.onclick = () => playCard(index);
        }
        handContainer.appendChild(cardDiv);
    });
}

function playCard(index) {
    if (!isMyTurn) return;
    ws.send(JSON.stringify({
        action: "PLAY",
        cardIndex: index
    }));
    isMyTurn = false;
    statusBar.textContent = "Carta giocata, attesa esito...";
}