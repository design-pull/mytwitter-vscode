(function () {
	'use strict';

	var list = document.getElementById('activityList');
	var MAX_ITEMS = 20;

	function addActivity(iconText, announce) {
		if (!list) return;

		var li = document.createElement('li');
		li.className = 'activity-item';

		var icon = document.createElement('span');
		icon.setAttribute('aria-hidden', 'true');
		icon.className = 'activity-icon';
		icon.textContent = iconText || '🔔';

		var text = document.createElement('span');
		text.className = 'activity-text';
		text.textContent = announce;

		li.appendChild(icon);
		li.appendChild(text);

		if (list.firstChild) {
			list.insertBefore(li, list.firstChild);
		} else {
			list.appendChild(li);
		}

		while (list.children.length > MAX_ITEMS) {
			list.removeChild(list.lastChild);
		}

		li.classList.add('fade-in');
		setTimeout(function () { li.classList.remove('fade-in'); }, 600);

	}

	window.addActivity = addActivity;
})();
