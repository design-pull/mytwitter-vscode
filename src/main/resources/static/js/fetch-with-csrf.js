// fetch-with-csrf.js
(function () {
  'use strict';

  // ページの meta から CSRF トークンとヘッダ名を取得
  function getMeta(name) {
    var m = document.querySelector('meta[name="' + name + '"]');
    return m ? m.getAttribute('content') : null;
  }

  var CSRF_TOKEN = getMeta('_csrf');
  var CSRF_HEADER = getMeta('_csrf_header') || 'X-CSRF-TOKEN';

  function fetchWithCsrf(url, options) {
    options = options || {};
    options.headers = options.headers || {};

    // JSON body の場合は適切なヘッダを追加
    if (!(options.body instanceof FormData) && options.body && typeof options.body === 'object') {
      options.headers['Content-Type'] = options.headers['Content-Type'] || 'application/json;charset=UTF-8';
      options.body = JSON.stringify(options.body);
    }

    // CSRF トークンをヘッダに追加（存在する場合）
    if (CSRF_TOKEN) {
      options.headers[CSRF_HEADER] = CSRF_TOKEN;
    }

    // X-Requested-With はサーバ側で Ajax 判定に使える
    options.headers['X-Requested-With'] = options.headers['X-Requested-With'] || 'XMLHttpRequest';

    return fetch(url, options);
  }

  window.fetchWithCsrf = fetchWithCsrf;
})();
