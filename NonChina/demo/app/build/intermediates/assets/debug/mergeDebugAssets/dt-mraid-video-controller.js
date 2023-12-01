/**
 * dt-mraid-video-controller.js
 * Ver. 0.1 - 01/2023
 */

var EVENT_VIDEO_START = "videoStart";
var EVENT_VIDEO_PAUSE = "videoPause";
var EVENT_VIDEO_RESUME = "videoResume";
var EVENT_VIDEO_COMPLETED = "videoCompleted";
var EVENT_VIDEO_VOLUME_CHANGE = "volumeChange";
var EVENT_VIDEO_BUFFER_START = "bufferingStart";
var EVENT_VIDEO_BUFFER_FINISH = "bufferingEnd";

var EVENT_FIRST_QUARTILE = "firstQuartile";
var EVENT_MIDPOINT = "midpoint";
var EVENT_THIRD_QUARTILE = "thirdQuartile";

var EVENT_FY_MRAID_VIDEO_AD = "fyMraidVideoAd";
var EVENT_FY_MRAID_AD_COMPLETION = "fyMraidVideoAdCompleted";

var sdkPrefixProtocol = 'FyMraidVideo://';//prfix protocol to notify SDK

var FyLogger = (function initFyLogger() {

    var fyLogger = {
        log: function log() {

        },

        info: function info() {

        },

        error : function error() {

        },

        warn: function warn() {

        }
    };

    var _logger = window.console || fyLogger;

    return {
        log: function log() {
            var argsAsArray = Array.prototype.slice.call(arguments);
            argsAsArray.forEach(function forEachArgs(arg, index){
                if(typeof arg === 'string') {
                    argsAsArray[index] = '%c'+arg;
                }
            });
            argsAsArray.push('color: #89a6d6; font-size: 18px;');
            _logger.log.apply(_logger, argsAsArray);
        },

        info: function info() {

        },

        error : function error() {

        },

        warn: function warn() {

        }
    };
})();

