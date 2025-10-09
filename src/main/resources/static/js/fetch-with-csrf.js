(function () {
  'use strict';
  function getMeta(name) {
    var m = document.querySelector('meta[name="' + name + '"]');
    return m ? m.getAttribute('content') : null;
  }
  var CSRF_TOKEN = getMeta('_csrf');
  var CSRF_HEADER = getMeta('_csrf_header') || 'X-CSRF-TOKEN';
  function fetchWithCsrf(url, options) {
    options = options || {};
    options.headers = options.headers || {};
    if (!(options.body instanceof FormData) && options.body && typeof options.body === 'object') {
      options.headers['Content-Type'] = options.headers['Content-Type'] || 'application/json;charset=UTF-8';
      options.body = JSON.stringify(options.body);
    }
    if (CSRF_TOKEN) {
      options.headers[CSRF_HEADER] = CSRF_TOKEN;
    }
    options.headers['X-Requested-With'] = options.headers['X-Requested-With'] || 'XMLHttpRequest';
    return fetch(url, options);
  }
  window.fetchWithCsrf = fetchWithCsrf;
})();
