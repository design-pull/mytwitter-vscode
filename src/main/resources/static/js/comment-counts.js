// comment-counts.js
(function () {
  'use strict';

  // デフォルト値（テンプレートで上書き可）
  var DEFAULT_MAX_COMMENT = 500;
  var DEFAULT_MAX_AUTHOR = 30;

  // テンプレ側で次のように値を注入している場合、それがグローバル定数として使われます:
  // <script th:inline="javascript"> const MAX_COMMENT_LENGTH = /*[[${maxCommentLength}]]*/ 500; </script>
  var MAX_BODY = (typeof MAX_COMMENT_LENGTH === 'number' && MAX_COMMENT_LENGTH > 0) ? MAX_COMMENT_LENGTH : DEFAULT_MAX_COMMENT;
  var MAX_AUTHOR = DEFAULT_MAX_AUTHOR;

  function el(id) { return document.getElementById(id); }

  function updateCount(inputEl, counterEl, max) {
    if (!inputEl || !counterEl) return;
    var len = inputEl.value ? inputEl.value.length : 0;
    counterEl.textContent = len + ' / ' + max;
    counterEl.style.color = (len > max) ? '#c00' : '#666';
  }

  function enforceMax(inputEl, max) {
    if (!inputEl) return;
    var v = inputEl.value || '';
    if (v.length > max) inputEl.value = v.substring(0, max);
  }

  function init() {
    // IDs used in your current HTML
    var body = el('comment') || el('body') || el('body'); // accept either id
    var bodyCount = el('comment-count') || el('body-count');
    var author = el('author') || el('displayName') || null;
    var authorCount = el('author-count') || el('displayName-count');

    if (body) {
      body.addEventListener('input', function () { updateCount(body, bodyCount, MAX_BODY); });
      updateCount(body, bodyCount, MAX_BODY);
    }
    if (author) {
      author.addEventListener('input', function () { updateCount(author, authorCount, MAX_AUTHOR); });
      updateCount(author, authorCount, MAX_AUTHOR);
    }

    var form = document.querySelector('.comment-form');
    if (form) {
      form.addEventListener('submit', function (e) {
        if (body) {
          body.value = body.value.trim();
          enforceMax(body, MAX_BODY);
          if (body.value.length === 0) {
            e.preventDefault();
            alert('コメントは必須です。');
            body.focus();
            return;
          }
          if (body.value.length > MAX_BODY) {
            e.preventDefault();
            alert('コメントは' + MAX_BODY + '文字以内で入力してください。');
            body.focus();
            return;
          }
        }
        if (author) {
          author.value = author.value.trim();
          if (author.value.length > MAX_AUTHOR) {
            e.preventDefault();
            alert('表示名は' + MAX_AUTHOR + '文字以内で入力してください。');
            author.focus();
            return;
          }
        }
      }, { passive: false });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
