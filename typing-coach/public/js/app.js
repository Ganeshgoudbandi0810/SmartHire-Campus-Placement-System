/**
 * App Main Coordinator
 * Manages view switching, UI events, smart paste parsing,
 * dashboard metrics, and integration between components.
 */
document.addEventListener('DOMContentLoaded', () => {
  // DOM Elements - Navigation
  const navBtns = document.querySelectorAll('.nav-btn');
  const viewSections = document.querySelectorAll('.view-section');

  // DOM Elements - Dashboard Metrics
  const metricAvgWpm = document.getElementById('metric-avg-wpm');
  const metricPeakWpm = document.getElementById('metric-peak-wpm');
  const metricAvgAcc = document.getElementById('metric-avg-acc');
  const metricTargetStatus = document.getElementById('metric-target-status');
  const metricTestCount = document.getElementById('metric-test-count');

  // DOM Elements - Coach Advice Banner
  const coachBanner = document.getElementById('coach-banner');
  const coachBadge = document.getElementById('coach-badge');
  const coachHeadline = document.getElementById('coach-headline');
  const coachMessage = document.getElementById('coach-message');
  const coachActionBtn = document.getElementById('btn-coach-action');

  // DOM Elements - Charts
  const filterBtns = document.querySelectorAll('.filter-btn');

  // DOM Elements - Log Result Form
  const formLog = document.getElementById('form-log-result');
  const inputWpm = document.getElementById('log-wpm');
  const inputAcc = document.getElementById('log-accuracy');
  const selectMode = document.getElementById('log-mode');
  const inputMissedWords = document.getElementById('log-missed-words');
  const inputMissedChars = document.getElementById('log-missed-chars');
  const inputNotes = document.getElementById('log-notes');
  const pasteBox = document.getElementById('smart-paste-box');
  const btnParsePaste = document.getElementById('btn-parse-paste');
  const pasteFeedback = document.getElementById('paste-feedback');

  // DOM Elements - Recent History Table
  const tableRecentBody = document.getElementById('table-recent-body');

  // DOM Elements - Mistake Analysis
  const listTopBigrams = document.getElementById('list-top-bigrams');
  const listTopWords = document.getElementById('list-top-words');

  // DOM Elements - Practice Arena
  const drillTypeBtns = document.querySelectorAll('.drill-type-btn');
  const drillTitle = document.getElementById('drill-title');
  const drillExplanation = document.getElementById('drill-explanation');
  const btnRestartDrill = document.getElementById('btn-restart-drill');
  const btnNextDrill = document.getElementById('btn-next-drill');
  const modalDrillSummary = document.getElementById('modal-drill-summary');
  const btnCloseModal = document.getElementById('btn-close-modal');
  const btnModalNext = document.getElementById('btn-modal-next');
  const btnModalRetry = document.getElementById('btn-modal-retry');

  // DOM Elements - Settings
  const settingTargetAcc = document.getElementById('setting-target-acc');
  const targetAccValue = document.getElementById('target-acc-value');
  const settingSoundToggle = document.getElementById('setting-sound-toggle');
  const settingSoundType = document.getElementById('setting-sound-type');
  const settingSoundVolume = document.getElementById('setting-sound-volume');
  const btnTestSound = document.getElementById('btn-test-sound');
  const settingTheme = document.getElementById('setting-theme');
  const btnExportData = document.getElementById('btn-export-data');
  const inputImportFile = document.getElementById('input-import-file');
  const btnLoadSampleData = document.getElementById('btn-load-sample-data');
  const btnClearAllData = document.getElementById('btn-clear-all-data');

  let currentDrillConfig = { type: 'cadence' };

  // ==========================================
  // INITIALIZATION
  // ==========================================
  function initApp() {
    loadSettingsIntoUI();
    applyTheme(Storage.getSettings().theme);

    // Initialize Chart
    ChartRenderer.init('progressChart', 'chartTooltip');

    // Initialize Keyboard Heatmap
    KeyboardHeatmap.init('keyboardHeatmapContainer', (selectedKey) => {
      // Direct single key drill click
      switchTab('practice');
      loadSingleKeyDrill(selectedKey);
    });

    // Initialize Practice Engine
    PracticeEngine.init({
      wordsContainerId: 'practiceWordsContainer',
      inputId: 'practiceHiddenInput',
      hudWpmId: 'hudWpm',
      hudAccId: 'hudAcc',
      hudErrorsId: 'hudErrors',
      hudTimerId: 'hudTimer',
      hudPacingId: 'hudPacing',
      modalId: 'modal-drill-summary'
    });

    setupEventListeners();
    refreshAll();

    // Auto-generate starting drill in practice arena
    generateAndLoadDrill('auto');
  }

  function setupEventListeners() {
    // Navigation switching
    navBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        const tab = btn.getAttribute('data-tab');
        switchTab(tab);
      });
    });

    // Chart Filter buttons
    filterBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        filterBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        const filter = btn.getAttribute('data-filter');
        ChartRenderer.setFilter(filter);
      });
    });

    // Smart Paste Parser
    if (btnParsePaste) {
      btnParsePaste.addEventListener('click', handleSmartPaste);
    }

    // Form Log Result Submission
    if (formLog) {
      formLog.addEventListener('submit', handleLogSubmit);
    }

    // Coach Advice Action Button
    if (coachActionBtn) {
      coachActionBtn.addEventListener('click', () => {
        const results = Storage.getResults();
        const settings = Storage.getSettings();
        const feedback = Coach.getCoachingFeedback(results, settings.targetAccuracy);
        switchTab('practice');
        generateAndLoadDrill(feedback.drillType || 'cadence');
      });
    }

    // Drill selector buttons
    drillTypeBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        drillTypeBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        const type = btn.getAttribute('data-drill-type');
        generateAndLoadDrill(type);
      });
    });

    if (btnRestartDrill) {
      btnRestartDrill.addEventListener('click', () => {
        if (currentDrillConfig.activeDrill) {
          PracticeEngine.loadDrill(currentDrillConfig.activeDrill);
        } else {
          generateAndLoadDrill(currentDrillConfig.type);
        }
      });
    }

    if (btnNextDrill) {
      btnNextDrill.addEventListener('click', () => {
        generateAndLoadDrill(currentDrillConfig.type);
      });
    }

    // Modal controls
    if (btnCloseModal) {
      btnCloseModal.addEventListener('click', () => {
        modalDrillSummary.style.display = 'none';
      });
    }
    if (btnModalRetry) {
      btnModalRetry.addEventListener('click', () => {
        modalDrillSummary.style.display = 'none';
        if (currentDrillConfig.activeDrill) {
          PracticeEngine.loadDrill(currentDrillConfig.activeDrill);
        }
      });
    }
    if (btnModalNext) {
      btnModalNext.addEventListener('click', () => {
        modalDrillSummary.style.display = 'none';
        generateAndLoadDrill(currentDrillConfig.type);
      });
    }

    // Keyboard Shortcuts (Tab + Enter or Esc to restart drill)
    document.addEventListener('keydown', (e) => {
      const activeTab = document.querySelector('.nav-btn.active')?.getAttribute('data-tab');
      if (activeTab === 'practice') {
        if (e.key === 'Escape') {
          e.preventDefault();
          if (modalDrillSummary.style.display === 'flex') {
            modalDrillSummary.style.display = 'none';
          }
          if (currentDrillConfig.activeDrill) {
            PracticeEngine.loadDrill(currentDrillConfig.activeDrill);
          }
        }
      }
    });

    // Settings listeners
    if (settingTargetAcc) {
      settingTargetAcc.addEventListener('input', (e) => {
        const val = parseInt(e.target.value, 10);
        targetAccValue.textContent = `${val}%`;
        Storage.saveSettings({ targetAccuracy: val });
        refreshAll();
      });
    }

    if (settingSoundToggle) {
      settingSoundToggle.addEventListener('change', (e) => {
        Storage.saveSettings({ soundEnabled: e.target.checked });
      });
    }

    if (settingSoundType) {
      settingSoundType.addEventListener('change', (e) => {
        Storage.saveSettings({ soundType: e.target.value });
      });
    }

    if (settingSoundVolume) {
      settingSoundVolume.addEventListener('input', (e) => {
        Storage.saveSettings({ soundVolume: parseFloat(e.target.value) });
      });
    }

    if (btnTestSound) {
      btnTestSound.addEventListener('click', () => {
        const s = Storage.getSettings();
        SoundManager.playKeyClick(s.soundType, s.soundVolume);
      });
    }

    if (settingTheme) {
      settingTheme.addEventListener('change', (e) => {
        const theme = e.target.value;
        Storage.saveSettings({ theme });
        applyTheme(theme);
      });
    }

    // Data Export & Import
    if (btnExportData) {
      btnExportData.addEventListener('click', handleExport);
    }
    if (inputImportFile) {
      inputImportFile.addEventListener('change', handleImport);
    }
    if (btnLoadSampleData) {
      btnLoadSampleData.addEventListener('click', () => {
        if (confirm('Load sample test results to test charts, mistake analysis, and coach guidance?')) {
          Storage.loadSampleData();
          refreshAll();
          alert('Sample test data loaded successfully!');
        }
      });
    }
    if (btnClearAllData) {
      btnClearAllData.addEventListener('click', () => {
        if (confirm('Are you sure you want to clear all logged tests? This cannot be undone.')) {
          Storage.clearAllData();
          refreshAll();
          alert('All test history cleared.');
        }
      });
    }

    // Reactive event from drill saving
    window.addEventListener('resultsUpdated', () => {
      refreshAll();
    });
  }

  // ==========================================
  // VIEW SWITCHING
  // ==========================================
  function switchTab(tabName) {
    navBtns.forEach(btn => {
      btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
    });

    viewSections.forEach(section => {
      section.classList.toggle('active', section.id === `view-${tabName}`);
    });

    if (tabName === 'dashboard') {
      setTimeout(() => ChartRenderer.render(Storage.getResults()), 50);
    } else if (tabName === 'practice') {
      setTimeout(() => PracticeEngine.focusTyping(), 100);
    }
  }

  // ==========================================
  // DASHBOARD & ANALYTICS REFRESH
  // ==========================================
  function refreshAll() {
    const results = Storage.getResults();
    const settings = Storage.getSettings();

    // 1. Calculate Summary Stats
    updateSummaryMetrics(results, settings.targetAccuracy);

    // 2. Update Coach Advice Banner
    updateCoachBanner(results, settings.targetAccuracy);

    // 3. Render Progress Chart
    const activeFilter = document.querySelector('.filter-btn.active')?.getAttribute('data-filter') || 'all';
    ChartRenderer.render(results, activeFilter);

    // 4. Update Recent Results Table
    renderRecentTable(results);

    // 5. Analyze Mistakes & Update Keyboard Heatmap
    const mistakeData = Coach.analyzeMistakes(results);
    KeyboardHeatmap.render(mistakeData);
    renderMistakeRankings(mistakeData);
  }

  function updateSummaryMetrics(results, targetAcc) {
    if (!results || results.length === 0) {
      metricAvgWpm.textContent = '—';
      metricPeakWpm.textContent = '—';
      metricAvgAcc.textContent = '—';
      metricTargetStatus.textContent = `${targetAcc}% Target`;
      metricTestCount.textContent = '0 Tests';
      return;
    }

    const wpms = results.map(r => r.wpm);
    const accs = results.map(r => r.accuracy);

    const avgWpm = Math.round(wpms.reduce((a, b) => a + b, 0) / wpms.length * 10) / 10;
    const peakWpm = Math.round(Math.max(...wpms) * 10) / 10;
    const avgAcc = Math.round(accs.reduce((a, b) => a + b, 0) / accs.length * 10) / 10;

    metricAvgWpm.textContent = `${avgWpm}`;
    metricPeakWpm.textContent = `${peakWpm}`;
    metricAvgAcc.textContent = `${avgAcc}%`;
    metricTestCount.textContent = `${results.length} Tests`;

    if (avgAcc >= targetAcc) {
      metricTargetStatus.innerHTML = `<span class="text-success">&#10003; Target Met (${avgAcc}%)</span>`;
    } else {
      const diff = Math.round((targetAcc - avgAcc) * 10) / 10;
      metricTargetStatus.innerHTML = `<span class="text-warn">&#9650; Need +${diff}% to Target</span>`;
    }
  }

  function updateCoachBanner(results, targetAcc) {
    const feedback = Coach.getCoachingFeedback(results, targetAcc);

    coachBadge.textContent = feedback.badge;
    coachBadge.className = `coach-badge badge-${feedback.badgeColor}`;
    coachHeadline.textContent = feedback.headline;
    coachMessage.textContent = feedback.message;

    if (coachActionBtn) {
      coachActionBtn.textContent = `⚡ Train: ${feedback.recommendedAction}`;
    }
  }

  function renderRecentTable(results) {
    if (!tableRecentBody) return;

    if (!results || results.length === 0) {
      tableRecentBody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">No tests recorded yet. Log your first test above!</td></tr>`;
      return;
    }

    // Show recent 10 tests in reverse chronological order
    const recent = [...results].reverse().slice(0, 10);
    const settings = Storage.getSettings();

    tableRecentBody.innerHTML = recent.map(r => {
      const accColor = r.accuracy >= settings.targetAccuracy ? 'text-success' : 'text-danger';
      const missed = r.missedWords && r.missedWords.length > 0 
        ? r.missedWords.slice(0, 3).join(', ') + (r.missedWords.length > 3 ? '...' : '')
        : (r.missedChars && r.missedChars.length > 0 ? r.missedChars.join(', ') : '—');

      return `
        <tr>
          <td>${r.dateStr || new Date(r.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</td>
          <td class="font-bold text-accent">${r.wpm}</td>
          <td class="font-bold ${accColor}">${r.accuracy}%</td>
          <td><span class="mode-pill">${escapeHtml(r.mode)}</span></td>
          <td class="text-muted text-sm">${escapeHtml(missed)}</td>
          <td class="text-muted text-sm">${escapeHtml(r.notes || '—')}</td>
          <td>
            <button type="button" class="btn-delete-row" data-id="${r.id}" title="Delete test">&times;</button>
          </td>
        </tr>
      `;
    }).join('');

    // Attach row delete listeners
    tableRecentBody.querySelectorAll('.btn-delete-row').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = btn.getAttribute('data-id');
        if (confirm('Delete this test record?')) {
          Storage.deleteResult(id);
          refreshAll();
        }
      });
    });
  }

  function renderMistakeRankings(mistakeData) {
    // Render Top Bigrams
    if (listTopBigrams) {
      const { sortedNgrams = [] } = mistakeData;
      if (sortedNgrams.length === 0) {
        listTopBigrams.innerHTML = `<li class="text-muted">No character pair mistakes recorded yet.</li>`;
      } else {
        listTopBigrams.innerHTML = sortedNgrams.slice(0, 8).map(ng => `
          <li class="mistake-item">
            <div class="mistake-label">
              <span class="ngram-tag">${ng.ngram.toUpperCase()}</span>
              <span class="count-badge">${ng.count}x missed</span>
            </div>
            <button type="button" class="btn-train-mini" data-ngram="${ng.ngram}">Train</button>
          </li>
        `).join('');

        listTopBigrams.querySelectorAll('.btn-train-mini').forEach(btn => {
          btn.addEventListener('click', () => {
            const ngram = btn.getAttribute('data-ngram');
            switchTab('practice');
            loadSingleNgramDrill(ngram);
          });
        });
      }
    }

    // Render Top Words
    if (listTopWords) {
      const { sortedWords = [] } = mistakeData;
      if (sortedWords.length === 0) {
        listTopWords.innerHTML = `<li class="text-muted">No word errors recorded yet.</li>`;
      } else {
        listTopWords.innerHTML = sortedWords.slice(0, 8).map(sw => `
          <li class="mistake-item">
            <div class="mistake-label">
              <span class="word-tag">${escapeHtml(sw.word)}</span>
              <span class="count-badge">${sw.count}x missed</span>
            </div>
            <button type="button" class="btn-train-mini" data-word="${sw.word}">Train</button>
          </li>
        `).join('');

        listTopWords.querySelectorAll('.btn-train-mini').forEach(btn => {
          btn.addEventListener('click', () => {
            const word = btn.getAttribute('data-word');
            switchTab('practice');
            loadSingleWordDrill(word);
          });
        });
      }
    }
  }

  // ==========================================
  // SMART PASTE PARSER & LOG FORM
  // ==========================================
  function handleSmartPaste() {
    const rawText = pasteBox.value.trim();
    if (!rawText) {
      showPasteFeedback('Please paste your Monkeytype summary text first.', 'error');
      return;
    }

    const parsed = Coach.parseMonkeytypeText(rawText);
    if (!parsed || (parsed.wpm === null && parsed.accuracy === null)) {
      showPasteFeedback('Could not recognize WPM or Accuracy. Please check the text or enter manually.', 'error');
      return;
    }

    if (parsed.wpm !== null) inputWpm.value = parsed.wpm;
    if (parsed.accuracy !== null) inputAcc.value = parsed.accuracy;
    if (parsed.mode) selectMode.value = parsed.mode;
    if (parsed.missedWords && parsed.missedWords.length > 0) {
      inputMissedWords.value = parsed.missedWords.join(', ');
    }
    if (parsed.missedChars && parsed.missedChars.length > 0) {
      inputMissedChars.value = parsed.missedChars.join(', ');
    }

    showPasteFeedback(`Parsed: ${parsed.wpm || '?'} WPM, ${parsed.accuracy || '?'}% Acc, ${parsed.mode}! Review fields below.`, 'success');
  }

  function showPasteFeedback(msg, type) {
    if (!pasteFeedback) return;
    pasteFeedback.textContent = msg;
    pasteFeedback.className = `paste-feedback ${type}`;
    pasteFeedback.style.display = 'block';
    setTimeout(() => {
      if (pasteFeedback) pasteFeedback.style.display = 'none';
    }, 4000);
  }

  function handleLogSubmit(e) {
    e.preventDefault();

    const wpm = parseFloat(inputWpm.value);
    const accuracy = parseFloat(inputAcc.value);
    const mode = selectMode.value || 'time 60';
    const missedWordsRaw = inputMissedWords.value.trim();
    const missedCharsRaw = inputMissedChars.value.trim();
    const notes = inputNotes.value.trim();

    if (isNaN(wpm) || wpm < 0 || wpm > 350) {
      alert('Please enter a valid WPM between 0 and 350.');
      return;
    }

    if (isNaN(accuracy) || accuracy < 0 || accuracy > 100) {
      alert('Please enter a valid Accuracy percentage between 0 and 100.');
      return;
    }

    const missedWords = missedWordsRaw 
      ? missedWordsRaw.split(/[\s,;|]+/).map(w => w.trim().toLowerCase()).filter(Boolean)
      : [];

    const missedChars = missedCharsRaw 
      ? missedCharsRaw.split(/[\s,;|]+/).map(c => c.trim().toLowerCase()).filter(Boolean)
      : [];

    const newResult = Storage.addResult({
      wpm,
      accuracy,
      mode,
      missedWords,
      missedChars,
      notes,
      source: 'monkeytype'
    });

    if (newResult) {
      // Reset form
      formLog.reset();
      pasteBox.value = '';
      selectMode.value = 'time 60';

      refreshAll();

      // Switch to dashboard with success message
      switchTab('dashboard');
    }
  }

  // ==========================================
  // DRILL GENERATION & PRACTICE ROUTINES
  // ==========================================
  function generateAndLoadDrill(type = 'auto') {
    currentDrillConfig.type = type;
    const results = Storage.getResults();
    const settings = Storage.getSettings();
    const mistakeData = Coach.analyzeMistakes(results);

    let actualType = type;
    if (type === 'auto') {
      const feedback = Coach.getCoachingFeedback(results, settings.targetAccuracy);
      actualType = feedback.drillType || 'cadence';
    }

    const drill = Coach.generateDrill(actualType, mistakeData, settings.drillWordCount || 20);
    currentDrillConfig.activeDrill = drill;

    if (drillTitle) drillTitle.textContent = drill.title;
    if (drillExplanation) drillExplanation.textContent = drill.explanation;

    PracticeEngine.loadDrill(drill);
  }

  function loadSingleKeyDrill(key) {
    const drill = {
      type: 'single-key',
      title: `Single-Key Focus: [ ${key.toUpperCase()} ]`,
      explanation: `Targeting the '${key.toUpperCase()}' key. Practice striking this key smoothly without tension or hand drifting.`,
      wordCount: 20,
      text: generateSingleKeyText(key, 20)
    };
    currentDrillConfig.activeDrill = drill;
    if (drillTitle) drillTitle.textContent = drill.title;
    if (drillExplanation) drillExplanation.textContent = drill.explanation;
    PracticeEngine.loadDrill(drill);
  }

  function generateSingleKeyText(key, count) {
    const k = key.toLowerCase();
    const words = [];
    const pool = [
      `${k}a${k}`, `${k}e${k}`, `${k}o${k}`, `a${k}a`, `e${k}e`, `i${k}i`,
      `${k}and`, `the${k}`, `with${k}`, `${k}in`, `${k}out`, `${k}or`,
      `${k}${k}`, `to${k}`
    ];
    // Add real words containing this key
    Coach.VOCAB_COMMON.forEach(w => {
      if (w.includes(k)) pool.push(w);
    });

    for (let i = 0; i < count; i++) {
      words.push(pool[Math.floor(Math.random() * pool.length)]);
    }
    return words.join(' ');
  }

  function loadSingleNgramDrill(ngram) {
    const ng = ngram.toLowerCase();
    const words = [];
    const pool = Coach.VOCAB_COMMON.filter(w => w.includes(ng));
    if (pool.length === 0) {
      pool.push(`${ng}a`, `${ng}e`, `a${ng}`, `e${ng}`, `${ng}ing`);
    }

    for (let i = 0; i < 20; i++) {
      if (Math.random() > 0.4 && pool.length > 0) {
        words.push(pool[Math.floor(Math.random() * pool.length)]);
      } else {
        words.push(Coach.VOCAB_COMMON[Math.floor(Math.random() * Coach.VOCAB_COMMON.length)]);
      }
    }

    const drill = {
      type: 'single-ngram',
      title: `Bigram Precision: [ ${ng.toUpperCase()} ]`,
      explanation: `Training smooth finger transitions for the pair '${ng}'. Maintain unbroken rhythm between these two keys.`,
      wordCount: words.length,
      text: words.join(' ')
    };

    currentDrillConfig.activeDrill = drill;
    if (drillTitle) drillTitle.textContent = drill.title;
    if (drillExplanation) drillExplanation.textContent = drill.explanation;
    PracticeEngine.loadDrill(drill);
  }

  function loadSingleWordDrill(word) {
    const w = word.toLowerCase();
    const words = [];
    for (let i = 0; i < 20; i++) {
      if (i % 2 === 0) {
        words.push(w);
      } else {
        words.push(Coach.VOCAB_COMMON[Math.floor(Math.random() * Coach.VOCAB_COMMON.length)]);
      }
    }

    const drill = {
      type: 'single-word',
      title: `Target Word Drill: "${w}"`,
      explanation: `Repetitive muscle memory practice for the word "${w}" interleaved with steady connective vocabulary.`,
      wordCount: words.length,
      text: words.join(' ')
    };

    currentDrillConfig.activeDrill = drill;
    if (drillTitle) drillTitle.textContent = drill.title;
    if (drillExplanation) drillExplanation.textContent = drill.explanation;
    PracticeEngine.loadDrill(drill);
  }

  // ==========================================
  // SETTINGS & THEMES
  // ==========================================
  function loadSettingsIntoUI() {
    const s = Storage.getSettings();
    if (settingTargetAcc) {
      settingTargetAcc.value = s.targetAccuracy;
      targetAccValue.textContent = `${s.targetAccuracy}%`;
    }
    if (settingSoundToggle) settingSoundToggle.checked = s.soundEnabled;
    if (settingSoundType) settingSoundType.value = s.soundType;
    if (settingSoundVolume) settingSoundVolume.value = s.soundVolume;
    if (settingTheme) settingTheme.value = s.theme;
  }

  function applyTheme(themeName) {
    document.documentElement.setAttribute('data-theme', themeName || 'serika');
  }

  function handleExport() {
    const jsonStr = Storage.exportJSON();
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `monkeytype-coach-backup-${new Date().toISOString().slice(0, 10)}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }

  function handleImport(e) {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const res = Storage.importJSON(event.target.result);
      if (res.success) {
        loadSettingsIntoUI();
        applyTheme(Storage.getSettings().theme);
        refreshAll();
        alert(`Successfully imported ${res.count} test records!`);
      } else {
        alert(`Failed to import data: ${res.error}`);
      }
    };
    reader.readAsText(file);
    e.target.value = '';
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  // Kick off application
  initApp();
});
