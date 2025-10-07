// static/js/activity-ajax.js
(function () {
  'use strict';
  var form = document.querySelector('.comment-form[data-ajax="true"]');
  if (!form) return;

  form.addEventListener('submit', function (e) {
    e.preventDefault();
    var fd = new FormData(form);
    var url = form.getAttribute('action') || window.location.pathname;

    fetch(url, {
      method: 'POST',
      body: fd,
      headers: { 'X-Requested-With': 'XMLHttpRequest' }
    }).then(function (res) {
      if (!res.ok) throw res;
      return res.json();
    }).then(function (data) {
      // 期待するレスポンス例: { author: "Tera", preview: "本文先頭..." }
      var icon = data.icon || '💬';
      var text = data.preview || (data.author ? data.author + ': コメントが投稿されました' : '新しいコメント');
      if (window.addActivity) window.addActivity(icon, text);
      // フォームクリア
      form.reset();
      // カウント更新があるなら手動で呼ぶ（comment-counts.js の init に依存）
      var commentEl = document.getElementById('comment');
      if (commentEl) commentEl.dispatchEvent(new Event('input'));
    }).catch(function (err) {
      console.error(err);
      alert('投稿に失敗しました。再度お試しください。');
    });
  });
})();
