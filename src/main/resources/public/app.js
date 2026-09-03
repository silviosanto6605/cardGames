const ws = new WebSocket(`ws://${window.location.host}/game`);

const statusBar = document.getElementById("status-bar");
const myScoreEl = document.getElementById("my-score");
const oppScoreEl = document.getElementById("opp-score");
const deckCountEl = document.getElementById("deck-count");
const briscolaEl = document.getElementById("briscola-card");
const tableCard1El = document.getElementById("table-card-1");
const tableCard2El = document.getElementById("table-card-2");
const handContainer = document.getElementById("hand-container");
const myTakenPile = document.getElementById("my-taken-pile");
const oppTakenPile = document.getElementById("opp-taken-pile");
const sideDeckEl = document.getElementById("side-deck");

let isMyTurn = false;
let currentHand = [];
let currentTable = [null, null]; // Traccia le carte visibili sul tavolo


ws.onopen = () => {
    statusBar.textContent = "Connesso! In attesa del secondo giocatore...";
};

ws.onmessage = (event) => {
    const data = JSON.parse(event.data);

    if (data.type === "DISCONNECT") {
        statusBar.textContent = data.statusMessage;
        disableHand();
        return;
    }

    if (data.type === "ROUND_OVER") {
        handleRoundOver(data);
        return;
    }

    updateUI(data);
};

ws.onclose = () => {
    statusBar.textContent = "Connessione interrotta con il server.";
    disableHand();
};

function disableHand() {
    isMyTurn = false;
    handContainer.querySelectorAll(".hand-card").forEach(c => c.classList.add("disabled"));
}

const rankWords = {
    1: "Asso", 2: "Due", 3: "Tre", 4: "Quattro", 5: "Cinque",
    6: "Sei", 7: "Sette", 8: "Otto", 9: "Nove", 10: "Dieci"
};

function getCardImagePath(card) {
    if (!card) return "";
    const rankWord = rankWords[card.rank];
    const suitName = card.suit.toLowerCase();

    return `/cards/napol/${rankWord}_di_${suitName}.jpg`;
}

function renderSlot(slotEl, card, animClass) {
    slotEl.innerHTML = "";
    slotEl.className = "card";

    if (!card) {
        slotEl.classList.add("empty");
        slotEl.textContent = "-";
        return;
    }

    const img = document.createElement("img");
    img.src = getCardImagePath(card);
    img.alt = `${card.rank} di ${card.suit}`;
    img.className = "card-img";

    if (animClass) {
        slotEl.classList.add(animClass);
    }
    slotEl.appendChild(img);
}

function areCardsEqual(c1, c2) {
    if (!c1 || !c2) return c1 === c2;
    return c1.rank === c2.rank && c1.suit === c2.suit;
}

function triggerDeckDrawAnimation() {
    if (parseInt(deckCountEl.textContent, 10) <= 0) return;

    const flyCardMe = document.createElement("div");
    flyCardMe.className = "card back drawing-card-hand";
    flyCardMe.innerHTML = `<img src="/cards/napol/retro.jpg" class="card-img">`;

    const flyCardOpp = document.createElement("div");
    flyCardOpp.className = "card back drawing-card-opp";
    flyCardOpp.innerHTML = `<img src="/cards/napol/retro.jpg" class="card-img">`;

    sideDeckEl.appendChild(flyCardMe);
    sideDeckEl.appendChild(flyCardOpp);

    setTimeout(() => {
        flyCardMe.remove();
        flyCardOpp.remove();
    }, 600);
}

