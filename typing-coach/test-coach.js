/**
 * Verification test script for Coach logic, mistake analysis, and parser
 */
const assert = require('assert');

// Mock localStorage for node test
global.localStorage = {
  _data: {},
  getItem(k) { return this._data[k] || null; },
  setItem(k, v) { this._data[k] = String(v); },
  removeItem(k) { delete this._data[k]; },
  clear() { this._data = {}; }
};

const Storage = require('./public/js/storage.js');
const Coach = require('./public/js/coach.js');

console.log('--- Starting Verification Tests ---');

// 1. Test Smart Parser
console.log('1. Testing Smart Monkeytype Parser...');
const testInput1 = "wpm: 76.4 acc: 97.2% time 60s missed: rhythm, separate, q";
const parsed1 = Coach.parseMonkeytypeText(testInput1);
assert.strictEqual(parsed1.wpm, 76.4, 'WPM should parse correctly');
assert.strictEqual(parsed1.accuracy, 97.2, 'Accuracy should parse correctly');
assert.strictEqual(parsed1.mode, 'time 60', 'Mode should parse correctly');
assert(parsed1.missedWords.includes('rhythm'), 'Missed words should contain rhythm');
assert(parsed1.missedWords.includes('separate'), 'Missed words should contain separate');
assert(parsed1.missedChars.includes('q'), 'Missed chars should contain q');
console.log('  ✓ Smart Parser test 1 passed');

const testInput2 = "54 wpm 92% words 50 missed words: receive, accommodation";
const parsed2 = Coach.parseMonkeytypeText(testInput2);
assert.strictEqual(parsed2.wpm, 54, 'WPM parsed');
assert.strictEqual(parsed2.accuracy, 92, 'Accuracy parsed');
assert.strictEqual(parsed2.mode, 'words 50', 'Mode parsed');
assert(parsed2.missedWords.includes('receive'), 'Missed words parsed');
console.log('  ✓ Smart Parser test 2 passed');

// 2. Test Mistake Pattern Analysis
console.log('2. Testing Mistake Pattern Analysis...');
const mockTests = [
  { missedChars: ['p', 'q', 'p'], missedWords: ['rhythm', 'receive'] },
  { missedChars: ['q', 'z'], missedWords: ['privilege', 'rhythm'] }
];
const analysis = Coach.analyzeMistakes(mockTests);
assert(analysis.keyCounts['p'] >= 2, 'Key counts for p should be >= 2');
assert(analysis.keyCounts['q'] >= 2, 'Key counts for q should be >= 2');
assert.strictEqual(analysis.wordCounts['rhythm'], 2, 'Word rhythm should be counted twice');
assert(analysis.sortedNgrams.length > 0, 'N-grams should be extracted from missed words');
console.log('  ✓ Mistake Pattern Analysis passed');

// 3. Test Coaching Feedback & Accuracy-First Logic
console.log('3. Testing Coaching Advice Rules...');
// Below target (< 94% with 96% target)
const lowAccTests = [
  { wpm: 70, accuracy: 91 },
  { wpm: 72, accuracy: 92 },
  { wpm: 75, accuracy: 90 }
];
const feedbackLow = Coach.getCoachingFeedback(lowAccTests, 96);
assert.strictEqual(feedbackLow.status, 'throttle', 'Should trigger speed throttle mode when accuracy is low');
assert.strictEqual(feedbackLow.badge, 'ACCURACY REPAIR');
console.log('  ✓ Accuracy repair trigger verified: ' + feedbackLow.headline);

// Above target (>= 96%)
const highAccTests = [
  { wpm: 68, accuracy: 97.5 },
  { wpm: 70, accuracy: 98.2 },
  { wpm: 72, accuracy: 98.0 }
];
const feedbackHigh = Coach.getCoachingFeedback(highAccTests, 96);
assert.strictEqual(feedbackHigh.status, 'unlock', 'Should trigger speed unlock mode when accuracy is high');
console.log('  ✓ Speed unlock trigger verified: ' + feedbackHigh.headline);

// 4. Test Drill Generation
console.log('4. Testing Drill Generation...');
const keyDrill = Coach.generateDrill('keys', analysis, 15);
assert.strictEqual(keyDrill.type, 'keys');
assert(keyDrill.text.length > 10, 'Drill text generated');
assert.strictEqual(keyDrill.text.split(' ').length, 15, 'Drill should have requested word count');

const ngramDrill = Coach.generateDrill('ngrams', analysis, 10);
assert.strictEqual(ngramDrill.type, 'ngrams');

const wordDrill = Coach.generateDrill('words', analysis, 10);
assert.strictEqual(wordDrill.type, 'words');

const cadenceDrill = Coach.generateDrill('cadence', analysis, 10);
assert.strictEqual(cadenceDrill.type, 'cadence');
console.log('  ✓ All 4 drill types generated successfully');

// 5. Test Storage & Sample Data
console.log('5. Testing Storage Manager...');
Storage.clearAllData();
assert.strictEqual(Storage.getResults().length, 0);

Storage.addResult({
  wpm: 65,
  accuracy: 97,
  mode: 'time 60',
  missedWords: ['rhythm'],
  missedChars: ['p']
});
assert.strictEqual(Storage.getResults().length, 1);

const sampleData = Storage.loadSampleData();
assert(sampleData.length > 5, 'Sample data should populate history');
const exported = Storage.exportJSON();
assert(exported.includes('sample_'), 'Export JSON includes sample data');

const importRes = Storage.importJSON(exported);
assert(importRes.success, 'Import JSON should succeed');
console.log('  ✓ Storage operations verified');

console.log('\n--- ALL VERIFICATION TESTS PASSED ---');
