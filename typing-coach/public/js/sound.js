/**
 * Sound Manager for Typing Practice Coach
 * Synthesizes mechanical switch keystroke acoustics using Web Audio API.
 * Requires 0 external audio files and runs completely offline.
 */
const SoundManager = (() => {
  let audioCtx = null;

  function initContext() {
    if (!audioCtx && (window.AudioContext || window.webkitAudioContext)) {
      const AudioContextClass = window.AudioContext || window.webkitAudioContext;
      audioCtx = new AudioContextClass();
    }
    if (audioCtx && audioCtx.state === 'suspended') {
      audioCtx.resume();
    }
  }

  function playKeyClick(type = 'thock', volume = 0.15) {
    try {
      initContext();
      if (!audioCtx) return;

      const now = audioCtx.currentTime;
      const masterGain = audioCtx.createGain();
      masterGain.gain.setValueAtTime(volume, now);
      masterGain.connect(audioCtx.destination);

      if (type === 'thock') {
        // Deep, dampened tactile thock (vintage mechanical switch)
        const osc = audioCtx.createOscillator();
        const filter = audioCtx.createBiquadFilter();
        const gain = audioCtx.createGain();

        osc.type = 'triangle';
        const baseFreq = 180 + Math.random() * 40;
        osc.frequency.setValueAtTime(baseFreq, now);
        osc.frequency.exponentialRampToValueAtTime(45, now + 0.04);

        filter.type = 'lowpass';
        filter.frequency.setValueAtTime(600, now);
        filter.frequency.exponentialRampToValueAtTime(100, now + 0.05);

        gain.gain.setValueAtTime(0.8, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.055);

        osc.connect(filter);
        filter.connect(gain);
        gain.connect(masterGain);

        osc.start(now);
        osc.stop(now + 0.06);

        // Click transient
        createClickTransient(now, 800, 0.3, masterGain);
      } else if (type === 'clack') {
        // Higher pitched crisp switch
        const osc = audioCtx.createOscillator();
        const filter = audioCtx.createBiquadFilter();
        const gain = audioCtx.createGain();

        osc.type = 'square';
        osc.frequency.setValueAtTime(380 + Math.random() * 60, now);
        osc.frequency.exponentialRampToValueAtTime(80, now + 0.035);

        filter.type = 'bandpass';
        filter.frequency.setValueAtTime(1400, now);
        filter.Q.setValueAtTime(3, now);

        gain.gain.setValueAtTime(0.6, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.04);

        osc.connect(filter);
        filter.connect(gain);
        gain.connect(masterGain);

        osc.start(now);
        osc.stop(now + 0.045);

        createClickTransient(now, 2200, 0.4, masterGain);
      } else {
        // Default clean crisp click
        createClickTransient(now, 1800, 0.7, masterGain);
      }
    } catch (e) {
      // Audio autoplay policy or device failure - gracefully ignore
    }
  }

  function createClickTransient(time, freq, amp, destination) {
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(freq, time);
    osc.frequency.exponentialRampToValueAtTime(120, time + 0.015);

    gain.gain.setValueAtTime(amp, time);
    gain.gain.exponentialRampToValueAtTime(0.001, time + 0.02);

    osc.connect(gain);
    gain.connect(destination);

    osc.start(time);
    osc.stop(time + 0.025);
  }

  function playErrorBeep(volume = 0.15) {
    try {
      initContext();
      if (!audioCtx) return;

      const now = audioCtx.currentTime;
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();

      osc.type = 'sawtooth';
      osc.frequency.setValueAtTime(130, now);
      osc.frequency.linearRampToValueAtTime(90, now + 0.08);

      gain.gain.setValueAtTime(volume * 0.7, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.08);

      osc.connect(gain);
      gain.connect(audioCtx.destination);

      osc.start(now);
      osc.stop(now + 0.09);
    } catch (e) {}
  }

  function playSuccessChord() {
    try {
      initContext();
      if (!audioCtx) return;
      const notes = [440, 554.37, 659.25]; // A major chord
      notes.forEach((freq, idx) => {
        const now = audioCtx.currentTime + idx * 0.06;
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(freq, now);
        gain.gain.setValueAtTime(0.12, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.25);
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.start(now);
        osc.stop(now + 0.26);
      });
    } catch (e) {}
  }

  return {
    initContext,
    playKeyClick,
    playErrorBeep,
    playSuccessChord
  };
})();

if (typeof module !== 'undefined' && module.exports) {
  module.exports = SoundManager;
}
