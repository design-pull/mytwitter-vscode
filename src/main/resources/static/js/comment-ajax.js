// comment-ajax.js
(function () {
  'use strict';

  var form = document.querySelector('.comment-form[data-ajax="true"]') || document.querySelector('.comment-form');
  if (!form) return;

  function getMetaContent(name) {
    var m = document.querySelector('meta[name="' + name + '"]');
    return m ? m.getAttribute('content') : null;
  }
  var CSRF_TOKEN = getMetaContent('_csrf');
  var CSRF_HEADER = getMetaContent('_csrf_header') || 'X-CSRF-TOKEN';

  function createCommentListItem(data) {
    var li = document.createElement('li');
    li.className = 'comment-item';

    var meta = document.createElement('div');
    meta.className = 'comment-meta';

    var strong = document.createElement('strong');
    strong.textContent = (data.author === 'anonymous') ? '匿名（とくめい）' : data.author;

    var small = document.createElement('small');
    small.className = 'comment-time';
    if (data.createdAt) {
      try {
        var d = new Date(data.createdAt);
        if (!isNaN(d.getTime())) {
          var yyyy = d.getFullYear();
          var mm = ('0' + (d.getMonth() + 1)).slice(-2);
          var dd = ('0' + d.getDate()).slice(-2);
          var hh = ('0' + d.getHours()).slice(-2);
          var min = ('0' + d.getMinutes()).slice(-2);
          small.textContent = yyyy + '/' + mm + '/' + dd + ' ' + hh + ':' + min;
        } else {
          small.textContent = data.createdAt;
        }
      } catch (e) {
        small.textContent = data.createdAt;
      }
    } else {
      small.textContent = '';
    }

    meta.appendChild(strong);
    meta.appendChild(small);

    var p = document.createElement('p');
    p.className = 'comment-body';
    var bodyText = data.body || data.preview || '';
    var max = parseInt(window.MAX_COMMENT_LENGTH || '500', 10);
    if (bodyText.length > max) bodyText = bodyText.substring(0, max) + '...';
    p.textContent = bodyText;

    li.appendChild(meta);
    li.appendChild(p);

    return li;
  }

  function toUrlSearchParams(form) {
    var params = new URLSearchParams();
    var f = new FormData(form);
    for (var pair of f.entries()) {
      params.append(pair[0], pair[1] === undefined || pair[1] === null ? '' : pair[1]);
    }
    return params;
  }

  function init() {
    form.addEventListener('submit', function (e) {
      e.preventDefault();

      var base = form.getAttribute('action') || window.location.pathname;
      var url = base.endsWith('/ajax') ? base : (base.replace(/\/$/, '') + '/ajax');

      var params = toUrlSearchParams(form);

      var headers = {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
      };
      if (CSRF_TOKEN) {
        headers[CSRF_HEADER] = CSRF_TOKEN;
      }

      fetch(url, {
        method: 'POST',
        body: params.toString(),
        headers: headers,
        credentials: 'same-origin'
      }).then(function (res) {
        if (!res.ok) throw res;
        return res.json();
      }).then(function (data) {
        if (window.addActivity) {
          window.addActivity(data.icon || '💬', (data.author === 'anonymous' ? '匿名（とくめい）' : data.author) + ': ' + (data.preview || 'コメントが投稿されました'));
        }

        var list = document.querySelector('.comment-list');
        if (list) {
          var li = createCommentListItem(data);
          if (list.firstChild) list.insertBefore(li, list.firstChild);
          else list.appendChild(li);
        }

        form.reset();
        var commentEl = document.getElementById('comment');
        if (commentEl) commentEl.dispatchEvent(new Event('input'));
      }).catch(function (err) {
        console.error('comment-ajax error', err);
        if (err && err.status === 400) {
          alert('送信内容に問題があります。入力を確認してください。');
        } else {
          alert('投稿に失敗しました。ページをリロードして再試行してください。');
        }
      });
    }, { passive: false });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
