(function () {
  'use strict';
  function formatPreview(author, body) {
    var a = author && author.trim() ? author.trim() : '匿名（とくめい）';
    var preview = (body || '').trim();
    if (preview.length > 60) preview = preview.substring(0, 60) + '...';
    return a + ': ' + preview;
  }
  var form = document.querySelector('.comment-form');
  if (!form) return;

  form.addEventListener('submit', function (e) {
    try {
      var author = (document.getElementById('author') || { value: '' }).value;
      var comment = (document.getElementById('comment') || { value: '' }).value;
      if (window.addActivity && comment && comment.trim().length > 0) {
        window.addActivity('💬', formatPreview(author, comment));
      }
    } catch (err) {
    }
  }, { passive: true });
})();