var FyberMraidVideoController = (function initFyberMraidVideoController() {
    var video;//the HTMLVideoElement element
    var animationName = 'node-an-inserted';//the animation name an hack to catch when video element inserted to dom
    var nativePlayMethod;//the native play method that will be override so we can will took control when to statrt ad
    var isFyPlay = false;
    var overlay;//HTMLDivElement hack to catch when video controls clicked by the user
    var ifr = getSDKRedicectIfr();//HTMLIframeElement to notify SDK on events
    var quartiles;// an array of data about quartiles events
    var sdkPlayBtn;//HTMLElement that SDK will clicked for usergester to start playing
    var isFyMraidVideoAd = false;//boolean flag to detect if the ad include video element
    
    var onFyberVideoEvent = [];
    var onHtmlVideoElementReady = [];
    var minVideoCompleteDuration;

    /**
     * start the quartiles events object
     * @param {number} duration the video clip length
     */
    function initQuartiles(duration) {
        quartiles = [
            {name: EVENT_FIRST_QUARTILE, tracked: false, time: Math.round(25 * duration) / 100},
            {name: EVENT_MIDPOINT, tracked: false, time: Math.round(50 * duration) / 100},
            {name: EVENT_THIRD_QUARTILE, tracked: false, time: Math.round(75 * duration) / 100}
        ];
    }

    /**
     * register events of time update and duration changed
     * to notify to SDK about quartiles events
     * @param {HTMLVideoElement} video
     */
    function handleVideoQuartilesEvents(video) {
        if(video){
            var duration = video.duration;
            if(!duration) {
                video.addEventListener('durationchange', function onDurationChange(){
                    duration = video.duration;
                    onVideoDurationAvailable(duration);
                }, {once: true});
            } else {
                onVideoDurationAvailable(duration);
            }
        }
    }
    
    /**
     * validate if all Quartiles reported
     * @return {Boolean} !quartilesLen
    */
    function isAllQuartilesTriggered() {
       var quartilesLen = quartiles.length;
       quartiles.forEach(function forEachQuartile(quartile) {
           if(quartile.tracked){
               --quartilesLen;
            }
        });
        return !quartilesLen;
    }

    function isVideoCompleted(currentTime) {
        return ((isFyPlay && currentTime === 0) || (isAllQuartilesTriggered() && (currentTime >= minVideoCompleteDuration)));
    }

    function handleVideoCompleted() {
        video.removeEventListener('timeupdate', onTimeUpdate);
        video.removeEventListener('volumechange', onVolumeChange);
        video.removeEventListener('pause', onPause);
        video.removeEventListener('waiting', onWaiting);
        video.removeEventListener('canplay', onCanPlay);
        trigger({name: EVENT_VIDEO_COMPLETED});
    }
    
    function onVideoDurationAvailable(duration) {
        fullVideoDuration = duration.toFixed(2);
        minVideoCompleteDuration = (video.duration * 0.95).toFixed(2); //Video completed when was shown 95% of full duration
        initQuartiles(duration);
    }

    /**
     * register timeupdate and ended to notify to SDK
     * @param {HTMLVideoElement} video
     */
    function handleVideoEvents(video) {
        handleVideoQuartilesEvents(video);
    }

    /**
     * trigger the event to the SDK
     * @param {string} eventAndParams (the evnt name and data)
     */
    function trigger(eventAndParams) {
        FyLogger.log('--trigger-- ' + eventAndParams.name);
        onFyberVideoEvent.forEach(listener => {
            if (listener) {
                listener(eventAndParams);
            }
        });
        onFyberVideoEvent = onFyberVideoEvent.filter(listener => listener != null);
    }

    /**
     * create and iframe element to communicate with the sdk
     * @return {HTMLIFrameElement} ifr
     */
    function getSDKRedicectIfr() {
        var ifr = document.createElement('iframe');
        var container = document.body || document.documentElement;
        container.appendChild(ifr);
        ifr.setAttribute('style', 'position: fixed; bottom: -20px; border: none; visibility: hidden; height: 20px; z-index: -99999');
        return ifr;
    }

    /**
     * overrid the play method of HTMLVideoElement
     * sp we can control when to start the Video
     * @return {Promise|undefined} promise depend if the WebView support promise
     */
    function fyPlay() {
        var promise;
        if(!isFyPlay) {
            if(window.hasOwnProperty('Promise') && typeof window.Promise === 'function') {
                promise = new Promise(function handlePromise(res, rej) {
                    rej('FyMraidVideo://onlyPlayedByFySdk');
                });
            }
        } else if(nativePlayMethod) {
            promise = nativePlayMethod();
            if(promise) {
                promise.then(function onPlay() {
                    FyLogger.log('FyMraidVideo://onPlay');
                }).catch(function onPlayFailed() {
                    FyLogger.log('FyMraidVideo://onPlayFailed');
                });
            }
        }
        if(promise) {
            promise.catch(function onFyPlayCatch(err) {
                FyLogger.log('fyPlay --> ', err);
            });
        }
        return promise;
    }

    /**
     * override Video Methods currently only play
     * @param {HTMLVideoElement} video 
     */
    function overrideVideoMethods(video) {
        nativePlayMethod = video.play.bind(video);
        video.play = fyPlay;
    }
    
    /**
     * remove the overlay div after video start by the user or by SDK
     */
    function destroyOverlay() {
        if (overlay) {
            overlay.remove();
            overlay.removeEventListener('click', onOverlayClicked);
            overlay = null;
        }
    }

    /**
     * register overlay click event to start video
     */
    function onOverlayClicked() {
        FyberMraidVideoController.play();
    }

    /**
    * 
    * @param {HTMLVideoElement} video 
    */
    function initOverlay(video) {
        var computed = video.getBoundingClientRect();
        var width = computed.width;
        var height = computed.height;
        var top = computed.y;
        var left = computed.x;
        overlay = document.createElement('div');
        overlay.setAttribute('style', 'position: absolute; background: transparent; width: '+width+'; height: '+height+';top: '+top+'; left: '+left+';');
        overlay.className = "fy-video-overlay";
        document.body.appendChild(overlay);
        overlay.addEventListener('click', onOverlayClicked, {once: true});
    }

    function destroySDKPlayBtn() {
        if (sdkPlayBtn) {
            sdkPlayBtn.removeEventListener('click', onSDKPlayBtnClicked);
            sdkPlayBtn.remove();
            sdkPlayBtn = null;
        }
    }

    function onSDKPlayBtnClicked() {
        FyberMraidVideoController.play();
    }

    function appendSDKPlayBtn(video) {
        sdkPlayBtn = document.createElement('span');
        sdkPlayBtn.setAttribute('style', 'position: fixed; top: 0; left: 0; width: 30px; height: 30px; background: transparent;');
        sdkPlayBtn.addEventListener('click', onSDKPlayBtnClicked, {once: true});
        document.body.appendChild(sdkPlayBtn);
    }

    function onPlay(event) {
        var video = event.target;
        if(!isFyPlay && !FyberMraidVideoController.isPaused()) {
            FyLogger.log('onPlay --> FyberMraidVideoController -> onlyPlayedByFySdk');
            return video.pause();
        }
        video.removeEventListener(event.type, onPlay);
        destroyOverlay();
    }

    /**
     *
     * @param {HTMLVideoElement} video
     */
    function handleFyVideo(video) {
        isFyMraidVideoAd = true;
        appendSDKPlayBtn();
        var eventName = sdkPrefixProtocol + EVENT_FY_MRAID_VIDEO_AD;
        trigger({name: eventName});
        overrideVideoMethods(video);
        handleVideoEvents(video);
        initOverlay(video);
        video.addEventListener('play', onPlay);
        video.addEventListener('timeupdate', onPlay);
        /**
         * We need 'playing' listener to indicate that video already playing and our controller missed it cause 3d pparty lib started video after impression
         * and as a result we also do not pause it but our SDK's call play() wont trigget event listener registered to play event.
         */
        video.addEventListener('playing', onStartPlay, {once: true});
        video.addEventListener('play', onStartPlay, {once: true});
        video.addEventListener('pause', onPause);
        video.addEventListener('volumechange', onVolumeChange);
        video.addEventListener('waiting', onWaiting);
        video.addEventListener('timeupdate', onTimeUpdate);
        
        // document.onvisibilitychange = function(event) {
        // Possible way to detect skipped video TBD in future
        //     FyLogger.log('onVisibilityChange --> FyHtmlVideo -> document visible: ' + (document.visibilityState === 'visible'));
        // }
    }

    /**
     * load css rules that helps to detect video element inserted to dom
     */
    function loadCSS(targetdoc) {
        td = targetdoc == null ? document : targetdoc;
        var css = [
            '@keyframes '+animationName+' {',
            '   from { opacity: 0.99; }',
            '   to { opacity: 1; }',
            '}',
            'video {',
            '   width: 100%;',
            '   animation-duration: 0.001s;',
            '   animation-name: '+animationName+';',
            '}',
            'iframe {',
            '   animation-duration: 0.001s;',
            '   animation-name: '+animationName+';',
            '}'
        ].join('');
        var styleElm = td.createElement('style');
        styleElm.setAttribute('type', 'text/css');
        styleElm.appendChild(td.createTextNode(css));
        var tragetElement = td.body || td.documentElement;
        tragetElement.appendChild(styleElm);
    }

    /**
     * self invoking function, run when script load and detect if there is video element
     * then notify to SDK video exist
     * and override video methods to control when to start the video
     */
    (function detectIsVideoAd() {
        /**
         * wait to dom ready to seek video element
         */
        function onDomReady() {
            video = document.querySelector('video');
            if(!video) {
                loadCSS();
                return document.addEventListener('animationstart', onAnimationStartEvent);
            } else {
                onVideoElementFound(video);
            }
        }

        //check if the dom ready
        //if ready call to onDOmREady
        //else we will wait to DOMContentLoaded event
        if (document.readyState === 'loading') {
            return document.addEventListener('DOMContentLoaded', onDomReady, {once: true});
        }
        onDomReady();
    })();

    function onStartPlay(event) {
        FyLogger.log('onStartPlay --> FyberMraidVideoController -> eventType: ' + event.type);
        trigger({name: EVENT_VIDEO_START, duration: video.duration, volume: FyberMraidVideoController.volume()});
        video.removeEventListener('play', onStartPlay);
        video.removeEventListener('playing', onStartPlay);
    }

    function onPause(event) {
        FyLogger.log('onPause --> FyberMraidVideoController -> event: ' + event.type);
        trigger({name: EVENT_VIDEO_PAUSE});
        if (video) {
            video.addEventListener('playing', onPlaying, {once: true});
        }
    }

    function onPlaying(event) {
        FyLogger.log('onPlaying --> FyberMraidVideoController -> Video Resume Playing eventType: ' + event.type);
        trigger({name: EVENT_VIDEO_RESUME});
    }

    function onVolumeChange(event) {
        FyLogger.log('onVolumeChange --> FyberMraidVideoController -> event: ' + event.type + ' volume: ' + FyberMraidVideoController.volume());
        trigger({name: EVENT_VIDEO_VOLUME_CHANGE, volume: FyberMraidVideoController.volume()});
    }

    function onWaiting(event) {
        FyLogger.log('onWaiting --> FyberMraidVideoController -> event: ' + event.type);
        trigger({name:EVENT_VIDEO_BUFFER_START});
        if (video) {
            FyLogger.log('onWaiting --> FyberMraidVideoController -> add onCanPlay listener');
            video.removeEventListener('canplay', onCanPlay);
            video.addEventListener('canplay', onCanPlay, {once: true});
        }
    }

    function onCanPlay(event) {
        FyLogger.log('onCanPlay --> FyberMraidVideoController -> event: ' + event.type);
        trigger({name: EVENT_VIDEO_BUFFER_FINISH});
    }

    function onTimeUpdate(event) {
        handleQuartiles();
        var currentTime = video.currentTime.toFixed(2);
        if(isVideoCompleted(currentTime)) {
            handleVideoCompleted();
        }
    }

    function handleQuartiles() {
        quartiles.forEach(function forEachQuartile(quartile){
            if(!quartile.tracked && quartile.time <= video.currentTime){
                quartile.tracked = true;
                trigger({name: quartile.name});
            }
        });
    }

    function onVideoElementFound(videoElement) {
        video = videoElement;
        FyberMraidVideoController.registerEventListener(fyMraidControllerEventsHandler);
        onHtmlVideoElementReady.forEach(listener => {
            if (listener) {
                listener(video);
            }
        });
        onHtmlVideoElementReady = onHtmlVideoElementReady.filter(listener => listener != null);
        handleFyVideo(video);
    }

    function handleCssAnimationEvent(eventTarget) {
        switch (eventTarget.tagName) {
            case 'VIDEO':
                document.removeEventListener('animationstart', onAnimationStartEvent);
                onVideoElementFound(eventTarget);
                break;
            case 'IFRAME':
                var docElement = eventTarget.contentDocument;
                docElement.addEventListener('animationstart', onAnimationStartEvent)
                loadCSS(docElement);
                break;
        }
    }

    function onAnimationStartEvent(event) {
        if(event.animationName === animationName) {
            handleCssAnimationEvent(event.target);   
        }
    }

    function fyMraidControllerEventsHandler(event) {
        if (event && event.name && event.name.startsWith(sdkPrefixProtocol)) {
            FyberMraidVideoController.sendCmd(event.name);
        }
    }

    //the FyHtmlVideo api for the SDK
    return {
        /**
         * return if the ad include video element
         * @return {boolean} isFyHtmldVideoAd
         */
        isVideoAd() {
            return isFyMraidVideoAd;
        },

        setCurrentTime(currentTime) {
            if(video && currentTime) {
                video.currentTime = currentTime;
            }
        },

        /**
         * return the current time of the video element
         * @return {Number} currentTime
         */
        getCurrentTime() {
            return video && video.currentTime;
        },

        /**
         * check the state of the video play/pause
         * @return {boolean} paused
         */
        isPaused: function isPaused() {
            return video && video.paused;
        },

        /**
         * pause the video
         */
        pause: function pause() {
            if(video) {
                this.lastCurrentTime = this.getCurrentTime();
                video.pause();
            }
        },

        /**
        * check the state of the video mute/unmute
        * @return {boolean} muted
        */
        isMuted: function isMuted() {
            return video && video.muted;
        },

        /**
         * mute/unmute the video (depend on isMuted)
         * @param {boolean} isMuted 
         */
        mute: function mute(isMuted) {
            if(video){
                video.muted = isMuted;
                video.volume = isMuted ? 0 : 1;
            }
        },

        /**
         * start playing the video element
         */
        play: function play() {
            if (video) {
                isFyPlay = true;
                this.setCurrentTime(this.lastCurrentTime);
                video.play();
                destroyOverlay();
                destroySDKPlayBtn();
            }
        },

        volume: function volume() {
            return (video && !video.muted)? video.volume : 0;
        },

        videoElement() {
            return video;
        },

        registerEventListener: function registerEventListener(eventListener) {
            FyLogger.log('registerEventListener --> FyberMraidVideoController -> register fyber html video event listener');
            onFyberVideoEvent.push(eventListener);
        },

        removeEventListener: function removeEventListener(eventListener) {
            FyLogger.log('removeEventListener --> FyberMraidVideoController -> remove fyber html video event listener');
            var index = onFyberVideoEvent.indexOf(eventListener);
            if (index >= 0 && index < onFyberVideoEvent.length) {
                onFyberVideoEvent[index] = null;
            }
        },

        registerVideoElementReadyListener(listener) {
            onHtmlVideoElementReady.push(listener);
        },

        removeVideoElementReadyListener(listener) {
            var index = onHtmlVideoElementReady.indexOf(listener);
            if (index >= 0 && index < onHtmlVideoElementReady.length) {
                onHtmlVideoElementReady[index] = null;
            }
        },

        sendCmd: function sendCmd(cmd) {
            ifr.setAttribute('src', cmd);
        }
    };
})();

var FyberVideoCompletionEventHandler = (function adCompletionHandler() {
    (function detectIsVideoAd() {
        var videoElement = FyberMraidVideoController.videoElement(); 
        if (videoElement) {
            onVideoElementReady(videoElement);
        } else {
            FyberMraidVideoController.registerVideoElementReadyListener(onVideoElementReady);
        }
    })();

    function onVideoElementReady(videoElement) {
        FyLogger.log('onVideoElementReady ---> FyberVideoCompletionEventHandler -> ' + videoElement);
        FyberMraidVideoController.registerEventListener(onVideoEventHandler);
        FyberMraidVideoController.removeVideoElementReadyListener(onVideoElementReady);
    }

    function onVideoEventHandler(event) {
        FyLogger.log('onVideoEventHandler ---> FyberVideoCompletionEventHandler -> ' + event.name);
        if (event.name === EVENT_VIDEO_COMPLETED) {
            FyberMraidVideoController.sendCmd(sdkPrefixProtocol + EVENT_FY_MRAID_AD_COMPLETION);
        }
    }
})();