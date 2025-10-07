// static/js/activity-ws-client.js
(function () {
  'use strict';
  var ws = new WebSocket((location.protocol === 'https:' ? 'wss://' : 'ws://') + location.host + '/ws/activities');
  ws.addEventListener('message', function (ev) {
    try {
      var p = JSON.parse(ev.data);
      window.addActivity(p.icon || '🔔', p.text || p.message);
    } catch (e) { console.error(e); }
  });
})();
