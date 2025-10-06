// comment-counts.js
(function () {
  'use strict';

  var MAX_BODY = 500;
  var MAX_AUTHOR = 30;

  function $(id) { return document.getElementById(id); }
  function q(sel) { return document.querySelector(sel); }

  function updateCount(el, counterEl, max) {
    if (!el || !counterEl) return;
    var len = el.value ? el.value.length : 0;
    counterEl.textContent = len + ' / ' + max;
    counterEl.style.color = (len > max) ? '#c00' : '#666';
  }

  function init() {
    var body = $('body');
    var bodyCount = $('body-count');
    var author = $('author');
    var authorCount = $('author-count');
    var form = q('.comment-form');

    // If Thymeleaf injected MAX_COMMENT_LENGTH, override default
    if (typeof MAX_COMMENT_LENGTH === 'number' && MAX_COMMENT_LENGTH > 0) {
      MAX_BODY = MAX_COMMENT_LENGTH;
    }

    if (body) {
      body.addEventListener('input', function () { updateCount(body, bodyCount, MAX_BODY); });
      updateCount(body, bodyCount, MAX_BODY);
    }
    if (author) {
      author.addEventListener('input', function () { updateCount(author, authorCount, MAX_AUTHOR); });
      updateCount(author, authorCount, MAX_AUTHOR);
    }

    if (form) {
      form.addEventListener('submit', function (e) {
        var bodyVal = body ? body.value || '' : '';
        var authorVal = author ? author.value.trim() : '';

        if (bodyVal.length === 0) {
          e.preventDefault();
          alert('コメントは必須です。');
          if (body) body.focus();
          return;
        }

        if (bodyVal.length > MAX_BODY) {
          e.preventDefault();
          alert('コメントは' + MAX_BODY + '文字以内で入力してください。');
          if (body) body.focus();
          return;
        }
        if (authorVal.length > MAX_AUTHOR) {
          e.preventDefault();
          alert('表示名は' + MAX_AUTHOR + '文字以内で入力してください。');
          if (author) author.focus();
          return;
        }

        // 送信前にトリム・切り詰め（保険）
        if (body && bodyVal.length > MAX_BODY) body.value = bodyVal.substring(0, MAX_BODY);
        if (author && authorVal.length > MAX_AUTHOR) author.value = authorVal.substring(0, MAX_AUTHOR);
      }, { passive: false });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
