/**
 * Visual QWERTY Keyboard Heatmap
 * Renders key caps dynamically colored according to error frequencies.
 * Clicking any key launches a targeted practice drill for that specific key.
 */
const KeyboardHeatmap = (() => {
  const KEYBOARD_ROWS = [
    ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
    ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'],
    ['z', 'x', 'c', 'v', 'b', 'n', 'm']
  ];

  let containerEl = null;
  let onKeySelectCallback = null;

  function init(containerId, onKeySelect) {
    containerEl = document.getElementById(containerId);
    onKeySelectCallback = onKeySelect;
  }

  function render(mistakeData) {
    if (!containerEl) return;
    const { keyCounts = {} } = mistakeData || {};

    // Find maximum count for scaling
    const counts = Object.values(keyCounts);
    const maxCount = counts.length > 0 ? Math.max(...counts, 1) : 1;

    let html = '<div class="keyboard-grid">';

    KEYBOARD_ROWS.forEach((row, rowIdx) => {
      html += `<div class="kb-row row-${rowIdx}">`;
      row.forEach(key => {
        const errorCount = keyCounts[key] || 0;
        const intensity = maxCount > 0 ? (errorCount / maxCount) : 0;
        
        let colorClass = 'heat-0';
        if (errorCount > 0) {
          if (intensity > 0.65) colorClass = 'heat-high';
          else if (intensity > 0.3) colorClass = 'heat-mid';
          else colorClass = 'heat-low';
        }

        html += `
          <button type="button" class="kb-key ${colorClass}" data-key="${key}" title="Key: ${key.toUpperCase()} | Errors: ${errorCount} (Click to train this key)">
            <span class="key-letter">${key.toUpperCase()}</span>
            ${errorCount > 0 ? `<span class="key-badge">${errorCount}</span>` : ''}
          </button>
        `;
      });
      html += '</div>';
    });

    html += '</div>';
    containerEl.innerHTML = html;

    // Attach click handlers
    containerEl.querySelectorAll('.kb-key').forEach(btn => {
      btn.addEventListener('click', () => {
        const key = btn.getAttribute('data-key');
        if (onKeySelectCallback) {
          onKeySelectCallback(key);
        }
      });
    });
  }

  return {
    init,
    render
  };
})();

if (typeof module !== 'undefined' && module.exports) {
  module.exports = KeyboardHeatmap;
}
