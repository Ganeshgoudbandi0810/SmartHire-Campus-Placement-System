/**
 * Storage Manager for Monkeytype Typing Practice Coach
 * Persists test history, settings, and mistake records in localStorage with JSON export/import.
 */
const Storage = (() => {
  const RESULTS_KEY = 'mt_coach_results_v1';
  const SETTINGS_KEY = 'mt_coach_settings_v1';

  const DEFAULT_SETTINGS = {
    targetAccuracy: 96,
    soundEnabled: true,
    soundVolume: 0.18,
    soundType: 'thock',
    theme: 'serika',
    drillWordCount: 20,
    strictPacing: true
  };

  function getSettings() {
    try {
      const raw = localStorage.getItem(SETTINGS_KEY);
      if (!raw) return { ...DEFAULT_SETTINGS };
      return { ...DEFAULT_SETTINGS, ...JSON.parse(raw) };
    } catch (e) {
      console.error('Error reading settings from localStorage:', e);
      return { ...DEFAULT_SETTINGS };
    }
  }

  function saveSettings(settings) {
    try {
      const current = getSettings();
      const updated = { ...current, ...settings };
      localStorage.setItem(SETTINGS_KEY, JSON.stringify(updated));
      return updated;
    } catch (e) {
      console.error('Error saving settings to localStorage:', e);
      return getSettings();
    }
  }

  function getResults() {
    try {
      const raw = localStorage.getItem(RESULTS_KEY);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed.sort((a, b) => a.timestamp - b.timestamp);
    } catch (e) {
      console.error('Error reading results from localStorage:', e);
      return [];
    }
  }

  function addResult(resultData) {
    try {
      const results = getResults();
      const newEntry = {
        id: 'res_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
        timestamp: resultData.timestamp || Date.now(),
        dateStr: new Date(resultData.timestamp || Date.now()).toLocaleString(),
        wpm: Math.round(Number(resultData.wpm) * 10) / 10,
        accuracy: Math.round(Number(resultData.accuracy) * 10) / 10,
        mode: (resultData.mode || 'time 60').trim().toLowerCase(),
        missedWords: Array.isArray(resultData.missedWords) 
          ? resultData.missedWords.map(w => w.trim().toLowerCase()).filter(Boolean)
          : [],
        missedChars: Array.isArray(resultData.missedChars)
          ? resultData.missedChars.map(c => c.trim().toLowerCase()).filter(Boolean)
          : [],
        notes: (resultData.notes || '').trim(),
        source: resultData.source || 'monkeytype'
      };

      results.push(newEntry);
      localStorage.setItem(RESULTS_KEY, JSON.stringify(results));
      return newEntry;
    } catch (e) {
      console.error('Error adding result to localStorage:', e);
      return null;
    }
  }

  function deleteResult(id) {
    try {
      let results = getResults();
      results = results.filter(r => r.id !== id);
      localStorage.setItem(RESULTS_KEY, JSON.stringify(results));
      return results;
    } catch (e) {
      console.error('Error deleting result:', e);
      return getResults();
    }
  }

  function clearAllData() {
    localStorage.removeItem(RESULTS_KEY);
  }

  function exportJSON() {
    const data = {
      version: 1,
      exportedAt: new Date().toISOString(),
      settings: getSettings(),
      results: getResults()
    };
    return JSON.stringify(data, null, 2);
  }

  function importJSON(jsonStr) {
    try {
      const data = JSON.parse(jsonStr);
      if (!data || typeof data !== 'object') throw new Error('Invalid JSON structure');

      if (data.settings) {
        saveSettings(data.settings);
      }
      if (Array.isArray(data.results)) {
        localStorage.setItem(RESULTS_KEY, JSON.stringify(data.results));
      }
      return { success: true, count: Array.isArray(data.results) ? data.results.length : 0 };
    } catch (err) {
      return { success: false, error: err.message };
    }
  }

  function loadSampleData() {
    const now = Date.now();
    const day = 24 * 60 * 60 * 1000;
    const hour = 60 * 60 * 1000;

    const sampleTests = [
      {
        timestamp: now - 5 * day - 4 * hour,
        wpm: 58.2,
        accuracy: 91.5,
        mode: 'time 60',
        missedWords: ['rhythm', 'necessary', 'accommodate'],
        missedChars: ['p', 'q', 'z'],
        notes: 'Cold hands, rushed through tricky words',
        source: 'monkeytype'
      },
      {
        timestamp: now - 5 * day,
        wpm: 64.0,
        accuracy: 92.8,
        mode: 'time 60',
        missedWords: ['receive', 'calendar', 'definitely'],
        missedChars: ['c', 'v', 'p'],
        notes: 'Pushing speed too fast',
        source: 'monkeytype'
      },
      {
        timestamp: now - 4 * day - 2 * hour,
        wpm: 55.4,
        accuracy: 96.2,
        mode: 'words 50',
        missedWords: ['queue'],
        missedChars: ['q', 'u'],
        notes: 'Followed coach advice: slowed down for clean precision',
        source: 'monkeytype'
      },
      {
        timestamp: now - 3 * day - 5 * hour,
        wpm: 59.8,
        accuracy: 97.4,
        mode: 'time 60',
        missedWords: ['awkward'],
        missedChars: ['w', 'k'],
        notes: 'Accuracy feeling steady',
        source: 'monkeytype'
      },
      {
        timestamp: now - 3 * day,
        wpm: 63.5,
        accuracy: 95.8,
        mode: 'time 15',
        missedWords: ['though', 'thought'],
        missedChars: ['t', 'h', 'g'],
        notes: 'Sprint test',
        source: 'monkeytype'
      },
      {
        timestamp: now - 2 * day - 3 * hour,
        wpm: 66.2,
        accuracy: 98.1,
        mode: 'time 60',
        missedWords: ['business'],
        missedChars: ['s', 'i'],
        notes: 'Target met! High accuracy unlock',
        source: 'monkeytype'
      },
      {
        timestamp: now - 1 * day - 4 * hour,
        wpm: 68.9,
        accuracy: 97.6,
        mode: 'words 50',
        missedWords: ['privilege'],
        missedChars: ['l', 'g'],
        notes: 'Great flow state',
        source: 'monkeytype'
      },
      {
        timestamp: now - 6 * hour,
        wpm: 72.4,
        accuracy: 98.5,
        mode: 'time 60',
        missedWords: ['separate'],
        missedChars: ['a', 'r'],
        notes: 'New personal best with solid 98%+ accuracy!',
        source: 'monkeytype'
      }
    ];

    const results = sampleTests.map((t, idx) => ({
      id: 'sample_' + idx,
      timestamp: t.timestamp,
      dateStr: new Date(t.timestamp).toLocaleString(),
      wpm: t.wpm,
      accuracy: t.accuracy,
      mode: t.mode,
      missedWords: t.missedWords,
      missedChars: t.missedChars,
      notes: t.notes,
      source: t.source
    }));

    localStorage.setItem(RESULTS_KEY, JSON.stringify(results));
    return results;
  }

  return {
    getSettings,
    saveSettings,
    getResults,
    addResult,
    deleteResult,
    clearAllData,
    exportJSON,
    importJSON,
    loadSampleData
  };
})();

if (typeof module !== 'undefined' && module.exports) {
  module.exports = Storage;
}
