/**
 * dt-omsdk-mraid-video-tracker.js
 * Ver. 01/2023
 */
var FyberMraidVideoTracker = (function initFyberOmid() {
    var sessionClient;
    var adSession;
    var partner;
    var context;
    var adEvents;
    var mediaEvents;
    let shouldHandleImpression = false;

    try {
        sessionClient = OmidSessionClient['default'];
    } catch (e) {
        FyLogger.log('FyberMraidVideoTracker --> initFyberOmid -> Unable to load OMSDK: ' + e);
        return {};
    }

    const AdSession = sessionClient.AdSession;
    const Partner = sessionClient.Partner;
    const Context = sessionClient.Context;
    const AdEvents = sessionClient.AdEvents;
    const MediaEvents = sessionClient.MediaEvents;

    function handleSessionEvent(event) {
        if (event.type === "sessionStart") {
            FyLogger.log('initFyberOmid --> FyberMraidVideoTracker -> sessionStart');
            if (event.data.creativeType === 'definedByJavaScript') {
                adSession.setCreativeType('video');
            }
            if (event.data.impressionType === 'definedByJavaScript') {
                adSession.setImpressionType('beginToRender');
            }

            adEvents.loaded();
        } else if (event.type === "sessionError") {
            FyLogger.log('initFyberOmid --> FyberMraidVideoTracker -> sessionError: ' + event);
            // handle error
        } else if (event.type === "sessionFinish") {
            // clean up
            FyberMraidVideoController.removeEventListener(onFyberVideoEventHandler);
            FyberMraidVideoController.removeVideoElementReadyListener();
        }
    }

    function onFyberVideoEventHandler(event) {
        FyLogger.log('onEvent --> FyberMraidVideoTracker -> event: ' + event.name);
        switch (event.name) {
            case EVENT_VIDEO_START:
                let duration = event.duration;
                if (!duration || duration === 'NaN') {
                    duration = -1;
                }
                mediaEvents.start(duration, event.volume);
                break;
            case EVENT_VIDEO_PAUSE:
                mediaEvents.pause();
                break;
            case EVENT_VIDEO_RESUME:
                mediaEvents.resume();
                break;
            case EVENT_VIDEO_COMPLETED:
                mediaEvents.complete();
                break;
            case EVENT_VIDEO_VOLUME_CHANGE:
                mediaEvents.volumeChange(event.volume);
                break;
            case EVENT_FIRST_QUARTILE:
                mediaEvents.firstQuartile();
                break;
            case EVENT_MIDPOINT:
                mediaEvents.midpoint();
                break;
            case EVENT_THIRD_QUARTILE:
                mediaEvents.thirdQuartile();
                break;
            case EVENT_VIDEO_BUFFER_START:
                mediaEvents.bufferStart();
                break;
            case EVENT_VIDEO_BUFFER_FINISH:
                mediaEvents.bufferFinish();
                break;
        }
    }

    function onVideoElementReady(videoElement) {
        if (!videoElement) {
            videoElement = document.querySelector("video");
        }

        context.setVideoElement(videoElement);
        adEvents = new AdEvents(adSession);
        mediaEvents = new MediaEvents(adSession);
        adSession.registerSessionObserver(handleSessionEvent);
        FyberMraidVideoController.registerEventListener(onFyberVideoEventHandler);
        FyberMraidVideoController.removeVideoElementReadyListener();
        if (shouldHandleImpression) {
            FyberMraidVideoTracker.impression();
        }
    }

    return {
        initOmid: function initOmidSession(name, version) {
            FyLogger.log('initOmidSession --> FyberMraidVideoTracker -> initializing omid session {partner: ' + name + ', version: ' + version + '}');
            partner = new Partner(name, version);
            context = new Context(partner);
            adSession = new AdSession(context);
            var videoElement = FyberMraidVideoController.videoElement();
            if (videoElement) {
                onVideoElementReady(videoElement);
            } else {
                FyberMraidVideoController.registerVideoElementReadyListener(onVideoElementReady);
            }
        },

        impression: function impression() {
            if (adEvents) {
                adEvents.impressionOccurred();
                shouldHandleImpression = false;
            } else {
                shouldHandleImpression = true;
            }
        },

        adUserInteraction: function adUserInteraction() {
            if (mediaEvents) {
                mediaEvents.adUserInteraction("click");
            }
        }
    }
})();