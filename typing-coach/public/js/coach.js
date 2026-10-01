/**
 * Coaching Engine & Mistake Pattern Analyzer
 * Focuses on:
 * 1. Accuracy-first pacing rules (Speed increases only when target accuracy is sustained)
 * 2. Mistake clustering (key heatmap, n-grams, problem words)
 * 3. Dynamic targeted drill generation
 * 4. Smart parsing of pasted Monkeytype test results
 */
const Coach = (() => {

  // Common high-frequency English vocabulary database categorized by letter / bigram
  const VOCAB_COMMON = [
    'the', 'be', 'to', 'of', 'and', 'a', 'in', 'that', 'have', 'i',
    'it', 'for', 'not', 'on', 'with', 'he', 'as', 'you', 'do', 'at',
    'this', 'but', 'his', 'by', 'from', 'they', 'we', 'say', 'her', 'she',
    'or', 'an', 'will', 'my', 'one', 'all', 'would', 'there', 'their', 'what',
    'so', 'up', 'out', 'if', 'about', 'who', 'get', 'which', 'go', 'me',
    'when', 'make', 'can', 'like', 'time', 'no', 'just', 'him', 'know', 'take',
    'people', 'into', 'year', 'your', 'good', 'some', 'could', 'them', 'see', 'other',
    'than', 'then', 'now', 'look', 'only', 'come', 'its', 'over', 'think', 'also',
    'back', 'after', 'use', 'two', 'how', 'our', 'work', 'first', 'well', 'way',
    'even', 'new', 'want', 'because', 'any', 'these', 'give', 'day', 'most', 'us'
  ];

  const KEY_SPECIFIC_WORDS = {
    'q': ['quick', 'quite', 'quote', 'queen', 'equal', 'quiet', 'liquid', 'require', 'square', 'quality', 'query', 'unique'],
    'z': ['zone', 'size', 'zero', 'lazy', 'prize', 'crazy', 'freeze', 'amaze', 'breeze', 'hazard', 'citizen', 'puzzle'],
    'x': ['next', 'text', 'exact', 'extra', 'fixed', 'mixed', 'relax', 'expert', 'complex', 'extend', 'index', 'expect'],
    'p': ['people', 'place', 'point', 'power', 'problem', 'part', 'open', 'speed', 'simple', 'happy', 'practice', 'paper'],
    'b': ['about', 'before', 'between', 'both', 'bring', 'build', 'busy', 'table', 'number', 'public', 'object', 'symbol'],
    'v': ['very', 'value', 'voice', 'never', 'every', 'level', 'given', 'event', 'cover', 'travel', 'active', 'provide'],
    'c': ['could', 'come', 'call', 'clean', 'clear', 'focus', 'reach', 'track', 'score', 'custom', 'circle', 'factor'],
    'w': ['with', 'what', 'when', 'which', 'world', 'write', 'power', 'allow', 'follow', 'between', 'always', 'growth'],
    'm': ['make', 'more', 'many', 'most', 'move', 'much', 'time', 'name', 'same', 'system', 'moment', 'modern'],
    'k': ['know', 'make', 'take', 'look', 'back', 'think', 'work', 'like', 'keep', 'break', 'track', 'market'],
    'j': ['just', 'join', 'judge', 'major', 'project', 'object', 'enjoy', 'adjust', 'subject', 'journal', 'journey'],
    'y': ['they', 'year', 'your', 'only', 'way', 'day', 'many', 'may', 'say', 'any', 'every', 'always']
  };

  /**
   * Analyze all past tests to extract key frequencies, n-grams, and problem words
   */
  function analyzeMistakes(results) {
    const keyCounts = {};
    const wordCounts = {};
    const ngramCounts = {};

    let totalMissedChars = 0;
    let totalMissedWords = 0;

    results.forEach(test => {
      // Collect missed chars
      if (Array.isArray(test.missedChars)) {
        test.missedChars.forEach(c => {
          const char = c.toLowerCase().trim();
          if (char.length === 1 && char >= 'a' && char <= 'z') {
            keyCounts[char] = (keyCounts[char] || 0) + 1;
            totalMissedChars++;
          }
        });
      }

      // Collect missed words
      if (Array.isArray(test.missedWords)) {
        test.missedWords.forEach(w => {
          const word = w.toLowerCase().replace(/[^a-z]/g, '').trim();
          if (word.length >= 2) {
            wordCounts[word] = (wordCounts[word] || 0) + 1;
            totalMissedWords++;

            // Break word into individual character mistakes if missedChars wasn't specified
            if (!test.missedChars || test.missedChars.length === 0) {
              for (const ch of word) {
                if (ch >= 'a' && ch <= 'z') {
                  keyCounts[ch] = (keyCounts[ch] || 0) + 0.3; // weighted attribution
                }
              }
            }

            // Extract bigrams (letter pairs)
            for (let i = 0; i < word.length - 1; i++) {
              const bg = word.substring(i, i + 2);
              if (/^[a-z]{2}$/.test(bg)) {
                ngramCounts[bg] = (ngramCounts[bg] || 0) + 1;
              }
            }
          }
        });
      }
    });

    // Sort rankings
    const sortedKeys = Object.entries(keyCounts)
      .map(([key, count]) => ({ key, count: Math.round(count * 10) / 10 }))
      .sort((a, b) => b.count - a.count);

    const sortedWords = Object.entries(wordCounts)
      .map(([word, count]) => ({ word, count }))
      .sort((a, b) => b.count - a.count);

    const sortedNgrams = Object.entries(ngramCounts)
      .map(([ngram, count]) => ({ ngram, count }))
      .sort((a, b) => b.count - a.count);

    return {
      keyCounts,
      sortedKeys,
      wordCounts,
      sortedWords,
      ngramCounts,
      sortedNgrams,
      totalMissedChars,
      totalMissedWords
    };
  }

  /**
   * Evaluates recent performance against target accuracy.
   * Accuracy First Principle:
   * Only after maintaining >= target accuracy can user focus on unlocking higher speed pacing.
   */
  function getCoachingFeedback(results, targetAccuracy = 96) {
    if (!results || results.length === 0) {
      return {
        status: 'welcome',
        badge: 'GET STARTED',
        badgeColor: 'neutral',
        headline: 'Welcome to your Typing Practice Coach',
        message: 'Log your first Monkeytype test above or load sample data. Set your accuracy target to begin receiving tailored pacing guidance.',
        targetAccuracy,
        recentAccuracy: 0,
        recentWpm: 0,
        streakCount: 0,
        recommendedAction: 'Log a 60s or 50-word test to establish your baseline.',
        drillType: 'cadence'
      };
    }

    // Examine recent 3-5 tests
    const recentTests = results.slice(-5);
    const recentAccSum = recentTests.reduce((acc, t) => acc + t.accuracy, 0);
    const recentAccuracy = Math.round((recentAccSum / recentTests.length) * 10) / 10;

    const recentWpmSum = recentTests.reduce((acc, t) => acc + t.wpm, 0);
    const recentWpm = Math.round((recentWpmSum / recentTests.length) * 10) / 10;

    // Count consecutive recent tests meeting target
    let streakCount = 0;
    for (let i = results.length - 1; i >= 0; i--) {
      if (results[i].accuracy >= targetAccuracy) {
        streakCount++;
      } else {
        break;
      }
    }

    const mistakes = analyzeMistakes(recentTests);
    const topKeys = mistakes.sortedKeys.slice(0, 3).map(k => k.key.toUpperCase()).join(', ');

    // Accuracy Delta
    const delta = recentAccuracy - targetAccuracy;

    if (delta < -2.5) {
      // Critical Speed Throttle
      return {
        status: 'throttle',
        badge: 'ACCURACY REPAIR',
        badgeColor: 'danger',
        headline: 'Throttle Back Speed — Focus On Clean Keystrokes',
        message: `Your recent accuracy is ${recentAccuracy}%, which is below your ${targetAccuracy}% target. You are currently typing faster than your muscle memory can accurately execute. Slow down your pacing by 10 to 15 WPM on Monkeytype. Strive for 100% clean words before attempting to burst.`,
        targetAccuracy,
        recentAccuracy,
        recentWpm,
        streakCount: 0,
        recommendedAction: topKeys ? `Practice isolating your high-error keys (${topKeys}) with rhythm cadence drills.` : 'Run a slow cadence drill with zero errors.',
        drillType: topKeys ? 'keys' : 'cadence'
      };
    } else if (delta < 0) {
      // Precision Stabilization
      return {
        status: 'stabilize',
        badge: 'STABILIZE PRECISION',
        badgeColor: 'warning',
        headline: 'Almost At Target — Smooth Out Hesitations',
        message: `Your accuracy is ${recentAccuracy}% (close to your ${targetAccuracy}% goal). You are experiencing occasional error spikes on tricky finger transitions. Keep your eyes 1 to 2 words ahead rather than staring at the active letter, and maintain a steady metronome rhythm.`,
        targetAccuracy,
        recentAccuracy,
        recentWpm,
        streakCount,
        recommendedAction: 'Practice your frequent missed words and n-grams without stopping between letters.',
        drillType: 'ngrams'
      };
    } else {
      // Green Light / Tempo Expansion
      const speedAdvice = streakCount >= 3 
        ? `You have sustained ${targetAccuracy}%+ accuracy for ${streakCount} tests in a row! You have earned a speed push: safely increase your finger tempo by 3-5 WPM on comfortable vocabulary.`
        : `Target reached! Maintain this precision zone (${recentAccuracy}%) for at least 3 consecutive tests before pushing for faster speed bursts.`;

      return {
        status: 'unlock',
        badge: streakCount >= 3 ? 'SPEED UNLOCK' : 'TARGET ACHIEVED',
        badgeColor: 'success',
        headline: streakCount >= 3 ? 'Green Light: Safely Accelerate Tempo' : 'Accuracy Target Met: Cement Muscle Memory',
        message: speedAdvice,
        targetAccuracy,
        recentAccuracy,
        recentWpm,
        streakCount,
        recommendedAction: 'Run a short speed push drill on familiar words or test yourself on Monkeytype 60s.',
        drillType: 'words'
      };
    }
  }

  /**
   * Generates targeted practice drills based on mistake analysis
   */
  function generateDrill(type, mistakeData, wordCount = 20) {
    const { sortedKeys = [], sortedWords = [], sortedNgrams = [] } = mistakeData || {};
    let words = [];
    let title = '';
    let explanation = '';

    if (type === 'keys' && sortedKeys.length > 0) {
      // Drill isolating top 2-4 missed characters
      const targetKeys = sortedKeys.slice(0, 3).map(k => k.key.toLowerCase());
      title = `Key Isolation Drill: [ ${targetKeys.map(k => k.toUpperCase()).join(', ')} ]`;
      explanation = `Focusing on keys with your highest error counts (${targetKeys.join(', ')}). Type steadily and avoid double-tapping.`;

      const pool = [];
      targetKeys.forEach(k => {
        if (KEY_SPECIFIC_WORDS[k]) {
          pool.push(...KEY_SPECIFIC_WORDS[k]);
        }
      });

      // Add alternating finger patterns
      const fingerPatterns = targetKeys.flatMap(k => [
        `${k}a${k}a`, `${k}e${k}e`, `${k}i${k}i`, `${k}o${k}o`,
        `a${k}e`, `i${k}o`, `${k}${k}a`, `the${k}`
      ]);
      pool.push(...fingerPatterns);

      // Fill up drill
      while (words.length < wordCount) {
        if (pool.length > 0) {
          const chosen = pool[Math.floor(Math.random() * pool.length)];
          words.push(chosen);
        } else {
          words.push(targetKeys[Math.floor(Math.random() * targetKeys.length)]);
        }
      }
    } else if (type === 'ngrams' && sortedNgrams.length > 0) {
      // Drill isolating frequent bigrams
      const topNgrams = sortedNgrams.slice(0, 3).map(n => n.ngram.toLowerCase());
      title = `N-Gram Flow Drill: [ ${topNgrams.join(', ')} ]`;
      explanation = `Trains seamless finger transitions between character pairs (${topNgrams.join(', ')}) that commonly trip up your rhythm.`;

      const pool = [];
      VOCAB_COMMON.forEach(w => {
        if (topNgrams.some(ng => w.includes(ng))) {
          pool.push(w);
        }
      });
      // Add problem words containing these ngrams
      sortedWords.forEach(sw => {
        if (topNgrams.some(ng => sw.word.includes(ng))) {
          pool.push(sw.word);
        }
      });

      if (pool.length === 0) {
        pool.push(...VOCAB_COMMON);
      }

      while (words.length < wordCount) {
        const word = pool[Math.floor(Math.random() * pool.length)];
        words.push(word);
      }
    } else if (type === 'words' && sortedWords.length > 0) {
      // Direct practice on past missed words
      const topWords = sortedWords.slice(0, 8).map(w => w.word);
      title = `Problem Word Re-training`;
      explanation = `Reinforcing your actual missed words from past Monkeytype tests, interleaved with smooth transition words.`;

      while (words.length < wordCount) {
        if (Math.random() > 0.4 && topWords.length > 0) {
          const pw = topWords[Math.floor(Math.random() * topWords.length)];
          words.push(pw);
        } else {
          const cw = VOCAB_COMMON[Math.floor(Math.random() * VOCAB_COMMON.length)];
          words.push(cw);
        }
      }
    } else {
      // Clean Cadence / Metronome Drill (Default / Fallback)
      title = `Rhythm & Cadence Drill`;
      explanation = `High-frequency clean words to develop steady, unbroken typing cadence. Aim for 100% accuracy.`;
      
      const shuffled = [...VOCAB_COMMON].sort(() => 0.5 - Math.random());
      words = shuffled.slice(0, wordCount);
    }

    return {
      type,
      title,
      explanation,
      wordCount: words.length,
      text: words.join(' ')
    };
  }

  /**
   * Smart Parser for Monkeytype test results.
   * Can parse free text pasted by the user or copied from Monkeytype.
   * Examples:
   * "wpm 85.4 acc 98% 60s english missed words: rhythm separate"
   * "72 wpm 95% time 15 missed: the, quite, p"
   */
  function parseMonkeytypeText(input) {
    if (!input || typeof input !== 'string') return null;

    const result = {
      wpm: null,
      accuracy: null,
      mode: 'time 60',
      missedWords: [],
      missedChars: []
    };

    // 1. Extract WPM
    const wpmMatch = input.match(/(\d+(?:\.\d+)?)\s*(?:wpm|words\s*per\s*minute)/i) ||
                     input.match(/(?:wpm|speed)[:=\s]+(\d+(?:\.\d+)?)/i);
    if (wpmMatch) {
      result.wpm = parseFloat(wpmMatch[1]);
    }

    // 2. Extract Accuracy
    const accMatch = input.match(/(\d+(?:\.\d+)?)\s*%\s*(?:acc|accuracy)?/i) ||
                     input.match(/(?:acc|accuracy)[:=\s]+(\d+(?:\.\d+)?)\s*%?/i);
    if (accMatch) {
      result.accuracy = parseFloat(accMatch[1]);
    }

    // 3. Extract Test Mode
    const modeMatch = input.match(/(time\s*\d+|words\s*\d+|quote|zen|custom)/i) ||
                      input.match(/\b(15s|30s|60s|120s|10w|25w|50w|100w)\b/i);
    if (modeMatch) {
      let m = modeMatch[1].toLowerCase().trim();
      if (m.endsWith('s') && !m.startsWith('time')) m = 'time ' + m.replace('s', '');
      if (m.endsWith('w') && !m.startsWith('words')) m = 'words ' + m.replace('w', '');
      result.mode = m;
    }

    // 4. Extract Missed Words / Characters
    const missedIndex = input.toLowerCase().indexOf('missed');
    if (missedIndex !== -1) {
      const missedSubstr = input.substring(missedIndex);
      const afterColon = missedSubstr.split(/[:\n]/).slice(1).join(' ');
      const tokens = afterColon.split(/[\s,;|]+/).map(t => t.trim().toLowerCase()).filter(Boolean);

      tokens.forEach(t => {
        if (t.length === 1 && t >= 'a' && t <= 'z') {
          result.missedChars.push(t);
        } else if (t.length > 1 && !['words', 'characters', 'none', 'and', 'or'].includes(t)) {
          result.missedWords.push(t);
        }
      });
    }

    return result;
  }

  return {
    analyzeMistakes,
    getCoachingFeedback,
    generateDrill,
    parseMonkeytypeText,
    VOCAB_COMMON
  };
})();

if (typeof module !== 'undefined' && module.exports) {
  module.exports = Coach;
}
