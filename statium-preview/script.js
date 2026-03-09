const navToggle = document.querySelector('.nav-toggle');
const siteNav = document.querySelector('.site-nav');
const header = document.querySelector('.site-header');

if (navToggle && siteNav) {
  navToggle.addEventListener('click', () => {
    const isOpen = siteNav.classList.toggle('open');
    navToggle.setAttribute('aria-expanded', String(isOpen));
  });

  siteNav.querySelectorAll('a').forEach((link) => {
    link.addEventListener('click', () => {
      siteNav.classList.remove('open');
      navToggle.setAttribute('aria-expanded', 'false');
    });
  });
}

window.addEventListener('scroll', () => {
  if (!header) return;
  header.classList.toggle('scrolled', window.scrollY > 16);
});

document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
  anchor.addEventListener('click', (event) => {
    const id = anchor.getAttribute('href');
    const target = id ? document.querySelector(id) : null;
    if (!target) return;

    event.preventDefault();
    const offset = header ? header.offsetHeight + 12 : 0;
    const top = target.getBoundingClientRect().top + window.scrollY - offset;
    window.scrollTo({ top, behavior: 'smooth' });
  });
});

const revealItems = document.querySelectorAll('.reveal');
if ('IntersectionObserver' in window && revealItems.length) {
  const observer = new IntersectionObserver((entries, obs) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add('is-visible');
      obs.unobserve(entry.target);
    });
  }, { threshold: 0.14, rootMargin: '0px 0px -40px 0px' });

  revealItems.forEach((item, index) => {
    item.style.transitionDelay = `${Math.min(index * 40, 220)}ms`;
    observer.observe(item);
  });
} else {
  revealItems.forEach((item) => item.classList.add('is-visible'));
}

const oddsElements = document.querySelectorAll('.odds-live');
const oddsStates = [
  ['1.85 → 1.92', '2.10 → 2.05', '1.68 → 1.74'],
  ['1.84 → 1.90', '2.08 → 2.03', '1.70 → 1.77'],
  ['1.86 → 1.93', '2.12 → 2.06', '1.66 → 1.72']
];
let oddsIndex = 0;

if (oddsElements.length) {
  setInterval(() => {
    oddsIndex = (oddsIndex + 1) % oddsStates.length;
    oddsElements.forEach((element, index) => {
      if (!oddsStates[oddsIndex][index]) return;
      element.style.opacity = '0.5';
      setTimeout(() => {
        element.textContent = oddsStates[oddsIndex][index];
        element.style.opacity = '1';
      }, 180);
    });
  }, 2800);
}

const confidenceRing = document.querySelector('.confidence-ring');
if (confidenceRing) {
  const value = confidenceRing.dataset.value || '87';
  confidenceRing.style.setProperty('--value', value);
}
