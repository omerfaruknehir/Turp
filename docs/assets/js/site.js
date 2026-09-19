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
