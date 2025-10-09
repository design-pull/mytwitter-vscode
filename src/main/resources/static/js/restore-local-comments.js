// restore-local-comments.js
(function () {
  'use strict';

  function readLocalComments() {
    try {
      return JSON.parse(localStorage.getItem('localComments_v1') || '[]');
    } catch (e) {
      console.error('localComments parse error', e);
      return [];
    }
  }

  function normalizeLocal(c) {
    return {
      id: c.id || null,
      serverId: c.serverId || null,
      author: c.author || 'anonymous',
      body: c.body || '',
      createdAt: c.createdAt || new Date().toISOString()
    };
  }

  function parseISO(s) {
    var d = new Date(s);
    return isNaN(d.getTime()) ? null : d;
  }

  function collectServerItemsFromList(list) {
    var items = [];
    list.querySelectorAll('.comment-item').forEach(function (li) {
      var serverId = li.getAttribute('data-server-id') || null;
      var localId = li.getAttribute('data-local-id') || null;
      var authorEl = li.querySelector('.comment-meta strong');
      var timeEl = li.querySelector('.comment-time');
      var bodyEl = li.querySelector('.comment-body');
      var author = authorEl ? authorEl.textContent.trim() : 'anonymous';
      var body = bodyEl ? bodyEl.textContent.trim() : '';
      // try to read ISO timestamp from data-ts first, fallback to text content
      var iso = timeEl && timeEl.getAttribute ? timeEl.getAttribute('data-ts') : null;
      var createdAt = iso || (timeEl ? timeEl.textContent.trim() : new Date().toISOString());
      items.push({
        id: serverId || localId || null,
        serverId: serverId,
        localId: localId,
        author: author,
        body: body,
        createdAt: createdAt
      });
    });
    return items;
  }

  function getTargetLists() {
    var lists = [];
    var el1 = document.querySelector('.comment-list');
    if (el1) lists.push(el1);
    var el2 = document.querySelector('.recent-list');
    if (el2) lists.push(el2);
    return lists;
  }

  function mergeServerAndLocal(serverItems, localItems) {
    // map serverIds to avoid duplicates
    var seenServer = {};
    serverItems.forEach(function (s) {
      if (s.serverId) seenServer[s.serverId] = true;
    });
    // include serverItems as-is
    var merged = serverItems.slice();
    // append local items that are not present on server (serverId not set or not seen)
    localItems.forEach(function (l) {
      if (l.serverId && seenServer[l.serverId]) return;
      merged.push({
        id: l.serverId || l.id || null,
        serverId: l.serverId || null,
        localId: l.id || null,
        author: l.author,
        body: l.body,
        createdAt: l.createdAt
      });
    });
    return merged;
  }

  function sortByCreatedAtDesc(items) {
    return items.sort(function (a, b) {
      var da = parseISO(a.createdAt) || new Date(0);
      var db = parseISO(b.createdAt) || new Date(0);
      return db - da;
    });
  }


  function createLi(c) {
    var li = document.createElement('li');
    li.className = 'comment-item';
    if (c.serverId) li.setAttribute('data-server-id', String(c.serverId));
    else if (c.localId) li.setAttribute('data-local-id', String(c.localId));

    var meta = document.createElement('div'); meta.className = 'comment-meta';
    var strong = document.createElement('strong');
    strong.textContent = (c.author === 'anonymous' || !c.author) ? '匿名（とくめい）' : c.author;
    var small = document.createElement('small');
    small.className = 'comment-time';
    // show human readable if createdAt is ISO like, but keep data-ts for parsing
    var d = parseISO(c.createdAt);
    if (d) {
      var yyyy = d.getFullYear();
      var mm = ('0' + (d.getMonth() + 1)).slice(-2);
      var dd = ('0' + d.getDate()).slice(-2);
      var hh = ('0' + d.getHours()).slice(-2);
      var min = ('0' + d.getMinutes()).slice(-2);
      small.textContent = yyyy + '/' + mm + '/' + dd + ' ' + hh + ':' + min;
      small.setAttribute('data-ts', d.toISOString());
    } else {
      small.textContent = c.createdAt || '';
    }

    meta.appendChild(strong);
    meta.appendChild(small);

    var p = document.createElement('p'); p.className = 'comment-body';
    var max = parseInt(window.MAX_COMMENT_LENGTH || '500', 10);
    var body = c.body || '';
    if (body.length > max) body = body.substring(0, max) + '...';
    p.textContent = body;

    li.appendChild(meta);
    li.appendChild(p);
    return li;
  }

  function renderList(listEl, items) {
    listEl.innerHTML = '';
    items.forEach(function (c) {
      listEl.appendChild(createLi(c));
    });
  }

  function mergeAndRenderAll() {
    var lists = getTargetLists();
    if (lists.length === 0) return;

    // collect server items from first list (they are identical view-wise across lists)
    var serverItems = collectServerItemsFromList(lists[0]);
    var localRaw = readLocalComments();
    var localItems = localRaw.map(normalizeLocal);

    var merged = mergeServerAndLocal(serverItems, localItems);
    var sorted = sortByCreatedAtDesc(merged);

    // render sorted into each target list
    lists.forEach(function (list) {
      renderList(list, sorted);
    });
  }

  // integrate with existing loadPage: call merge+render after loadPage completes
  function integrateWithLoadPage() {
    if (typeof window.loadPage === 'function') {
      var orig = window.loadPage;
      window.loadPage = function () {
        var p = orig.apply(this, arguments);
        if (p && typeof p.then === 'function') {
          p.then(function () { mergeAndRenderAll(); }).catch(function () { });
        } else {
          setTimeout(mergeAndRenderAll, 50);
        }
        return p;
      };
    } else {
      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', mergeAndRenderAll);
      } else {
        mergeAndRenderAll();
      }
    }
  }

  integrateWithLoadPage();

})();