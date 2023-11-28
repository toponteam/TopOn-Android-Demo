VpaidUI = function () {
    this._eventsCallbacks = {};
    VpaidUI.prototype.createSkipButton = function () {
        var adButton = document.createElement('div');
        adButton.addEventListener('click', this.skipAd.bind(this), false);
        adButton.onclick = function (e) {
            e.stopPropagation();
        };
        var vpaidContainer = document.getElementById('slot');
        vpaidContainer.appendChild(adButton);
        adButton.style.position = "fixed";
        adButton.transform = "translate(0%, 0%)";
        adButton.style.right = 0;
        adButton.style.top = 0;
        adButton.style.width = "60px";
        adButton.style.height = "60px";
        adButton.style.backgroundSize = "19px";
        adButton.style.opacity = 1;
        adButton.style.background = "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABUAAAAVCAYAAACpF6WWAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAolJREFUeNqklctrE1EUxjs1KepYnx0TsUhDFEUNAReFkCxKNkZ0F/L4C0JCwCgKMUJWLiSItb5ITAIKKTQPcaWEuoguEgNdBVI31iQIiklRWm2mlBYcvyN3Qi22ncEDP7hzz51zzz3fmTtc3/9bP+DBL0mSRJrgmGMA7GXjLlhREfAh2AkkcBWBlzQs4AO2G9kyuAF+0O7bBD1IycRisbOtVktMJBITHMddIcdhMFksFlvlcvmz3W6fxvMz8Bjs2iboMZBDdlKtVpvH+CnY95dDtmw2+0Gv1z/HfAoMsWMqDtrP6rdGKzqdzrIoimsej+cEFl1wuVzHMX2P1Y1Xo1zP2u22aDQaX1UqlS86nW53Pp8fQ1lsGB+B+z4r1Q5VQeVsbTbbG7fb/ZbGDodjpNFoXPL7/afgHgePwKCqoKw1rhUKhY9ms3k6l8vN8TyvjcfjVgg5ZjKZhuGfAHo1QWW7jEy/er3ed5Q1WmbRarUerVar59FC5+C/C25u9vKfllqn4CSbkzc9AJ6AqWQyOSt3CFs7tZn6Wxk1/08QpY8iGo3OkYjkQGkElGJQ7fH7WCOTMOMQ6iQEu0glIAEDgUClXq9/+9dLmi0CkhC3kY2QyWRGKTOaJOFCodAsAn/H4y1wR2lQWrwHgpwOBoNnSH0SKhwO19AVn+Cj2+g6EJRmqsH3P5JOp0cNBsN++sJSqdR7n89XZ/VVdNn01G82mwsbFcbxXzJVh9kVp+hC6QWVrdvtrkYikRnWXgl5odILRcN219IxqXbUMk6ncwZCLGA+AhbBqppfgYYVfcVisbwWBEFbKpXm19VuSUEfS5QQnW6j8xCIM4YU9K9sdB2mqe64yV6wUg38FmAAGmFemcqkrBIAAAAASUVORK5CYII=') no-repeat";
        adButton.style.zIndex = 999999999999;
        adButton.style.border = "none";
        adButton.style.backgroundRepeat = "no-repeat";
        adButton.style.backgroundPosition = "center center";
        adButton.id = "skip-button";
        adButton.style.display = "none";
    };
    VpaidUI.prototype.showSkipButton = function (isShow) {
        console.log("SHOW BUTTON -- " + isShow);
        var skipButton = document.getElementById("skip-button");
        if (skipButton != null)
            skipButton.style.display = isShow ? "block" : "none";
    }
    VpaidUI.prototype.createProgressBar = function () {
        var progress = document.createElement('div');
        var bar = document.createElement('div');
        progress.appendChild(bar);
        var vpaidContainer = document.getElementById('slot');
        vpaidContainer.appendChild(progress);
        progress.style.position = "absolute";
        progress.style.width = "100%";
        progress.style.height = "3px";
        progress.style.backgroundColor = "transparent";
        progress.style.zIndex = 1000000;
        progress.style.bottom = 0;
        progress.id = "progress-lm";
        bar.style.position = "absolute";
        bar.style.width = "0%";
        bar.style.height = "100%";
        bar.style.backgroundColor = "rgb(0, 142, 239)";
        bar.style.zIndex = 1000000;
        bar.id = "bar-lm";
    };
    VpaidUI.prototype.moveProgress = function (currentTime, duration) {
        var step = (duration - currentTime) / duration * 100;
        var elem = document.getElementById("bar-lm");
        elem.style.width = step + "%";
    }
    VpaidUI.prototype.subscribe = function (aCallback, eventName, aContext) {
        var callBack = aCallback.bind(aContext);
        this._eventsCallbacks[eventName] = callBack;
    };
    VpaidUI.prototype.unsubscribe = function (eventName) {
        this._eventsCallbacks[eventName] = null;
    };
    VpaidUI.prototype._callEvent = function (eventType) {
        if (eventType in this._eventsCallbacks) {
            this._eventsCallbacks[eventType]();
        }
    };
    VpaidUI.prototype.skipAd = function () {
        this._callEvent('AdSkipped');
    };
}
var getUI = function () {
    return new VpaidUI();
}