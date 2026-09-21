(() => {
  const root = document.documentElement;
  const reducedMotion = matchMedia('(prefers-reduced-motion: reduce)');
  const staticPaletteName = 'turp';
  const animationDuration = 320;

  const palettes = {
    turp: { backgroundStart: '#712828', backgroundEnd: '#b93838', leafStart: '#d0a390', leafMid: '#e1c5b8', leafEnd: '#f0ddd5', bulbStart: '#f3e4de', bulbMid: '#e6cbc0', bulbEnd: '#cc927a' },
    arbor: { backgroundStart: '#e7f4ea', backgroundEnd: '#b5f1cc', leafStart: '#286448', leafMid: '#3d6472', leafEnd: '#0d5033', bulbStart: '#f4fbff', bulbMid: '#c1eafb', bulbEnd: '#3d6472' },
    system: { backgroundStart: '#293b52', backgroundEnd: '#67507e', leafStart: '#a9d4ff', leafMid: '#c7dfff', leafEnd: '#e8ddff', bulbStart: '#ffe9e4', bulbMid: '#ffb4a9', bulbEnd: '#d47770' },
    graphite: { backgroundStart: '#162234', backgroundEnd: '#425f86', leafStart: '#a9c7f8', leafMid: '#c8dafa', leafEnd: '#e7f0ff', bulbStart: '#fff4ed', bulbMid: '#e5bfa6', bulbEnd: '#b88769' },
    ocean: { backgroundStart: '#00363f', backgroundEnd: '#00677a', leafStart: '#54d6f2', leafMid: '#9be8f8', leafEnd: '#d5f7ff', bulbStart: '#f3f5ff', bulbMid: '#bec6ea', bulbEnd: '#8e99cd' },
    violet: { backgroundStart: '#2e1d4f', backgroundEnd: '#67508f', leafStart: '#d1bcff', leafMid: '#e1d4ff', leafEnd: '#f0e8ff', bulbStart: '#ffedf3', bulbMid: '#efb8c8', bulbEnd: '#c9829e' },
    sunset: { backgroundStart: '#5c1a07', backgroundEnd: '#9b4425', leafStart: '#ffb59c', leafMid: '#ffd0bf', leafEnd: '#ffede7', bulbStart: '#fff6dd', bulbMid: '#d7c58d', bulbEnd: '#ac995e' },
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
    const palette = palette;
    const svg = `<svg width="512" height="512" viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg"><defs><linearGradient id="bg" x1="11.896681" y1="98.103348" x2="99.103363" y2="10.896666" gradientUnits="userSpaceOnUse"><stop stop-color="${palette.backgroundStart}"/><stop offset="1" stop-color="${palette.backgroundEnd}"/></linearGradient><linearGradient id="leaf" x1="48.3456" y1="48.7232" x2="23.54455" y2="15.18682" gradientUnits="userSpaceOnUse"><stop stop-color="${palette.leafStart}"/><stop offset=".55" stop-color="${palette.leafMid}"/><stop offset="1" stop-color="${palette.leafEnd}"/></linearGradient><radialGradient id="bulb" cx="88.18859" cy="42.48624" r="92" gradientUnits="userSpaceOnUse"><stop stop-color="${palette.bulbStart}"/><stop offset=".42" stop-color="${palette.bulbMid}"/><stop offset="1" stop-color="${palette.bulbEnd}"/></radialGradient><filter id="soft-shadow" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="3.2"/></filter><clipPath id="clip"><rect width="108" height="108" rx="24"/></clipPath></defs><rect width="108" height="108" rx="24" fill="url(#bg)"/><g clip-path="url(#clip)"><path d="M35.482407,13.107376 a6.328125,6.328125 0,0 0,-6.621048,6.019547 c-0.408164,8.599098 0.530967,15.51428 1.941284,21.070266 c-6.729722,-1.773193 -14.868955,-2.57127 -23.310654,0.0997 a6.328125,6.328125 0,0 0,-4.1248168,7.941468 a6.328125,6.328125 0,0 0,7.9414678,4.124816 c10.399942,-3.290561 22.769933,2.310512 27.032547,4.520325 -9.260595,9.67514 -8.267305,18.917014 -1.302292,28.203415 16.604184,22.138307 34.981896,12.412517 45.540664,16.341087 2.763515,1.02822 2.659701,1.11499 2.130799,-1.785985 C82.68966,88.558844 95.480265,72.165557 76.588028,51.945007 68.591593,43.386343 59.57615,40.822081 48.289032,48.497498 a6.3287578,6.3287578 0,0 0,-0.423935,-0.485733 c0,0 -1.342276,-1.219602 -3.17189,-5.695313 C42.863593,37.84074 40.989028,30.57801 41.504013,19.728424 a6.328125,6.328125 0,0 0,-6.021606,-6.621048 z" transform="matrix(.89398215 .05493023 -.05493023 .89398215 9.9515303 2.2901164)" fill="#000" opacity=".42" filter="url(#soft-shadow)"/><path d="M45.355735,12.06325 a5.6678999,5.6678999 0,0 0,-6.249794,5.017655 c-0.837242,7.66502 -0.377422,13.898704 0.578185,18.943126 c-5.918849,-1.954868 -13.151238,-3.1155 -20.844684,-1.191404 a5.6678999,5.6678999 0,0 0,-4.1239,6.872888 a5.6678999,5.6678999 0,0 0,6.872889,4.123899 c11.65815,-2.915649 25.241827,6.358492 25.241827,6.358492 a5.6684667,5.6684667 0,0 0,7.678189,-8.240646 c0,0 -1.132965,-1.164033 -2.522755,-5.26574 c-1.38979,-4.101708 -2.666914,-10.697321 -1.610556,-20.368369 A5.6678999,5.6678999 0,0 0,45.355735,12.06325 Z" fill="url(#leaf)"/><path d="M54,24.484818 C28.27233,23.675401 18.764299,35.224655 17.750061,54 c-2.097335,38.825409 26.471295,44.93682 34.296873,58.69627 c2.048165,3.60122 1.857976,3.60101 3.906337,-0.00036 C63.779142,98.936713 92.347256,92.82507 90.249939,54 C89.235701,35.224655 79.72767,23.675401 54,24.484818 Z" transform="matrix(.51278267 -.37870208 .37870208 .51278267 13.241359 55.528951)" fill="url(#bulb)"/></g></svg>`;
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
