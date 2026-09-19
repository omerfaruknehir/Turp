(() => {
  const root = document.documentElement;
  const reducedMotion = matchMedia('(prefers-reduced-motion: reduce)');
  const staticPaletteName = 'turp';
  const animationDuration = 320;

  const palettes = {
    turp: {
      backgroundStart: '#24171c',
      backgroundEnd: '#5a2335',
      markStart: '#ffb0c5',
      markEnd: '#f06a91',
      leaf: '#c3c8c3',
      secondStroke: '#f7e7ec',
    },
    arbor: {
      backgroundStart: '#e7f4ea',
      backgroundEnd: '#b5f1cc',
      markStart: '#286448',
      markEnd: '#0d5033',
      leaf: '#3d6472',
      secondStroke: '#c1eafb',
    },
    system: {
      backgroundStart: '#293b52',
      backgroundEnd: '#67507e',
      markStart: '#a9d4ff',
      markEnd: '#e8ddff',
      leaf: '#ffb4a9',
      secondStroke: '#fff8ff',
    },
    graphite: {
      backgroundStart: '#162234',
      backgroundEnd: '#425f86',
      markStart: '#a9c7f8',
      markEnd: '#e7f0ff',
      leaf: '#e5bfa6',
      secondStroke: '#f7f9ff',
    },
    ocean: {
      backgroundStart: '#00363f',
      backgroundEnd: '#00677a',
      markStart: '#54d6f2',
      markEnd: '#d5f7ff',
      leaf: '#bec6ea',
      secondStroke: '#f2fdff',
    },
    violet: {
      backgroundStart: '#2e1d4f',
      backgroundEnd: '#67508f',
      markStart: '#d1bcff',
      markEnd: '#f0e8ff',
      leaf: '#efb8c8',
      secondStroke: '#fff8ff',
    },
    sunset: {
      backgroundStart: '#5c1a07',
      backgroundEnd: '#9b4425',
      markStart: '#ffb59c',
      markEnd: '#ffede7',
      leaf: '#d7c58d',
      secondStroke: '#fff8f6',
    },
  };

  const primaryToPalette = new Map([
    ['#9f244a', 'turp'],
    ['#ffb0c5', 'turp'],
    ['#286448', 'arbor'],
    ['#99d5b1', 'arbor'],
    ['#425f86', 'graphite'],
    ['#a9c7f8', 'graphite'],
    ['#00677a', 'ocean'],
    ['#54d6f2', 'ocean'],
    ['#67508f', 'violet'],
    ['#d1bcff', 'violet'],
    ['#9b4425', 'sunset'],
    ['#ffb59c', 'sunset'],
  ]);

  const clamp = (value, min = 0, max = 1) => Math.min(max, Math.max(min, value));

  function normalizeHex(value, fallback = '#000000') {
    const match = String(value || '').trim().match(/^#?([0-9a-f]{6})$/i);
    return match ? `#${match[1].toLowerCase()}` : fallback;
  }

  function hexToRgb(hex) {
    const value = Number.parseInt(normalizeHex(hex).slice(1), 16);
    return {
      r: (value >> 16) & 255,
      g: (value >> 8) & 255,
      b: value & 255,
    };
  }

  function rgbToHex({ r, g, b }) {
    const component = (value) => Math.round(clamp(value, 0, 255)).toString(16).padStart(2, '0');
    return `#${component(r)}${component(g)}${component(b)}`;
  }

  function mixColor(from, to, progress) {
    const start = hexToRgb(from);
    const end = hexToRgb(to);
    return rgbToHex({
      r: start.r + ((end.r - start.r) * progress),
      g: start.g + ((end.g - start.g) * progress),
      b: start.b + ((end.b - start.b) * progress),
    });
  }

  function mixPalette(from, to, progress) {
    const amount = clamp(progress);
    return Object.fromEntries(
      Object.keys(from).map((key) => [key, mixColor(from[key], to[key], amount)]),
    );
  }

  function paletteNameForScheme() {
    const scheme = root.dataset.schemePreference || 'turp';
    if (scheme !== 'app') return palettes[scheme] ? scheme : staticPaletteName;
    const primary = normalizeHex(getComputedStyle(root).getPropertyValue('--primary'));
    return primaryToPalette.get(primary) || 'system';
  }

  function targetPalette() {
    return root.dataset.dynamicIcon === 'on'
      ? palettes[paletteNameForScheme()]
      : palettes[staticPaletteName];
  }

  function logoDataUrl(palette) {
    const svg = `<svg width="512" height="512" viewBox="0 0 1536 1536" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bg" x1="220" y1="120" x2="1340" y2="1420" gradientUnits="userSpaceOnUse">
      <stop stop-color="${palette.backgroundStart}"/>
      <stop offset="1" stop-color="${palette.backgroundEnd}"/>
    </linearGradient>
    <linearGradient id="leaf" x1="720" y1="220" x2="860" y2="680" gradientUnits="userSpaceOnUse">
      <stop stop-color="${palette.markStart}"/>
      <stop offset="1" stop-color="${palette.markEnd}"/>
    </linearGradient>
  </defs>
  <rect width="1536" height="1536" rx="350" fill="url(#bg)"/>
  <path d="M774 649C784 604 779 559 748 520C715 480 662 455 643 408C621 354 629 289 650 242C664 210 684 206 708 216C754 236 805 280 840 337C866 379 874 430 856 489C842 540 818 595 795 648Z" fill="url(#leaf)"/>
  <path d="M827 674C874 624 927 570 1000 519C1060 477 1112 454 1155 466C1210 481 1260 515 1282 548C1304 579 1286 617 1260 649C1215 705 1168 732 1115 726C1078 722 1049 701 1015 684C973 663 935 656 900 670C870 682 846 690 827 674Z" fill="url(#leaf)"/>
  <path d="M817 661C801 597 802 523 805 447C809 372 840 315 890 264C936 216 982 178 1017 176C1057 174 1090 198 1111 237C1134 281 1141 342 1137 393C1133 444 1115 489 1085 518C1054 547 1012 563 969 576C912 594 858 616 817 661Z" fill="url(#leaf)"/>
  <path d="M734 681C686 657 633 648 586 654C519 663 463 699 428 747C399 786 389 828 400 875C406 903 421 936 440 970C458 1004 469 1035 469 1070C470 1122 449 1173 414 1219C390 1250 365 1278 378 1289C386 1296 414 1267 449 1239C491 1205 532 1181 574 1169C618 1157 669 1167 718 1161C771 1154 820 1131 858 1091C895 1053 918 1005 923 953C930 891 916 836 884 791C846 738 790 705 734 681Z" fill="${palette.leaf}"/>
  <path d="M440 989C471 1005 500 1024 531 1048C570 1078 601 1114 631 1158C600 1159 570 1166 541 1178C499 1195 462 1223 428 1253C402 1276 382 1295 376 1290C369 1284 393 1253 415 1225C451 1179 470 1128 468 1072C468 1037 457 1006 440 989Z" fill="${palette.secondStroke}"/>
</svg>`;
    return `data:image/svg+xml,${encodeURIComponent(svg)}`;
  }

  function installDialogLogoPreview() {
    const icon = document.querySelector('.appearance-switch-row__icon');
    if (!icon || document.querySelector('.appearance-switch-row__logo')) return;
    const source = document.querySelector('[data-turp-logo]')?.getAttribute('src')
      || '/assets/images/turp-logo.svg';
    const image = document.createElement('img');
    image.className = 'appearance-switch-row__logo';
    image.setAttribute('src', source);
    image.setAttribute('alt', '');
    image.setAttribute('aria-hidden', 'true');
    image.dataset.turpLogo = '';
    icon.replaceWith(image);
  }

  let currentPalette = targetPalette();
  let animationFrame = 0;
  let stateFrame = 0;
  let isPreviewing = false;

  function renderPalette(palette) {
    currentPalette = palette;
    const source = logoDataUrl(palette);
    document.querySelectorAll('[data-turp-logo]').forEach((image) => {
      image.setAttribute('src', source);
    });
  }

  function animateTo(palette, duration = animationDuration) {
    cancelAnimationFrame(animationFrame);
    const start = currentPalette;
    if (reducedMotion.matches || duration <= 0) {
      renderPalette(palette);
      return;
    }

    const startedAt = performance.now();
    renderPalette(start);
    const tick = (now) => {
      const linear = clamp((now - startedAt) / duration);
      const eased = 1 - Math.pow(1 - linear, 3);
      renderPalette(mixPalette(start, palette, eased));
      if (linear < 1) animationFrame = requestAnimationFrame(tick);
    };
    animationFrame = requestAnimationFrame(tick);
  }

  function scheduleStateSync() {
    if (isPreviewing || stateFrame) return;
    stateFrame = requestAnimationFrame(() => {
      stateFrame = 0;
      animateTo(targetPalette());
    });
  }

  installDialogLogoPreview();
  renderPalette(currentPalette);

  const observer = new MutationObserver(scheduleStateSync);
  observer.observe(root, {
    attributes: true,
    attributeFilter: ['data-dynamic-icon', 'data-scheme-preference', 'style'],
  });

  document.addEventListener('turp-switch-preview', (event) => {
    const control = event.detail?.control;
    if (!(control instanceof Element) || !control.matches('[data-dynamic-icon-toggle]')) return;
    isPreviewing = true;
    cancelAnimationFrame(animationFrame);
    const progress = clamp(Number(event.detail.progress));
    renderPalette(mixPalette(palettes[staticPaletteName], palettes[paletteNameForScheme()], progress));
  });

  document.addEventListener('turp-switch-preview-end', (event) => {
    const control = event.detail?.control;
    if (!(control instanceof Element) || !control.matches('[data-dynamic-icon-toggle]')) return;
    isPreviewing = false;
    animateTo(targetPalette(), 180);
  });
})();
