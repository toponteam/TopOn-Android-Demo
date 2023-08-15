VpaidWrapper = function () {
    this._creative = getVPAIDAd();
    this._ui = getUI();
    this.timer = null;
    VpaidWrapper.prototype.setVpaidClient = function (vpaidClient) {
        this._vpaidClient = vpaidClient;
    }
    VpaidWrapper.prototype.handshakeVersion = function (version) {
        var result = this._creative.handshakeVersion(version);
        android.handshakeVersionResult(result);
        return result;
    }
    VpaidWrapper.prototype.initAd = function (width, height, viewMode, desiredBitrate, creativeData, environmentVars) {
        this._creative.initAd(width, height, viewMode, desiredBitrate, creativeData, environmentVars);
        android.initAdResult();
    };
    VpaidWrapper.prototype.onAdPaused = function () {
        console.log("onAdPaused");
        this._vpaidClient.vpaidAdPaused();
    };
    VpaidWrapper.prototype.onAdPlaying = function () {
        console.log("onAdPlaying");
        this._vpaidClient.vpaidAdPlaying();
    };
    VpaidWrapper.prototype.onAdError = function (message) {
        console.log("onAdError: " + message);
        this._vpaidClient.vpaidAdError(message);
    };
    VpaidWrapper.prototype.onAdLog = function (message) {
        console.log("onAdLog: " + message);
        this._vpaidClient.vpaidAdLog(message);
    };
    VpaidWrapper.prototype.onAdUserAcceptInvitation = function () {
        console.log("onAdUserAcceptInvitation");
        this._vpaidClient.vpaidAdUserAcceptInvitation();
    };
    VpaidWrapper.prototype.onAdUserMinimize = function () {
        console.log("onAdUserMinimize");
        this._vpaidClient.vpaidAdUserMinimize();
    };
    VpaidWrapper.prototype.onAdUserClose = function () {
        console.log("onAdUserClose");
        this._vpaidClient.vpaidAdUserClose();
    };
    VpaidWrapper.prototype.onAdSkippableStateChange = function () {
        console.log("Ad Skippable State Changed to: " + this._creative.getAdSkippableState());
        this._ui.showSkipButton(this.getAdSkippableState());
        this._vpaidClient.vpaidAdSkippableStateChange();
    };
    VpaidWrapper.prototype.onAdExpandedChange = function () {
        console.log("Ad Expanded Changed to: " + this._creative.getAdExpanded());
        this._vpaidClient.vpaidAdExpandedChange();
    };
    VpaidWrapper.prototype.getAdExpanded = function () {
        console.log("getAdExpanded");
        var result = this._creative.getAdExpanded();
        android.getAdExpandedResult(result);
    };
    VpaidWrapper.prototype.getAdSkippableState = function () {
        console.log("getAdSkippableState");
        var result = this._creative.getAdSkippableState();
        android.getAdSkippableStateResult(result);
    };
    VpaidWrapper.prototype.onAdSizeChange = function () {
        console.log("Ad size changed to: w=" + this._creative.getAdWidth() + " h=" + this._creative.getAdHeight());
        this._vpaidClient.vpaidAdSizeChange();
    };
    VpaidWrapper.prototype.onAdDurationChange = function () {
        if (this.handshakeVersion() >= 2) {
            this._ui.moveProgress(this._creative.getAdRemainingTime(), this._creative.getAdDuration());
        }
        this._vpaidClient.vpaidAdDurationChange();
    };
    VpaidWrapper.prototype.onAdRemainingTimeChange = function () {
        if (this.handshakeVersion() < 2) {
            this._ui.moveProgress(this._creative.getAdRemainingTime(), this._creative.getAdDuration());
        }
        this._vpaidClient.vpaidAdRemainingTimeChange();
    };
    VpaidWrapper.prototype.getAdRemainingTime = function () {
        console.log("getAdRemainingTime");
        var result = this._creative.getAdRemainingTime();
        android.getAdRemainingTimeResult(result);
    };
    VpaidWrapper.prototype.onAdImpression = function () {
        console.log("Ad Impression");
        this._vpaidClient.vpaidAdImpression();
    };
    VpaidWrapper.prototype.onAdClickThru = function (url, id, playerHandles) {
        console.log("Clickthrough portion of the ad was clicked");
        var adjustedUrl = url;
        if (adjustedUrl == undefined) adjustedUrl = ""
        this._vpaidClient.vpaidAdClickThruIdPlayerHandles(adjustedUrl, id, playerHandles);
    };
    VpaidWrapper.prototype.onAdInteraction = function (id) {
        console.log("A non-clickthrough event has occured");
        this._vpaidClient.vpaidAdInteraction(id);
    };
    VpaidWrapper.prototype.onAdVideoStart = function () {
        console.log("Video 0% completed");
        this._vpaidClient.vpaidAdVideoStart();
        document.getElementById("black-screen") && (document.getElementById("black-screen").style.display = "none");
    };
    VpaidWrapper.prototype.onAdVideoFirstQuartile = function () {
        console.log("Video 25% completed");
        this._vpaidClient.vpaidAdVideoFirstQuartile();
    };
    VpaidWrapper.prototype.onAdVideoMidpoint = function () {
        console.log("Video 50% completed");
        this._vpaidClient.vpaidAdVideoMidpoint();
    };
    VpaidWrapper.prototype.onAdVideoThirdQuartile = function () {
        console.log("Video 75% completed");
        this._vpaidClient.vpaidAdVideoThirdQuartile();
    };
    VpaidWrapper.prototype.onAdVideoComplete = function () {
        console.log("Video 100% completed");
        this._vpaidClient.vpaidAdVideoComplete();
    };
    VpaidWrapper.prototype.onAdLinearChange = function () {
        console.log("Ad linear has changed: " + this._creative.getAdLinear());
        this._vpaidClient.vpaidAdLinearChange();
    };
    VpaidWrapper.prototype.getAdLinear = function () {
        console.log("getAdLinear");
        var result = this._creative.getAdLinear();
        android.getAdLinearResult(result);
    };
    VpaidWrapper.prototype.getAdDuration = function () {
        console.log("getAdDuration");
        var result = this._creative.getAdDuration();
        android.getAdDurationResult(result);
    };
    VpaidWrapper.prototype.onAdLoaded = function () {
        console.log("ad has been loaded");
        this._ui.createSkipButton();
        this._ui.showSkipButton(this.getAdSkippableState());
        this._vpaidClient.vpaidAdLoaded();
    };
    VpaidWrapper.prototype.onAdStarted = function () {
        console.log("Ad has started");
        this.timer = setInterval(function () {
            this._ui.moveProgress(this._creative.getAdRemainingTime(), this._creative.getAdDuration());
            var result = this._creative.getAdRemainingTime();
            this._vpaidClient.getAdRemainingTimeResult(result);
        }.bind(this), 500);
        this._vpaidClient.vpaidAdStarted();
    };
    VpaidWrapper.prototype.onAdStopped = function () {
        console.log("Ad has stopped");
        clearInterval(this.timer);
        this._vpaidClient.vpaidAdStopped();
    };
    VpaidWrapper.prototype.onAdSkipped = function () {
        console.log("Ad was skipped");
        this._creative.stopAd();
        this._vpaidClient.vpaidAdSkipped();
    };
    VpaidWrapper.prototype.setAdVolume = function (val) {
        this._creative.setAdVolume(val);
    };
    VpaidWrapper.prototype.getAdVolume = function () {
        var result = this._creative.getAdVolume();
        android.getAdVolumeResult(result);
    };
    VpaidWrapper.prototype.onAdVolumeChange = function () {
        console.log("Ad Volume has changed to - " + this._creative.getAdVolume());
        this._vpaidClient.vpaidAdVolumeChanged();
    };
    VpaidWrapper.prototype.startAd = function () {
        this._ui.createProgressBar();
        this._creative.startAd();
    };
    VpaidWrapper.prototype.skipAd = function () {
        this._creative.skipAd();
    };
    VpaidWrapper.prototype.stopAd = function () {
        this._creative.stopAd();
    };
    VpaidWrapper.prototype.resizeAd = function (width, height, viewMode) {
        this._creative.resizeAd(width, height, viewMode);
    };
    VpaidWrapper.prototype.pauseAd = function () {
        this._creative.pauseAd();
    };
    VpaidWrapper.prototype.resumeAd = function () {
        this._creative.resumeAd();
    };
    VpaidWrapper.prototype.expandAd = function () {
        this._creative.expandAd();
    };
    VpaidWrapper.prototype.collapseAd = function () {
        this._creative.collapseAd();
    };
    VpaidWrapper.prototype.setCallbacksForCreative = function () {
        var callbacks = {
            'AdStarted': this.onAdStarted,
            'AdStopped': this.onAdStopped,
            'AdSkipped': this.onAdSkipped,
            'AdLoaded': this.onAdLoaded,
            'AdLinearChange': this.onAdLinearChange,
            'AdSizeChange': this.onAdSizeChange,
            'AdExpandedChange': this.onAdExpandedChange,
            'AdSkippableStateChange': this.onAdSkippableStateChange,
            'AdDurationChange': this.onAdDurationChange,
            'AdRemainingTimeChange': this.onAdRemainingTimeChange,
            'AdVolumeChange': this.onAdVolumeChange,
            'AdImpression': this.onAdImpression,
            'AdClickThru': this.onAdClickThru,
            'AdInteraction': this.onAdInteraction,
            'AdVideoStart': this.onAdVideoStart,
            'AdVideoFirstQuartile': this.onAdVideoFirstQuartile,
            'AdVideoMidpoint': this.onAdVideoMidpoint,
            'AdVideoThirdQuartile': this.onAdVideoThirdQuartile,
            'AdVideoComplete': this.onAdVideoComplete,
            'AdUserAcceptInvitation': this.onAdUserAcceptInvitation,
            'AdUserMinimize': this.onAdUserMinimize,
            'AdUserClose': this.onAdUserClose,
            'AdPaused': this.onAdPaused,
            'AdPlaying': this.onAdPlaying,
            'AdError': this.onAdError,
            'AdLog': this.onAdLog
        };
        for (var eventName in callbacks) {
            this._creative.subscribe(callbacks[eventName], eventName, this);
        }
    };
    VpaidWrapper.prototype.onAdSkipPress = function () {
        this._creative.skipAd();
    }
    VpaidWrapper.prototype.setCallbacksForUI = function () {
        var callbacks = {'AdSkipped': this.onAdSkipPress,};
        for (var eventName in callbacks) {
            this._ui.subscribe(callbacks[eventName], eventName, this);
        }
    };
}
initVpaidWrapper = function () {
    vapidWrapperInstance = new VpaidWrapper();
    vapidWrapperInstance.setCallbacksForCreative();
    vapidWrapperInstance.setVpaidClient(android);
    vapidWrapperInstance.setCallbacksForUI()
    android.wrapperReady();
}