// JS: src/main/resources/static/js/activity.js
(function () {
	'use strict';

	var list = document.getElementById('activityList');
	var MAX_ITEMS = 20;

	// 追加用関数: iconText は視覚用の絵文字や短いアイコン、announce はスクリーンリーダー向け文
	function addActivity(iconText, announce) {
		if (!list) return;

		var li = document.createElement('li');
		li.className = 'activity-item';

		// 視覚的アイコン（スクリーンリーダーには無視させる）
		var icon = document.createElement('span');
		icon.setAttribute('aria-hidden', 'true');
		icon.className = 'activity-icon';
		icon.textContent = iconText || '🔔';

		// 実際のテキスト
		var text = document.createElement('span');
		text.className = 'activity-text';
		text.textContent = announce;

		li.appendChild(icon);
		li.appendChild(text);

		// 先頭に挿入して新しいものが上に来るようにする
		if (list.firstChild) {
			list.insertBefore(li, list.firstChild);
		} else {
			list.appendChild(li);
		}

		// 件数制限: 超えたら末尾を削除
		while (list.children.length > MAX_ITEMS) {
			list.removeChild(list.lastChild);
		}

		// オプション: 簡単なフェードイン（CSS 側で .fade-in を定義しておく）
		li.classList.add('fade-in');
		setTimeout(function () { li.classList.remove('fade-in'); }, 600);

		// スクリーンリーダー向け補助（既に aria-live があるなら不要だが確実に読み上げたい場合）
		// var sr = document.getElementById('activity-sr');
		// if (sr) { sr.textContent = announce; setTimeout(() => sr.textContent = '', 1000); }
	}

	// Expose for other modules
	window.addActivity = addActivity;

	// Example usage: window.addActivity('✅', 'ログイン成功しました');

	// ブラウザの devtools コンソールで実行
	window.addActivity('✅', '新しいアクティビティが追加されました');

})();