function handleRoundOver(state) {
    // Se lo slot 2 non è ancora renderizzato nel nostro schermo (ha appena giocato l'avversario)
    if (!areCardsEqual(currentTable[1], state.secondCard)) {
        renderSlot(tableCard2El, state.secondCard, "card-play-from-opp");
        currentTable[1] = state.secondCard;
    }

    statusBar.textContent = state.lastRoundWonByMe ? "Hai preso tu!" : "Ha preso l'avversario.";
    statusBar.style.color = state.lastRoundWonByMe ? "#4caf50" : "#ff5722";

    // 1. Pausa per completare l'animazione di discesa e visualizzare entrambe le carte
    setTimeout(() => {
        const flyClass = state.lastRoundWonByMe ? "fly-to-me" : "fly-to-opp";
        tableCard1El.classList.add(flyClass);
        tableCard2El.classList.add(flyClass);

        // 2. Volo verso il mazzetto delle prese
        setTimeout(() => {
            if (state.lastRoundWonByMe) {
                myTakenPile.classList.remove("hidden");
            } else {
                oppTakenPile.classList.remove("hidden");
            }

            renderSlot(tableCard1El, null, null);
            renderSlot(tableCard2El, null, null);
            tableCard1El.classList.remove(flyClass);
            tableCard2El.classList.remove(flyClass);
            currentTable = [null, null];

            statusBar.style.color = "#ffeb3b";
            myScoreEl.textContent = state.myScore;
            oppScoreEl.textContent = state.opponentScore;
            deckCountEl.textContent = state.deckSize;

            // 3. Pesca dal tallone
            triggerDeckDrawAnimation();

            // 4. Mostra la mano aggiornata dopo la pesca
            renderHand(state.myHand, state.isMyTurn);
        }, 600);
    }, 1400);
}

function renderHand(hand, isTurn) {
    currentHand = [...hand];
    handContainer.innerHTML = "";

    currentHand.forEach((card, index) => {
        const cardDiv = document.createElement("div");
        cardDiv.className = `card hand-card ${!isTurn ? "disabled" : ""}`;
        cardDiv.innerHTML = `<img src="${getCardImagePath(card)}" class="card-img">`;

        if (isTurn) {
            cardDiv.onclick = () => playCard(index);
        }
        handContainer.appendChild(cardDiv);
    });
}

function updateUI(state) {
    isMyTurn = state.isMyTurn;
    statusBar.textContent = state.statusMessage || (isMyTurn ? "Tocca a te!" : "Turno dell'avversario");

    myScoreEl.textContent = state.myScore;
    oppScoreEl.textContent = state.opponentScore;
    deckCountEl.textContent = state.deckSize;

    if (state.groundBriscola) {
        briscolaEl.innerHTML = `<img src="${getCardImagePath(state.groundBriscola)}" class="card-img">`;
    }

    const deckPileEl = document.getElementById("deck-pile");
    briscolaEl.style.visibility = state.deckSize === 0 ? "hidden" : "visible";
    deckPileEl.style.visibility = state.deckSize <= 1 ? "hidden" : "visible";

    // Se l'avversario ha giocato la prima carta sul tavolo vuoto
    if (state.firstCard && !areCardsEqual(currentTable[0], state.firstCard)) {
        renderSlot(tableCard1El, state.firstCard, "card-play-from-opp");
        currentTable[0] = state.firstCard;
    }

    renderHand(state.myHand, state.isMyTurn);
}

function playCard(index) {
    if (!isMyTurn || index < 0 || index >= currentHand.length) return;

    const playedCard = currentHand[index];

    // Rimuovi visivamente la carta dalla mano all'istante
    currentHand.splice(index, 1);
    renderHand(currentHand, false);

    // Falla atterrare immediatamente con l'animazione di salita dal basso
    if (!currentTable[0]) {
        renderSlot(tableCard1El, playedCard, "card-play-from-me");
        currentTable[0] = playedCard;
    } else {
        renderSlot(tableCard2El, playedCard, "card-play-from-me");
        currentTable[1] = playedCard;
    }

    ws.send(JSON.stringify({ action: "PLAY", cardIndex: index }));
    isMyTurn = false;
    statusBar.textContent = "Carta giocata, attesa esito...";
    disableHand();
}