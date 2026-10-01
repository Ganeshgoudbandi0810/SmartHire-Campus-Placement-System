# 🎯 Monkeytype Typing Practice Coach

A lightweight, local, user-controlled typing coach designed to help you methodically improve your speed and consistency on Monkeytype.

The coach is built around the fundamental typing principle: **Accuracy first, then speed**. When accuracy is high and stable, speed follows naturally as muscle memory consolidates. When accuracy dips, rushing causes hesitation and erratic pacing.

---

## 🚀 How to Run Locally

### Option 1: Using Node.js (Recommended)
This runs the lightweight, zero-dependency local static server:

1. Open PowerShell or a terminal in the `typing-coach` folder:
   ```powershell
   cd c:\Users\ganes\Desktop\SmartHire\typing-coach
   ```
2. Start the local server:
   ```powershell
   node server.js
   ```
   *(or run `npm.cmd start`)*

3. Open your browser to:
   ```
   http://localhost:3000
   ```

> 📱 **Mobile Access**: The server automatically displays your local network IP (e.g. `http://192.168.1.X:3000`) so you can open it on your phone or tablet connected to the same Wi-Fi network.

### Option 2: Direct File Opening (Zero-Install)
Because the app is built entirely with modern native Web APIs and ES modules without build-step requirements, you can also directly double-click or open:
```
c:\Users\ganes\Desktop\SmartHire\typing-coach\public\index.html
```
in any modern web browser (Google Chrome, Microsoft Edge, Firefox, Brave, Safari).

---

## ✨ Features & Capabilities

### 1. 📊 Enter & Parse Monkeytype Results
- **Smart Paste Parser**: Directly paste Monkeytype summary strings (e.g. `78 wpm 97% acc time 60 missed: receive, calendar, q`) and click **"Auto-Extract & Populate"** to automatically parse WPM, Accuracy, Test Mode, and Missed words.
- **Manual Form**: Easily record WPM, Accuracy, Mode (Time 15s/60s, Words 25/50, Quotes), missed characters, and session notes.

### 2. 📈 Local Storage & Interactive Progress Charts
- All test history and settings are stored **100% locally in your browser** (`localStorage`).
- **Smooth Retina Canvas Chart**:
  - WPM progression line with data points.
  - 3-Test Moving Average curve to highlight your true baseline trajectory.
  - Accuracy % trend line with your **Target Accuracy guideline**.
  - Interactive hover tooltips showing date, WPM, accuracy, mode, and missed tokens.
  - Mode filters (All, 60s, 15s, 50 words, 25 words).

### 3. 🧠 Mistake Pattern Analysis & Keyboard Heatmap
- **Visual QWERTY Heatmap**: Dynamically colors keys according to error frequency (Neutral &rarr; Yellow &rarr; Orange &rarr; Crimson).
- **Click-to-Train**: Click *any* key on the heatmap to immediately launch a specialized single-key isolation drill.
- **N-Gram Pair Analysis**: Detects bigrams (letter pairs like `th`, `qu`, `er`, `ce`) that trigger finger clashes or hesitation.
- **Problem Words Ranking**: Aggregates the words that tripped you up most frequently in past sessions.

### 4. 🎯 Accuracy-First Coaching Engine
- Set your **Target Accuracy** (recommended: 96% – 98%).
- **Dynamic Pacing Directives**:
  - **Accuracy Repair Mode** (*Accuracy < Target - 2.5%*): Detects when you are out-typing your precision. Instructs you to intentionally throttle back speed by 10–15 WPM on Monkeytype to re-establish clean muscle memory.
  - **Precision Stabilization** (*Within 2% of Target*): Pinpoints hesitations on problem keys and instructs you to keep eyes scanning 1–2 words ahead.
  - **Speed Unlock / Green Light** (*Accuracy &ge; Target for 3+ tests*): Confirms your foundation is rock solid and gives you the green light to accelerate your finger tempo by 3–5 WPM on familiar words.

### 5. ⚡ Interactive Practice Arena
- Type generated drills with immediate feedback:
  - Character-level color states (Pending, Correct, Error with wavy underline).
  - Smooth caret navigation and auto-scrolling.
  - **Live HUD**: Real-time WPM, Real-time Accuracy %, Error Counter, and Elapsed Time.
  - **Live Pacing Indicator**: Informs you in real-time whether your current drill run is in the "Clean Cadence" zone or if you are rushing.
  - **Synthesized Mechanical Switch Audio**: Realistic switch acoustics (*Thock*, *Clack*, *Crisp Click*) synthesized purely in real-time via the native Web Audio API (0 audio downloads needed).
  - Post-drill summary with 1-click option to log the drill run to your training history.
  - Quick shortcuts: `Esc` to restart drill, `Space` to advance word, `Ctrl + Backspace` to wipe word.

### 6. ⚙️ Privacy & Data Management
- No accounts, no cookies, no tracking.
- **Backup & Restore**: Export your entire training history as a `.json` backup file or restore from a previous file anytime.
- **Sample Data**: Includes a "Load Sample Data" button to explore charts, heatmaps, and coach recommendations immediately.

---

## 🔒 Practice & Ethics Guidelines
- **User-Controlled Practice**: This tool is an offline diagnostic and training companion.
- **No Automation**: It does not automate keystrokes on Monkeytype, submit scores, or alter leaderboards.
- **Realistic Improvement**: It does not make unrealistic promises. Improvement in typing requires disciplined consistency, patience, and deliberate focus on accuracy over ego speed.
