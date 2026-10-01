/**
 * Progress Charts Component (Native HTML5 Canvas)
 * Renders high-DPI smooth line charts for WPM and Accuracy progression over time,
 * including a 5-test moving average, user accuracy target guideline, and hover tooltips.
 */
const ChartRenderer = (() => {
  let activeCanvas = null;
  let activeTooltip = null;
  let currentResults = [];
  let currentFilter = 'all';
  let hoveredPoint = null;

  function init(canvasId, tooltipId) {
    activeCanvas = document.getElementById(canvasId);
    activeTooltip = document.getElementById(tooltipId);

    if (activeCanvas) {
      // Setup pointer events for tooltip
      activeCanvas.addEventListener('mousemove', handleMouseMove);
      activeCanvas.addEventListener('mouseleave', handleMouseLeave);
      window.addEventListener('resize', debounce(() => render(currentResults, currentFilter), 150));
    }
  }

  function debounce(fn, wait) {
    let timeout;
    return (...args) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => fn(...args), wait);
    };
  }

  function setFilter(filter) {
    currentFilter = filter;
    render(currentResults, currentFilter);
  }

  function render(results, filter = 'all') {
    if (!activeCanvas) return;
    currentResults = results || [];
    currentFilter = filter;

    const ctx = activeCanvas.getContext('2d');
    const rect = activeCanvas.parentElement.getBoundingClientRect();
    const width = Math.max(rect.width, 320);
    const height = 300;

    const dpr = window.devicePixelRatio || 1;
    activeCanvas.width = width * dpr;
    activeCanvas.height = height * dpr;
    activeCanvas.style.width = width + 'px';
    activeCanvas.style.height = height + 'px';

    ctx.scale(dpr, dpr);

    // Filter results
    let filtered = currentResults;
    if (filter !== 'all') {
      filtered = currentResults.filter(r => (r.mode || '').includes(filter.toLowerCase()));
    }

    // Clear background
    ctx.clearRect(0, 0, width, height);

    if (!filtered || filtered.length === 0) {
      renderEmptyState(ctx, width, height);
      return;
    }

    const padding = { top: 30, right: 35, bottom: 45, left: 50 };
    const chartWidth = width - padding.left - padding.right;
    const chartHeight = height - padding.top - padding.bottom;

    // Calculate ranges
    const wpms = filtered.map(r => r.wpm);
    const accs = filtered.map(r => r.accuracy);
    const minWpm = Math.max(0, Math.floor(Math.min(...wpms) * 0.85));
    const maxWpm = Math.ceil(Math.max(...wpms) * 1.15) || 100;

    const minAcc = Math.max(70, Math.floor(Math.min(...accs, 85)));
    const maxAcc = 100;

    // Grid lines and Y-axis labels
    drawGridAndAxes(ctx, padding, chartWidth, chartHeight, minWpm, maxWpm);

    // Accuracy Target Guideline
    const settings = (typeof Storage !== 'undefined') ? Storage.getSettings() : { targetAccuracy: 96 };
    drawTargetLine(ctx, padding, chartWidth, chartHeight, settings.targetAccuracy, minWpm, maxWpm);

    // Coordinate mapping
    const points = filtered.map((r, i) => {
      const x = padding.left + (i / Math.max(1, filtered.length - 1)) * chartWidth;
      const yWpm = padding.top + chartHeight - ((r.wpm - minWpm) / Math.max(1, maxWpm - minWpm)) * chartHeight;
      const yAcc = padding.top + chartHeight - ((r.accuracy - minAcc) / Math.max(1, maxAcc - minAcc)) * chartHeight;
      return { x, yWpm, yAcc, data: r, index: i };
    });

    // Draw Accuracy Line (Accent teal/cyan)
    drawLine(ctx, points.map(p => ({ x: p.x, y: p.yAcc })), '#4fd1c5', 1.8, [3, 3]);

    // Draw 5-test Moving Average Line (Translucent Amber)
    if (points.length >= 3) {
      const maPoints = calculateMovingAverage(points, 3);
      drawLine(ctx, maPoints, 'rgba(237, 137, 54, 0.65)', 2.2, []);
    }

    // Draw WPM Main Line (Primary Yellow / Serika Gold)
    drawLine(ctx, points.map(p => ({ x: p.x, y: p.yWpm })), '#e2b714', 3.0, []);

    // Draw Points
    points.forEach(p => {
      // WPM dot
      ctx.beginPath();
      ctx.arc(p.x, p.yWpm, 4.5, 0, Math.PI * 2);
      ctx.fillStyle = '#1e1f29';
      ctx.fill();
      ctx.lineWidth = 2.5;
      ctx.strokeStyle = '#e2b714';
      ctx.stroke();

      // Accuracy dot
      ctx.beginPath();
      ctx.arc(p.x, p.yAcc, 3.5, 0, Math.PI * 2);
      ctx.fillStyle = '#4fd1c5';
      ctx.fill();
    });

    // Store points for hover detection
    activeCanvas._points = points;
  }

  function calculateMovingAverage(points, windowSize = 3) {
    const maPoints = [];
    for (let i = 0; i < points.length; i++) {
      const start = Math.max(0, i - windowSize + 1);
      const subset = points.slice(start, i + 1);
      const avgY = subset.reduce((sum, p) => sum + p.yWpm, 0) / subset.length;
      maPoints.push({ x: points[i].x, y: avgY });
    }
    return maPoints;
  }

  function drawGridAndAxes(ctx, padding, width, height, minWpm, maxWpm) {
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
    ctx.lineWidth = 1;
    ctx.font = '11px sans-serif';
    ctx.fillStyle = 'rgba(255, 255, 255, 0.45)';
    ctx.textAlign = 'right';

    const steps = 4;
    for (let i = 0; i <= steps; i++) {
      const y = padding.top + (i / steps) * height;
      const wpmVal = Math.round(maxWpm - (i / steps) * (maxWpm - minWpm));

      ctx.beginPath();
      ctx.moveTo(padding.left, y);
      ctx.lineTo(padding.left + width, y);
      ctx.stroke();

      ctx.fillText(`${wpmVal} wpm`, padding.left - 8, y + 4);
    }

    // X Axis bottom baseline
    ctx.beginPath();
    ctx.moveTo(padding.left, padding.top + height);
    ctx.lineTo(padding.left + width, padding.top + height);
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.15)';
    ctx.stroke();

    // X Axis Label
    ctx.textAlign = 'center';
    ctx.fillStyle = 'rgba(255, 255, 255, 0.4)';
    ctx.fillText('Test Chronology (Oldest → Latest)', padding.left + width / 2, padding.top + height + 30);
  }

  function drawTargetLine(ctx, padding, width, height, targetAcc, minWpm, maxWpm) {
    // Show a clean target accuracy note in top right
    ctx.font = '12px sans-serif';
    ctx.fillStyle = '#4fd1c5';
    ctx.textAlign = 'right';
    ctx.fillText(`Target Accuracy: ${targetAcc}%`, padding.left + width, padding.top - 10);
  }

  function drawLine(ctx, pts, color, lineWidth, dash = []) {
    if (pts.length < 1) return;
    ctx.save();
    ctx.beginPath();
    ctx.setLineDash(dash);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';

    ctx.moveTo(pts[0].x, pts[0].y);
    for (let i = 1; i < pts.length; i++) {
      ctx.lineTo(pts[i].x, pts[i].y);
    }
    ctx.stroke();
    ctx.restore();
  }

  function renderEmptyState(ctx, width, height) {
    ctx.fillStyle = 'rgba(255, 255, 255, 0.35)';
    ctx.font = '14px sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('No tests logged yet for this filter.', width / 2, height / 2 - 10);
    ctx.font = '12px sans-serif';
    ctx.fillText('Log your first Monkeytype test or click "Load Sample Data" to see your charts!', width / 2, height / 2 + 15);
  }

  function handleMouseMove(e) {
    if (!activeCanvas || !activeCanvas._points || !activeTooltip) return;
    const rect = activeCanvas.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    let closest = null;
    let minDistance = 25; // threshold px

    activeCanvas._points.forEach(p => {
      const distWpm = Math.hypot(p.x - mouseX, p.yWpm - mouseY);
      const distAcc = Math.hypot(p.x - mouseX, p.yAcc - mouseY);
      const dist = Math.min(distWpm, distAcc);
      if (dist < minDistance) {
        minDistance = dist;
        closest = p;
      }
    });

    if (closest) {
      hoveredPoint = closest;
      const d = closest.data;
      const missedSummary = d.missedWords && d.missedWords.length > 0 
        ? `Missed: ${d.missedWords.slice(0, 3).join(', ')}${d.missedWords.length > 3 ? '...' : ''}` 
        : (d.missedChars && d.missedChars.length > 0 ? `Keys: ${d.missedChars.join(', ')}` : 'Clean run');

      activeTooltip.innerHTML = `
        <div style="font-weight: 600; color: #e2b714; margin-bottom: 2px;">${d.wpm} WPM &bull; ${d.accuracy}% ACC</div>
        <div style="color: #94a3b8; font-size: 11px;">Mode: ${d.mode.toUpperCase()} &bull; ${d.dateStr || ''}</div>
        <div style="color: #cbd5e1; font-size: 11px; margin-top: 3px;">${missedSummary}</div>
      `;
      activeTooltip.style.display = 'block';
      activeTooltip.style.left = `${rect.left + closest.x}px`;
      activeTooltip.style.top = `${rect.top + closest.yWpm - 65}px`;
    } else {
      handleMouseLeave();
    }
  }

  function handleMouseLeave() {
    if (activeTooltip) {
      activeTooltip.style.display = 'none';
    }
    hoveredPoint = null;
  }

  return {
    init,
    render,
    setFilter
  };
})();

if (typeof module !== 'undefined' && module.exports) {
  module.exports = ChartRenderer;
}
