// Vanilla JS — no framework, no bundler. This is the whole client.
//
// On submit we open an EventSource (the browser's built-in SSE client) to
// /api/converse?message=... . The server pushes one JSON payload per OASP
// event; we render assistant text chunks into a single bubble AS THEY ARRIVE,
// which is the live token-by-token effect this sample is here to show.

const transcript = document.getElementById("transcript");
const statusLine = document.getElementById("status");
const form = document.getElementById("composer");
const input = document.getElementById("message");
const sendButton = document.getElementById("send");

// The assistant bubble currently being streamed into (null between turns).
let activeBubble = null;
// The open SSE connection for the current turn (null when idle).
let source = null;

form.addEventListener("submit", (event) => {
  event.preventDefault();
  const message = input.value.trim();
  if (!message || source) return; // ignore empty input or a turn in flight

  addBubble("user", message);
  input.value = "";
  startTurn(message);
});

// Open the SSE stream and wire up the handlers for one conversation turn.
function startTurn(message) {
  setBusy(true);
  setStatus("Connecting…");
  activeBubble = null;

  source = new EventSource("/api/converse?message=" + encodeURIComponent(message));

  // Default (unnamed) events carry our JSON event payloads.
  source.onmessage = (event) => handlePayload(JSON.parse(event.data));

  // The server sends a named "done" event when the turn is complete; close the
  // connection ourselves so the browser does not auto-reconnect and start over.
  source.addEventListener("done", endTurn);

  // A real network/server error (not a normal completion).
  source.onerror = () => {
    setStatus("Connection error.");
    endTurn();
  };
}

// Render one event payload. `kind` mirrors EventPayload.java on the server.
function handlePayload(payload) {
  switch (payload.kind) {
    case "message-start":
      activeBubble = addBubble("assistant", "");
      setStatus("Assistant is replying…");
      break;
    case "text": // an incremental chunk — append it to the live bubble
      if (!activeBubble) activeBubble = addBubble("assistant", "");
      activeBubble.textContent += payload.text;
      scrollToBottom();
      break;
    case "message-end":
      activeBubble = null;
      break;
    case "status":
      setStatus("Session status: " + payload.status);
      break;
    case "error":
      setStatus("Error: " + payload.text);
      break;
    default: // thinking, tool-use, unknown — surface the kind quietly
      setStatus("Event: " + payload.kind);
  }
}

// Close the stream and re-enable the composer.
function endTurn() {
  if (source) {
    source.close();
    source = null;
  }
  activeBubble = null;
  setBusy(false);
}

// --- small DOM helpers ---------------------------------------------------

function addBubble(role, text) {
  const bubble = document.createElement("div");
  bubble.className = "bubble bubble--" + role;
  bubble.textContent = text;
  transcript.appendChild(bubble);
  scrollToBottom();
  return bubble;
}

function setStatus(text) {
  statusLine.textContent = text;
}

function setBusy(busy) {
  sendButton.disabled = busy;
  input.disabled = busy;
  if (!busy) input.focus();
}

function scrollToBottom() {
  transcript.scrollTop = transcript.scrollHeight;
}
