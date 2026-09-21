(() => {
  const root = document.documentElement;
  const media = matchMedia('(prefers-color-scheme: dark)');
  const reducedMotion = matchMedia('(prefers-reduced-motion: reduce)');
  const themeState = window.TurpPageTheme || {
    appTheme: null,
    colorVariables: [],
    fixedColors: () => ({}),
    supportedThemes: ['app', 'dark', 'light', 'system'],
    supportedSchemes: ['app', 'turp', 'arbor'],
    queryKeys: ['theme', 'scheme', 'dynamicLogo'],
  };
  const storedDynamicIcon = localStorage.getItem('turp-dynamic-icon');
  let dynamicIconEnabled = storedDynamicIcon !== null
    ? storedDynamicIcon === '1'
    : Boolean(themeState.appTheme?.dynamicLogo);

  const appIconPalettes = {
    turp: { backgroundStart: '#712828', backgroundEnd: '#b93838', leafStart: '#d0a390', leafMid: '#e1c5b8', leafEnd: '#f0ddd5', bulbStart: '#f3e4de', bulbMid: '#e6cbc0', bulbEnd: '#cc927a' },
    arbor: { backgroundStart: '#e7f4ea', backgroundEnd: '#b5f1cc', leafStart: '#286448', leafMid: '#3d6472', leafEnd: '#0d5033', bulbStart: '#f4fbff', bulbMid: '#c1eafb', bulbEnd: '#3d6472' },
    system: { backgroundStart: '#293b52', backgroundEnd: '#67507e', leafStart: '#a9d4ff', leafMid: '#c7dfff', leafEnd: '#e8ddff', bulbStart: '#ffe9e4', bulbMid: '#ffb4a9', bulbEnd: '#d47770' },
    graphite: { backgroundStart: '#162234', backgroundEnd: '#425f86', leafStart: '#a9c7f8', leafMid: '#c8dafa', leafEnd: '#e7f0ff', bulbStart: '#fff4ed', bulbMid: '#e5bfa6', bulbEnd: '#b88769' },
    ocean: { backgroundStart: '#00363f', backgroundEnd: '#00677a', leafStart: '#54d6f2', leafMid: '#9be8f8', leafEnd: '#d5f7ff', bulbStart: '#f3f5ff', bulbMid: '#bec6ea', bulbEnd: '#8e99cd' },
    violet: { backgroundStart: '#2e1d4f', backgroundEnd: '#67508f', leafStart: '#d1bcff', leafMid: '#e1d4ff', leafEnd: '#f0e8ff', bulbStart: '#ffedf3', bulbMid: '#efb8c8', bulbEnd: '#c9829e' },
    sunset: { backgroundStart: '#5c1a07', backgroundEnd: '#9b4425', leafStart: '#ffb59c', leafMid: '#ffd0bf', leafEnd: '#ffede7', bulbStart: '#fff6dd', bulbMid: '#d7c58d', bulbEnd: '#ac995e' },
  };

  const appPrimaryToIconPalette = new Map([
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

  function activeColor(variable, fallback) {
    return getComputedStyle(root).getPropertyValue(variable).trim() || fallback || '';
  }

  function normalizeHex(value, fallback = '') {
    const match = String(value || '').trim().match(/^#?([0-9a-f]{6})$/i);
    return match ? `#${match[1].toLowerCase()}` : fallback;
  }

  function iconPaletteFor(schemePreference) {
    if (schemePreference !== 'app') {
      return appIconPalettes[schemePreference] ? schemePreference : 'turp';
    }
    const appPrimary = normalizeHex(themeState.appTheme?.colors?.['--primary']);
    return appPrimaryToIconPalette.get(appPrimary) || 'system';
  }

  function dynamicLogoDataUrl(schemePreference) {
    if (!dynamicIconEnabled) return null;

    const paletteName = iconPaletteFor(schemePreference);
    const palette = appIconPalettes[paletteName];
    const svg = `<svg width="512" height="512" viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg"><defs><linearGradient id="bg" x1="11.896681" y1="98.103348" x2="99.103363" y2="10.896666" gradientUnits="userSpaceOnUse"><stop stop-color="${palette.backgroundStart}"/><stop offset="1" stop-color="${palette.backgroundEnd}"/></linearGradient><linearGradient id="leaf" x1="48.3456" y1="48.7232" x2="23.54455" y2="15.18682" gradientUnits="userSpaceOnUse"><stop stop-color="${palette.leafStart}"/><stop offset=".55" stop-color="${palette.leafMid}"/><stop offset="1" stop-color="${palette.leafEnd}"/></linearGradient><radialGradient id="bulb" cx="88.18859" cy="42.48624" r="92" gradientUnits="userSpaceOnUse"><stop stop-color="${palette.bulbStart}"/><stop offset=".42" stop-color="${palette.bulbMid}"/><stop offset="1" stop-color="${palette.bulbEnd}"/></radialGradient><filter id="soft-shadow" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="3.2"/></filter><clipPath id="clip"><rect width="108" height="108" rx="24"/></clipPath></defs><rect width="108" height="108" rx="24" fill="url(#bg)"/><g clip-path="url(#clip)"><path d="M35.482407,13.107376 a6.328125,6.328125 0,0 0,-6.621048,6.019547 c-0.408164,8.599098 0.530967,15.51428 1.941284,21.070266 c-6.729722,-1.773193 -14.868955,-2.57127 -23.310654,0.0997 a6.328125,6.328125 0,0 0,-4.1248168,7.941468 a6.328125,6.328125 0,0 0,7.9414678,4.124816 c10.399942,-3.290561 22.769933,2.310512 27.032547,4.520325 -9.260595,9.67514 -8.267305,18.917014 -1.302292,28.203415 16.604184,22.138307 34.981896,12.412517 45.540664,16.341087 2.763515,1.02822 2.659701,1.11499 2.130799,-1.785985 C82.68966,88.558844 95.480265,72.165557 76.588028,51.945007 68.591593,43.386343 59.57615,40.822081 48.289032,48.497498 a6.3287578,6.3287578 0,0 0,-0.423935,-0.485733 c0,0 -1.342276,-1.219602 -3.17189,-5.695313 C42.863593,37.84074 40.989028,30.57801 41.504013,19.728424 a6.328125,6.328125 0,0 0,-6.021606,-6.621048 z" transform="matrix(.89398215 .05493023 -.05493023 .89398215 9.9515303 2.2901164)" fill="#000" opacity=".42" filter="url(#soft-shadow)"/><path d="M45.355735,12.06325 a5.6678999,5.6678999 0,0 0,-6.249794,5.017655 c-0.837242,7.66502 -0.377422,13.898704 0.578185,18.943126 c-5.918849,-1.954868 -13.151238,-3.1155 -20.844684,-1.191404 a5.6678999,5.6678999 0,0 0,-4.1239,6.872888 a5.6678999,5.6678999 0,0 0,6.872889,4.123899 c11.65815,-2.915649 25.241827,6.358492 25.241827,6.358492 a5.6684667,5.6684667 0,0 0,7.678189,-8.240646 c0,0 -1.132965,-1.164033 -2.522755,-5.26574 c-1.38979,-4.101708 -2.666914,-10.697321 -1.610556,-20.368369 A5.6678999,5.6678999 0,0 0,45.355735,12.06325 Z" fill="url(#leaf)"/><path d="M54,24.484818 C28.27233,23.675401 18.764299,35.224655 17.750061,54 c-2.097335,38.825409 26.471295,44.93682 34.296873,58.69627 c2.048165,3.60122 1.857976,3.60101 3.906337,-0.00036 C63.779142,98.936713 92.347256,92.82507 90.249939,54 C89.235701,35.224655 79.72767,23.675401 54,24.484818 Z" transform="matrix(.51278267 -.37870208 .37870208 .51278267 13.241359 55.528951)" fill="url(#bulb)"/></g></svg>`;
    return `data:image/svg+xml,${encodeURIComponent(svg)}`;
  }

  function syncBrandLogo(schemePreference) {
    const dynamicSource = dynamicLogoDataUrl(schemePreference);
    root.dataset.brandLogo = dynamicSource ? iconPaletteFor(schemePreference) : 'static';
    document.querySelectorAll('[data-turp-logo]').forEach((image) => {
      image.dataset.staticSrc ||= image.getAttribute('src') || '';
      image.setAttribute('src', dynamicSource || image.dataset.staticSrc);
    });
    document.querySelectorAll('link[data-turp-favicon]').forEach((icon) => {
      icon.dataset.staticHref ||= icon.getAttribute('href') || '';
      const desired = dynamicSource || icon.dataset.staticHref;
      if (icon.getAttribute('href') === desired) return;
      const replacement = icon.cloneNode(true);
      replacement.setAttribute('href', desired);
      if (dynamicSource) replacement.setAttribute('type', 'image/svg+xml');
      icon.replaceWith(replacement);
    });
  }

  function syncDynamicIconControls() {
    root.dataset.dynamicIcon = dynamicIconEnabled ? 'on' : 'off';
    document.querySelectorAll('[data-dynamic-icon-toggle]').forEach((control) => {
      control.setAttribute('aria-checked', String(dynamicIconEnabled));
      control.classList.toggle('is-checked', dynamicIconEnabled);
    });
  }

  function syncAppearanceLinks() {
    document.querySelectorAll('a[href]').forEach((anchor) => {
      const target = new URL(anchor.getAttribute('href'), location.href);
      if (target.origin !== location.origin) return;
      themeState.queryKeys.forEach((key) => target.searchParams.delete(key));
      anchor.href = target.href;
    });
  }

  function storedFixedScheme() {
    const stored = localStorage.getItem('turp-scheme');
    return themeState.supportedSchemes.includes(stored) && stored !== 'app'
      ? stored
      : 'turp';
  }

  function resolvedTheme(themePreference) {
    if (themePreference === 'app' && themeState.appTheme) {
      return themeState.appTheme.dark ? 'dark' : 'light';
    }
    if (themePreference === 'system') return media.matches ? 'dark' : 'light';
    return themePreference === 'light' ? 'light' : 'dark';
  }

  function colorsFor(themePreference, schemePreference) {
    if (schemePreference === 'app' && themeState.appTheme) {
      return {
        ...themeState.fixedColors('turp', themeState.appTheme.dark),
        ...themeState.appTheme.colors,
        '--focus': themeState.appTheme.colors['--primary'],
      };
    }
    return themeState.fixedColors(
      schemePreference,
      resolvedTheme(themePreference) === 'dark',
    );
  }

  function currentThemePreference() {
    const value = root.dataset.themePreference;
    return themeState.supportedThemes.includes(value) ? value : 'dark';
  }

  function currentSchemePreference() {
    const value = root.dataset.schemePreference;
    return themeState.supportedSchemes.includes(value) ? value : 'turp';
  }

  function cleanAppearanceUrl() {
    const url = new URL(location.href);
    let changed = false;
    themeState.queryKeys.forEach((key) => {
      if (!url.searchParams.has(key)) return;
      url.searchParams.delete(key);
      changed = true;
    });
    if (changed) history.replaceState(null, '', url);
  }

  function applyAppearance(themePreference, schemePreference, persist = true) {
    if (themePreference === 'app' && !themeState.appTheme) themePreference = 'dark';
    if (schemePreference === 'app' && !themeState.appTheme) schemePreference = storedFixedScheme();
    if (schemePreference === 'app') themePreference = 'app';

    const resolved = resolvedTheme(themePreference);
    const colors = colorsFor(themePreference, schemePreference);
    themeState.colorVariables.forEach((name) => root.style.removeProperty(name));
    Object.entries(colors).forEach(([name, value]) => root.style.setProperty(name, value));

    root.dataset.theme = resolved;
    root.dataset.themePreference = themePreference;
    root.dataset.schemePreference = schemePreference;
    root.style.colorScheme = resolved;
    syncBrandLogo(schemePreference);
    syncDynamicIconControls();

    document.querySelector('meta[name="theme-color"]')?.setAttribute(
      'content',
      activeColor('--background'),
    );
    document.querySelectorAll('[data-theme-choice]').forEach((button) => {
      const selected = button.dataset.themeChoice === themePreference;
      button.setAttribute('aria-checked', String(selected));
      button.classList.toggle('is-selected', selected);
    });
    document.querySelectorAll('[data-scheme-choice]').forEach((button) => {
      const selected = button.dataset.schemeChoice === schemePreference;
      button.setAttribute('aria-checked', String(selected));
      button.classList.toggle('is-selected', selected);
    });

    if (persist) {
      localStorage.setItem('turp-theme', themePreference);
      localStorage.setItem('turp-scheme', schemePreference);
    }

    cleanAppearanceUrl();
    syncAppearanceLinks();
  }

  function setTheme(themePreference) {
    let schemePreference = currentSchemePreference();
    if (themePreference !== 'app' && schemePreference === 'app') {
      schemePreference = storedFixedScheme();
    }
    applyAppearance(themePreference, schemePreference);
  }

  function setScheme(schemePreference) {
    const themePreference = schemePreference === 'app'
      ? 'app'
      : currentThemePreference();
    applyAppearance(themePreference, schemePreference);
  }

  function setDynamicIcon(enabled) {
    dynamicIconEnabled = Boolean(enabled);
    localStorage.setItem('turp-dynamic-icon', dynamicIconEnabled ? '1' : '0');
    const themePreference = currentThemePreference();
    const schemePreference = currentSchemePreference();
    syncBrandLogo(schemePreference);
    syncDynamicIconControls();
    cleanAppearanceUrl();
    syncAppearanceLinks();
  }

  function renderAppearanceControls() {
    const rail = `
      <div class="appearance-launcher">
        <span class="appearance-launcher__label">Theme</span>
        <button class="icon-button" type="button" data-theme-settings aria-label="Open color scheme settings">
          <span class="material-symbols-rounded" aria-hidden="true">palette</span>
        </button>
      </div>
      <div class="theme-selector rail-theme-selector" role="radiogroup" aria-label="Theme">
        ${themeSegmentButton('app', 'phone_android', 'App', true)}
        ${themeSegmentButton('system', 'brightness_auto', 'Auto')}
        ${themeSegmentButton('light', 'light_mode', 'Light')}
        ${themeSegmentButton('dark', 'dark_mode', 'Dark')}
      </div>`;

    const dialog = `
      <div class="dialog-heading">
        <div>
          <h2 id="appearance-title">Appearance</h2>
          <p>Customize this site.</p>
        </div>
        <button class="icon-button" type="button" data-theme-close aria-label="Close appearance settings">
          <span class="material-symbols-rounded" aria-hidden="true">close</span>
        </button>
      </div>
      <section class="appearance-dialog__section" aria-labelledby="theme-section-title">
        <h3 class="appearance-dialog__section-title" id="theme-section-title">Theme</h3>
        <div class="theme-selector dialog-theme-selector" role="radiogroup" aria-label="Theme">
          ${themeSegmentButton('app', 'phone_android', 'App', true)}
          ${themeSegmentButton('system', 'brightness_auto', 'Auto')}
          ${themeSegmentButton('light', 'light_mode', 'Light')}
          ${themeSegmentButton('dark', 'dark_mode', 'Dark')}
        </div>
      </section>
      <section class="appearance-dialog__section" aria-labelledby="scheme-section-title">
        <h3 class="appearance-dialog__section-title" id="scheme-section-title">Color scheme</h3>
        <div class="dialog-scheme-grid" role="radiogroup" aria-label="Color scheme">
          ${schemeButton('app', 'App', true, true)}
          ${schemeButton('turp', 'Turp', false, true)}
          ${schemeButton('arbor', 'Arbor', false, true)}
          ${schemeButton('graphite', 'Graphite', false, true)}
          ${schemeButton('ocean', 'Ocean', false, true)}
          ${schemeButton('violet', 'Violet', false, true)}
          ${schemeButton('sunset', 'Sunset', false, true)}
        </div>
      </section>
      <section class="appearance-dialog__section appearance-dialog__switch-section" aria-labelledby="icon-section-title">
        <h3 class="appearance-dialog__section-title" id="icon-section-title">Brand icon</h3>
        <div class="appearance-switch-row">
          <span class="material-symbols-rounded appearance-switch-row__icon" aria-hidden="true">gradient</span>
          <span class="appearance-switch-row__copy">
            <strong>Dynamic icon</strong>
            <small>Use the same icon variant as Turp for the selected color scheme.</small>
          </span>
          <button class="material-switch" type="button" role="switch" data-dynamic-icon-toggle aria-label="Use dynamic Turp icon" aria-checked="false">
            <span class="material-switch__handle"></span>
          </button>
        </div>
      </section>`;

    document.querySelectorAll('.rail-appearance').forEach((container) => {
      container.innerHTML = rail;
    });
    document.querySelectorAll('[data-theme-dialog]').forEach((container) => {
      container.innerHTML = dialog;
    });
  }

  function themeSegmentButton(value, icon, label, hidden = false) {
    return `<button class="theme-selector__choice" type="button" data-theme-choice="${value}" role="radio"${hidden ? ' hidden' : ''}>
      <span class="material-symbols-rounded" aria-hidden="true">${icon}</span>
      <span class="theme-selector__label">${label}</span>
    </button>`;
  }

  function schemeButton(value, label, hidden = false, dialog = false) {
    return `<button class="palette-choice${dialog ? ' palette-choice--dialog' : ''}" type="button" data-scheme-choice="${value}" role="radio"${hidden ? ' hidden' : ''}>
      <span class="palette-choice__swatches" aria-hidden="true"><span></span><span></span><span></span></span>
      <span class="palette-choice__label">${label}</span>
      ${dialog ? '<span class="material-symbols-rounded palette-choice__check" aria-hidden="true">check</span>' : ''}
    </button>`;
  }

  renderAppearanceControls();

  const menuButton = document.querySelector('[data-menu-toggle]');
  const dismissMenu = () => {
    document.body.classList.remove('menu-open');
    menuButton?.setAttribute('aria-expanded', 'false');
  };
  menuButton?.addEventListener('click', () => {
    const open = document.body.classList.toggle('menu-open');
    menuButton.setAttribute('aria-expanded', String(open));
  });
  document.querySelector('[data-menu-dismiss]')?.addEventListener('click', dismissMenu);
  document.querySelectorAll('.site-rail a').forEach((link) => link.addEventListener('click', dismissMenu));
  addEventListener('keydown', (event) => {
    if (event.key === 'Escape') dismissMenu();
  });

  const dialog = document.querySelector('[data-theme-dialog]');
  document.querySelectorAll('[data-theme-settings]').forEach((button) => {
    button.addEventListener('click', () => {
      dismissMenu();
      dialog?.showModal();
    });
  });
  document.querySelector('[data-theme-close]')?.addEventListener('click', () => dialog?.close());
  dialog?.addEventListener('click', (event) => {
    if (event.target === dialog) dialog.close();
  });

  document.querySelectorAll('[data-theme-choice]').forEach((button) => {
    if (button.dataset.themeChoice === 'app') button.hidden = !themeState.appTheme;
    button.addEventListener('click', () => setTheme(button.dataset.themeChoice));
  });
  document.querySelectorAll('[data-scheme-choice]').forEach((button) => {
    if (button.dataset.schemeChoice === 'app') button.hidden = !themeState.appTheme;
    button.addEventListener('click', () => setScheme(button.dataset.schemeChoice));
  });
  document.querySelectorAll('[data-dynamic-icon-toggle]').forEach((control) => {
    control.addEventListener('click', () => setDynamicIcon(!dynamicIconEnabled));
  });

  window.addEventListener("load", () => {
    document.body.classList.remove("preload");
  });

  media.addEventListener('change', () => {
    if (currentThemePreference() === 'system') {
      applyAppearance('system', currentSchemePreference(), false);
    }
  });

  function setupTitleCollapse() {
    const scroller = document.querySelector('.page-with-app-bar');
    if (!scroller) return;
    const collapseDistance = Number.parseFloat(
      getComputedStyle(root).getPropertyValue('--turp-app-bar-collapse-distance'),
    ) || 88;
    const expandedTitleShift = 58;
    const expandedTitleScale = Number.parseFloat(
      getComputedStyle(scroller).getPropertyValue('--turp-title-expanded-scale'),
    ) || 1.18;
    const supportsScrollEnd = 'onscrollend' in scroller;
    let animationFrame = 0;
    let fallbackTimer = 0;
    let releaseTimer = 0;
    let settling = false;

    const applyProgress = () => {
      animationFrame = 0;
      const progress = Math.min(1, Math.max(0, scroller.scrollTop / collapseDistance));
      scroller.style.setProperty('--turp-app-bar-row-shift', `${collapseDistance * progress}px`);
      scroller.style.setProperty('--turp-title-shift', `${expandedTitleShift * (1 - progress)}px`);
      scroller.style.setProperty(
        '--turp-title-scale',
        String(expandedTitleScale - ((expandedTitleScale - 1) * progress)),
      );
      scroller.style.setProperty('--turp-bar-opacity', String(progress));
      scroller.style.setProperty('--turp-bar-shadow-alpha', String(0.13 * progress));
    };

    const queueProgress = () => {
      if (!animationFrame) animationFrame = requestAnimationFrame(applyProgress);
    };

    const settlePartialTitle = () => {
      applyProgress();
      if (settling) return;
      const position = scroller.scrollTop;
      if (position <= 1 || position >= collapseDistance - 1) return;
      const target = position < collapseDistance / 2 ? 0 : collapseDistance;
      settling = true;
      scroller.scrollTo({
        top: target,
        behavior: reducedMotion.matches ? 'auto' : 'smooth',
      });
      clearTimeout(releaseTimer);
      releaseTimer = setTimeout(() => {
        settling = false;
        applyProgress();
      }, reducedMotion.matches ? 0 : 320);
    };

    scroller.addEventListener('scroll', () => {
      queueProgress();
      if (!supportsScrollEnd) {
        clearTimeout(fallbackTimer);
        fallbackTimer = setTimeout(settlePartialTitle, 140);
      }
    }, { passive: true });
    if (supportsScrollEnd) scroller.addEventListener('scrollend', settlePartialTitle);
    addEventListener('resize', queueProgress, { passive: true });
    applyProgress();
  }

  applyAppearance(currentThemePreference(), currentSchemePreference(), false);
  syncAppearanceLinks();
  setupTitleCollapse();
})();
