function insertSoftBreaks(s, maxRun) {
  return s.replace(new RegExp('([^\\s]{' + maxRun + '})(?!\\s)', 'g'), '$1\u200B');
}

(function () {
  'use strict';

  var MAX_DN = 10;
  var MAX_BIO = 100;

  function $(id) { return document.getElementById(id); }

  function updateCounter(el, counter, max) {
    if (!el || !counter) return;
    var len = el.value ? el.value.length : 0;
    counter.textContent = len;
    counter.style.color = len > max ? '#c00' : '#666';
    counter.setAttribute('aria-live', 'polite');
  }

  function enforceMaxlength(el, max) {
    if (!el) return;
    var val = el.value || '';
    if (val.length > max) {
      el.value = val.substring(0, max);
    }
  }

  document.addEventListener('DOMContentLoaded', function () {
    var dn = $('displayName');
    var bio = $('bio');
    var dnCount = $('displayName-count');
    var bioCount = $('bio-count');
    var form = document.querySelector('.profile-form');

    if (dn) updateCounter(dn, dnCount, MAX_DN);
    if (bio) updateCounter(bio, bioCount, MAX_BIO);

    if (dn && !dn.getAttribute('maxlength')) dn.setAttribute('maxlength', String(MAX_DN));
    if (bio && !bio.getAttribute('maxlength')) bio.setAttribute('maxlength', String(MAX_BIO));

    if (dn) dn.addEventListener('input', function () { updateCounter(dn, dnCount, MAX_DN); });
    if (bio) bio.addEventListener('input', function () { updateCounter(bio, bioCount, MAX_BIO); });

    if (form) {
      form.addEventListener('submit', function (e) {
        if (dn) dn.value = dn.value.trim();
        if (bio) bio.value = bio.value.trim();

        if (dn) enforceMaxlength(dn, MAX_DN);
        if (bio) enforceMaxlength(bio, MAX_BIO);

        var dnLen = dn ? dn.value.length : 0;
        var bioLen = bio ? bio.value.length : 0;

        if (dnLen === 0) {
          e.preventDefault();
          alert('表示名は必須です。');
          if (dn) dn.focus();
          return;
        }

        if (dnLen > MAX_DN || bioLen > MAX_BIO) {
          e.preventDefault();
          alert('入力文字数が上限を超えています。表示名は' + MAX_DN + '文字以内、自己紹介は' + MAX_BIO + '文字以内で入力してください。');
        }
      }, { passive: false });
    }
  });
})();
