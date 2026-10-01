/**
 * Interactive Typing Practice Arena
 * Handles real-time drill typing, live WPM, live accuracy, caret positioning,
 * error tracking, sound triggers, and post-drill evaluation.
 */
const PracticeEngine = (() => {
  let containerEl = null;
  let hiddenInput = null;
  let hudWpm = null;
  let hudAcc = null;
  let hudErrors = null;
  let hudTimer = null;
  let hudPacing = null;
  let modalEl = null;

  let currentDrill = null;
  let words = [];
  let currentWordIdx = 0;
  let currentCharIdx = 0;
  let typedHistory = []; // Array of typed strings per word

  let startTime = null;
  let timerInterval = null;
  let isFinished = false;
  let totalKeystrokes = 0;
  let totalErrors = 0;
  let drillMistakeChars = [];

  function init(config) {
    containerEl = document.getElementById(config.wordsContainerId);
    hiddenInput = document.getElementById(config.inputId);
    hudWpm = document.getElementById(config.hudWpmId);
    hudAcc = document.getElementById(config.hudAccId);
    hudErrors = document.getElementById(config.hudErrorsId);
    hudTimer = document.getElementById(config.hudTimerId);
    hudPacing = document.getElementById(config.hudPacingId);
    modalEl = document.getElementById(config.modalId);

    if (hiddenInput) {
      hiddenInput.addEventListener('input', handleInput);
      hiddenInput.addEventListener('keydown', handleKeyDown);
    }

    if (containerEl) {
      containerEl.addEventListener('click', focusTyping);
    }
  }

  function focusTyping() {
    if (hiddenInput && !isFinished) {
      hiddenInput.focus();
    }
  }

  function loadDrill(drill) {
    currentDrill = drill;
    words = drill.text.trim().split(/\s+/).filter(Boolean);
    currentWordIdx = 0;
    currentCharIdx = 0;
    typedHistory = words.map(() => '');
    startTime = null;
    isFinished = false;
    totalKeystrokes = 0;
    totalErrors = 0;
    drillMistakeChars = [];

    if (timerInterval) {
      clearInterval(timerInterval);
      timerInterval = null;
    }

    if (hiddenInput) {
      hiddenInput.value = '';
      hiddenInput.disabled = false;
    }

    if (modalEl) {
      modalEl.style.display = 'none';
    }

    renderDrillWords();
    updateHUD(0, 100, 0, 0);
    focusTyping();
  }

  function renderDrillWords() {
    if (!containerEl) return;

    let html = '';
    words.forEach((word, wIdx) => {
      const isCurrent = wIdx === currentWordIdx;
      const typed = typedHistory[wIdx] || '';
      
      let wordClasses = ['drill-word'];
      if (isCurrent) wordClasses.push('active');
      if (wIdx < currentWordIdx) wordClasses.push('completed');

      html += `<div class="${wordClasses.join(' ')}" data-word-idx="${wIdx}">`;

      // Render expected characters
      for (let cIdx = 0; cIdx < word.length; cIdx++) {
        const expectedChar = word[cIdx];
        const typedChar = typed[cIdx];

        let charClass = 'char-pending';
        let isCaretHere = (isCurrent && cIdx === typed.length);

        if (typedChar !== undefined) {
          if (typedChar === expectedChar) {
            charClass = 'char-correct';
          } else {
            charClass = 'char-incorrect';
          }
        }

        html += `<span class="char ${charClass} ${isCaretHere ? 'caret' : ''}">${escapeHtml(expectedChar)}</span>`;
      }

      // Render any overflow characters typed beyond word length
      if (typed.length > word.length) {
        for (let oIdx = word.length; oIdx < typed.length; oIdx++) {
          const isCaretHere = (isCurrent && oIdx === typed.length - 1);
          html += `<span class="char char-overflow ${isCaretHere ? 'caret' : ''}">${escapeHtml(typed[oIdx])}</span>`;
        }
      }

      html += '</div>';
    });

    containerEl.innerHTML = html;

    // Scroll active word into comfortable view if needed
    const activeWordEl = containerEl.querySelector('.drill-word.active');
    if (activeWordEl) {
      activeWordEl.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
    }
  }

  function handleKeyDown(e) {
    if (isFinished) return;

    // Handle space to advance word
    if (e.key === ' ' || e.code === 'Space') {
      e.preventDefault();
      const currentTyped = typedHistory[currentWordIdx] || '';
      if (currentTyped.length > 0) {
        advanceWord();
      }
      return;
    }

    // Handle Backspace across words
    if (e.key === 'Backspace') {
      const currentTyped = typedHistory[currentWordIdx] || '';
      if (e.ctrlKey || e.metaKey) {
        // Ctrl+Backspace: clear current word
        e.preventDefault();
        typedHistory[currentWordIdx] = '';
        renderDrillWords();
        playClick();
        return;
      }

      if (currentTyped.length === 0 && currentWordIdx > 0) {
        // Step back to previous word if backspacing on empty word
        e.preventDefault();
        currentWordIdx--;
        renderDrillWords();
        playClick();
        return;
      }
    }
  }

  function handleInput(e) {
    if (isFinished) return;

    // Start timer on first keystroke
    if (!startTime) {
      startTime = Date.now();
      timerInterval = setInterval(updateLiveMetrics, 100);
    }

    const inputVal = hiddenInput.value;
    hiddenInput.value = ''; // Reset input buffer

    if (!inputVal) return;

    const char = inputVal[inputVal.length - 1];

    if (char === ' ') {
      // Space was captured by input
      const currentTyped = typedHistory[currentWordIdx] || '';
      if (currentTyped.length > 0) {
        advanceWord();
      }
      return;
    }

    // Record character typed
    totalKeystrokes++;
    const targetWord = words[currentWordIdx] || '';
    const currentTyped = typedHistory[currentWordIdx] || '';
    const expectedChar = targetWord[currentTyped.length];

    if (char !== expectedChar) {
      totalErrors++;
      if (expectedChar) {
        drillMistakeChars.push(expectedChar.toLowerCase());
      }
      playErrorSound();
    } else {
      playClick();
    }

    typedHistory[currentWordIdx] = currentTyped + char;

    // Check if this was the last character of the last word
    if (currentWordIdx === words.length - 1 && typedHistory[currentWordIdx].length >= targetWord.length) {
      renderDrillWords();
      finishDrill();
      return;
    }

    renderDrillWords();
    updateLiveMetrics();
  }

  function advanceWord() {
    const targetWord = words[currentWordIdx] || '';
    const currentTyped = typedHistory[currentWordIdx] || '';

    // Check for missed remaining characters in word
    if (currentTyped.length < targetWord.length) {
      for (let i = currentTyped.length; i < targetWord.length; i++) {
        totalErrors++;
        drillMistakeChars.push(targetWord[i].toLowerCase());
      }
    }

    currentWordIdx++;
    playClick();

    if (currentWordIdx >= words.length) {
      finishDrill();
    } else {
      renderDrillWords();
      updateLiveMetrics();
    }
  }

  function updateLiveMetrics() {
    if (!startTime) return;

    const elapsedSec = (Date.now() - startTime) / 1000;
    const elapsedMin = elapsedSec / 60;

    // Count correct characters
    let correctChars = 0;
    words.forEach((word, wIdx) => {
      const typed = typedHistory[wIdx] || '';
      for (let i = 0; i < Math.min(word.length, typed.length); i++) {
        if (word[i] === typed[i]) correctChars++;
      }
    });

    const wpm = elapsedMin > 0 ? Math.round((correctChars / 5) / elapsedMin) : 0;
    const accuracy = totalKeystrokes > 0 ? Math.round(((totalKeystrokes - totalErrors) / totalKeystrokes) * 1000) / 10 : 100;

    updateHUD(wpm, Math.max(0, accuracy), totalErrors, Math.floor(elapsedSec));
  }

  function updateHUD(wpm, accuracy, errors, elapsedSec) {
    if (hudWpm) hudWpm.textContent = wpm;
    if (hudAcc) hudAcc.textContent = `${accuracy}%`;
    if (hudErrors) hudErrors.textContent = errors;
    if (hudTimer) hudTimer.textContent = `${elapsedSec}s`;

    if (hudPacing) {
      const settings = (typeof Storage !== 'undefined') ? Storage.getSettings() : { targetAccuracy: 96 };
      if (accuracy >= settings.targetAccuracy) {
        hudPacing.textContent = 'Clean Cadence';
        hudPacing.className = 'hud-pacing-badge pacing-good';
      } else if (accuracy >= settings.targetAccuracy - 3) {
        hudPacing.textContent = 'Steady Tempo';
        hudPacing.className = 'hud-pacing-badge pacing-warn';
      } else {
        hudPacing.textContent = 'Rushing: Slow Down';
        hudPacing.className = 'hud-pacing-badge pacing-alert';
      }
    }
  }

  function finishDrill() {
    isFinished = true;
    if (timerInterval) clearInterval(timerInterval);

    const elapsedSec = Math.max(1, (Date.now() - startTime) / 1000);
    const elapsedMin = elapsedSec / 60;

    let correctChars = 0;
    words.forEach((word, wIdx) => {
      const typed = typedHistory[wIdx] || '';
      for (let i = 0; i < Math.min(word.length, typed.length); i++) {
        if (word[i] === typed[i]) correctChars++;
      }
    });

    const finalWpm = Math.round((correctChars / 5) / elapsedMin * 10) / 10;
    const finalAcc = totalKeystrokes > 0 ? Math.round(((totalKeystrokes - totalErrors) / totalKeystrokes) * 1000) / 10 : 100;

    if (typeof SoundManager !== 'undefined') {
      SoundManager.playSuccessChord();
    }

    showSummaryModal(finalWpm, finalAcc, elapsedSec, totalErrors, drillMistakeChars);
  }

  function showSummaryModal(wpm, acc, elapsedSec, errors, mistakeChars) {
    if (!modalEl) return;

    const settings = (typeof Storage !== 'undefined') ? Storage.getSettings() : { targetAccuracy: 96 };
    const meetsTarget = acc >= settings.targetAccuracy;

    const modalTitle = modalEl.querySelector('.modal-title');
    const modalWpm = modalEl.querySelector('.modal-stat-wpm');
    const modalAcc = modalEl.querySelector('.modal-stat-acc');
    const modalTime = modalEl.querySelector('.modal-stat-time');
    const modalFeedback = modalEl.querySelector('.modal-feedback');
    const modalMistakes = modalEl.querySelector('.modal-mistakes');
    const saveBtn = modalEl.querySelector('.btn-save-drill');

    if (modalTitle) modalTitle.textContent = meetsTarget ? '🎉 Great Drill Run!' : '⚡ Drill Completed';
    if (modalWpm) modalWpm.textContent = `${wpm} WPM`;
    if (modalAcc) modalAcc.textContent = `${acc}%`;
    if (modalTime) modalTime.textContent = `${Math.round(elapsedSec)}s`;

    if (modalFeedback) {
      if (meetsTarget) {
        modalFeedback.innerHTML = `
          <div class="feedback-box success">
            <strong>Target Met (${acc}% &ge; ${settings.targetAccuracy}%):</strong> 
            Clean rhythm maintained. Your muscle memory is locking in!
          </div>
        `;
      } else {
        modalFeedback.innerHTML = `
          <div class="feedback-box warning">
            <strong>Accuracy First (${acc}% < ${settings.targetAccuracy}%):</strong> 
            Focus on continuous tempo without rushing words. Slow down slightly on the next run.
          </div>
        `;
      }
    }

    if (modalMistakes) {
      const uniqueMistakes = [...new Set(mistakeChars)];
      if (uniqueMistakes.length > 0) {
        modalMistakes.innerHTML = `
          <div class="mistake-tags">
            Missed keys: ${uniqueMistakes.map(k => `<span class="tag">${k.toUpperCase()}</span>`).join(' ')}
          </div>
        `;
      } else {
        modalMistakes.innerHTML = `<div class="tag-clean">✨ 100% Error-Free Keystrokes!</div>`;
      }
    }

    // Attach save action
    if (saveBtn) {
      saveBtn.onclick = () => {
        if (typeof Storage !== 'undefined') {
          Storage.addResult({
            wpm,
            accuracy: acc,
            mode: 'drill practice',
            missedWords: [],
            missedChars: mistakeChars,
            notes: `Drill: ${currentDrill ? currentDrill.title : 'Custom'}`,
            source: 'drill'
          });
          saveBtn.textContent = '✓ Saved to History!';
          saveBtn.disabled = true;

          // Dispatch event to refresh dashboard
          window.dispatchEvent(new CustomEvent('resultsUpdated'));
        }
      };
      saveBtn.textContent = 'Save Result to History';
      saveBtn.disabled = false;
    }

    modalEl.style.display = 'flex';
  }

  function playClick() {
    if (typeof SoundManager !== 'undefined' && typeof Storage !== 'undefined') {
      const s = Storage.getSettings();
      if (s.soundEnabled) {
        SoundManager.playKeyClick(s.soundType || 'thock', s.soundVolume || 0.15);
      }
    }
  }

  function playErrorSound() {
    if (typeof SoundManager !== 'undefined' && typeof Storage !== 'undefined') {
      const s = Storage.getSettings();
      if (s.soundEnabled) {
        SoundManager.playErrorBeep(s.soundVolume || 0.15);
      }
    }
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  return {
    init,
    loadDrill,
    focusTyping
  };
})();

if (typeof module !== 'undefined' && module.exports) {
  module.exports = PracticeEngine;
}
