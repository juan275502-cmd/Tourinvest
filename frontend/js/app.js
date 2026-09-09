(function () {
  // Helpers de UI compartidos entre dashboards (vanilla JS).

  window.TourInvestUI = window.TourInvestUI || {};

  // Tabs: despliega contenido usando [data-tab-id] y [data-tab-target]
  // dentro del mismo contenedor.
  window.TourInvestUI.initTabs = function initTabs(rootSelector) {
    var root = document.querySelector(rootSelector);
    if (!root) return;

    var buttons = root.querySelectorAll('[data-tab-id]');
    var panes = root.querySelectorAll('[data-tab-target]');
    if (!buttons.length || !panes.length) return;

    function setActive(id) {
      buttons.forEach(function (btn) {
        btn.classList.toggle('tab-btn--active', btn.getAttribute('data-tab-id') === id);
      });
      panes.forEach(function (pane) {
        pane.style.display = (pane.getAttribute('data-tab-target') === id) ? '' : 'none';
      });
    }

    buttons.forEach(function (btn) {
      btn.addEventListener('click', function () {
        var id = btn.getAttribute('data-tab-id');
        if (!id) return;
        setActive(id);
      });
    });

    // Arranque: primer tab activo o primero disponible.
    var activeBtn = root.querySelector('[data-tab-id].tab-btn--active') || buttons[0];
    if (activeBtn) setActive(activeBtn.getAttribute('data-tab-id'));
  };

  // Modales
  window.TourInvestUI.initModals = function initModals() {
    document.querySelectorAll('[data-open-modal]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        var sel = btn.getAttribute('data-open-modal');
        if (!sel) return;
        var modal = document.querySelector(sel);
        if (!modal) return;
        modal.classList.add('modal--open');
        modal.setAttribute('aria-hidden', 'false');
      });
    });

    document.querySelectorAll('[data-close-modal]').forEach(function (x) {
      x.addEventListener('click', function () {
        var modal = x.closest('.modal');
        if (!modal) return;
        modal.classList.remove('modal--open');
        modal.setAttribute('aria-hidden', 'true');
      });
    });

    // Backdrop click
    document.querySelectorAll('.modal .modal__backdrop').forEach(function (bd) {
      bd.addEventListener('click', function () {
        var modal = bd.closest('.modal');
        if (!modal) return;
        modal.classList.remove('modal--open');
        modal.setAttribute('aria-hidden', 'true');
      });
    });

    // ESC
    document.addEventListener('keydown', function (e) {
      if (e.key !== 'Escape') return;
      document.querySelectorAll('.modal.modal--open').forEach(function (m) {
        m.classList.remove('modal--open');
        m.setAttribute('aria-hidden', 'true');
      });
    });
  };

  // Toast
  window.TourInvestUI.toast = function toast(msg, variant) {
    variant = variant || 'success';
    var root = document.getElementById('toast-root');
    if (!root) {
      root = document.createElement('div');
      root.id = 'toast-root';
      document.body.appendChild(root);
    }

    var t = document.createElement('div');
    t.className = 'toast ' + (variant === 'error' ? 'toast--error' : 'toast--success');
    t.textContent = msg;
    root.appendChild(t);

    setTimeout(function () {
      t.classList.add('toast--hide');
      setTimeout(function () {
        if (t.parentNode) t.remove();
      }, 250);
    }, 2600);
  };

  // Expose legacy names if some HTML uses them
  window.showToast = function (msg) { window.TourInvestUI.toast(msg, 'success'); };

  window.setModalUser = function () { /* set by each page if needed */ };
})();

