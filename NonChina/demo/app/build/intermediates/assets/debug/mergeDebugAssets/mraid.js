
(function() {
    var mraid = window.mraid = {};
    var VERSION = mraid.VERSION = '2.0';

    // ===== Events =======================
    var Events = mraid.Events = {
        error: 'error',
        ready: 'ready',
        stateChange: 'stateChange',
        viewableChange: 'viewableChange',
        sizeChange: 'sizeChange',
        info: 'info'
    };

    // ===== Bridge interface to Each Platform =======================
    var isAndroid = navigator.userAgent.indexOf("Android") > 0;
    
    var executeNendSDK = (isAndroid ? nendSDK : window.webkit.messageHandlers);

    var bridgeFrom = (isAndroid
        ? window.nendsdkmraid = {
            readyNendSdk: false
        }
        : executeNendSDK.nendsdkmraid = {
            readyNendSdk: false
        }
    );

    bridgeFrom.notifyReadyEvent = function() {
        this.readyNendSdk = true;
        broadcastEvent(Events.ready);
    };

    bridgeFrom.notifyErrorEvent = function(message, action) {
        broadcastEvent(Events.error, message, action);
    };

    bridgeFrom.notifySizeChangeEvent = function(width, height) {
        if (this.lastSizeChangeProperties && width == this.lastSizeChangeProperties.width && height == this.lastSizeChangeProperties.height) {
            return;
        }

        this.lastSizeChangeProperties = {
            width: width,
            height: height
        };
        broadcastEvent(Events.sizeChange, width, height);
    };

    bridgeFrom.notifyState = function(_state) {
        state = _state;
        broadcastEvent(Events.info, 'Set state to ' + eventStringify(state));
        broadcastEvent(Events.stateChange, state);
    };

    bridgeFrom.notifyPlacementType = function(_placementType) {
        placementType = _placementType;
        broadcastEvent(Events.info, 'Set placement type to ' + eventStringify(placementType));
    };

    bridgeFrom.notifySupports = function(sms, tel, calendar, storePicture, inlineVideo) {
        supportProperties = {
            sms: sms,
            tel: tel,
            calendar: calendar,
            storePicture: storePicture,
            inlineVideo: inlineVideo
        };
    };

    bridgeFrom.notifyMaxSize = function(width, height) {
        maxSize = {
            width: width,
            height: height
        };

        expandProperties.width = width;
        expandProperties.height = height;

        broadcastEvent(Events.info, 'Set max size to ' + eventStringify(maxSize));
    };

    bridgeFrom.notifyDefaultPosition = function(x, y, width, height) {
        defaultPosition = {
            x: x,
            y: y,
            width: width,
            height: height
        };
        broadcastEvent(Events.info, 'Set default position to ' + eventStringify(defaultPosition));
    };

    bridgeFrom.notifyScreenSize = function(width, height) {
        screenSize = {
            width: width,
            height: height
        };
        broadcastEvent(Events.info, 'Set screen size to ' + eventStringify(screenSize));
    };

    bridgeFrom.notifyCurrentPosition = function(x, y, width, height) {
        currentPosition = {
            x: x,
            y: y,
            width: width,
            height: height
        };
        broadcastEvent(Events.info, 'Set current position to ' + eventStringify(currentPosition));
    };

    bridgeFrom.notifyChangeViewable = function(_isViewable) {
        isViewable = _isViewable;
        broadcastEvent(Events.info, 'Set isViewable to ' + eventStringify(isViewable));
        broadcastEvent(Events.viewableChange, isViewable);
    };

    // ===== Methods =======================

    /*
    parameters:
        • event – string, name of event to listen for
    • listener – function to execute return values:
        • none
    side effects:
        • none
    */
    mraid.addEventListener = function(event, listener) {
        if (!event || !listener) {
            broadcastEvent(Events.error, 'Both event and listener are required.', 'addEventListener');
        } else if (!contains(event, Events)) {
            broadcastEvent(Events.error, 'Unknown MRAID event: ' + event, 'addEventListener');
        } else {
            if (!listeners[event]) {
                listeners[event] = new EventListeners(event);
            }
            listeners[event].add(listener);
        }
    };

    mraid.createCalendarEvent = function() {
        console.log('`createCalendarEvent` does not support nendSDK');
    };

    /*
    parameters:
        • none
    return values:
        • none
    event triggered:
        • stateChange
    */
    mraid.close = function() {
        if (state === States.hidden) {
            broadcastEvent(Events.error, 'Ad cannot be closed when it is already hidden.', 'close');
        } else {
            if (isAndroid) {
                executeNendSDK.close();
            } else {
                executeNendSDK.mraidClose.postMessage('');
            }
        }
    };

    /*
    parameters:
        • URL (optional): The URL for the document to be displayed in a new overlay view.
          If null or a non-URL parameter is used, the body of the current ad will be used in the current webview.
    return values:
    • none
    events triggered:
        stateChange
    */
    mraid.expand = function(URL) {
        if (!(this.getState() === States.default || this.getState() === States.resized)) {
            broadcastEvent(Events.error, 'Ad can only be expanded from the default or resized state.', 'expand');
        } else {
            var json = JSON.stringify({
                 shouldUseCustomClose: expandProperties.useCustomClose,
                 url: URL
            });
            if (isAndroid) {
                executeNendSDK.expand(json);
            } else {
                executeNendSDK.mraidExpand.postMessage(json);
            }
        }
    };

    /*
    parameters:
        • none
    return value:
        • JavaScript Object - {x, y, width, height}: x=number of density-independent pixels offset
        from left edge of the rectangle defining getMaxSize(); y=number of density- independent pixels
        offset from top of the rectangle defining getMaxSize(); width=current width of container;
        height=current height of container (both measured in density-independent pixels)
    related events:
        • none
    */
    mraid.getCurrentPosition = function() {
        return {
            x: currentPosition.x,
            y: currentPosition.y,
            width: currentPosition.width,
            height: currentPosition.height
        };
    };
    var currentPosition = {};

    /*
    parameters:
        • none
    return values:
        • JavaScript Object - {x, y, width, height}: x=number of density-independent pixels offset
        from left of getMaxSize(); y=number of density-independent pixels offset from top of getMaxSize();
        width=current width of container; height=current height of container
    */
    mraid.getDefaultPosition = function() {
        return {
            x: defaultPosition.x,
            y: defaultPosition.y,
            width: defaultPosition.width,
            height: defaultPosition.height
        };
    };
    var defaultPosition = {};

    /*
    parameters:
        • none
    return values:
        • { ... } - this object contains the expand properties
    events triggered:
        • none
    */
    mraid.getExpandProperties = function() {
        var properties = {
            width: expandProperties.width,
            height: expandProperties.height,
            useCustomClose: expandProperties.useCustomClose,
            isModal: expandProperties.isModal
        };
        return properties;
    };
    var expandProperties = {
        width: false,
        height: false,
        useCustomClose: false,
        isModal: true
    };

    /*
    parameters:
        • none
    return value:
        • JavaScript Object, {width, height} - the maximum width and height the view can grow to
    related events:
        • none
    */
    mraid.getMaxSize = function() {
        return {
            width: maxSize.width,
            height: maxSize.height
        };
    };
    var maxSize = {};

    /*
    parameters:
        • none
    return values:
        • String: "inline", "interstitial"
    related events:
        • none
    */
    mraid.getPlacementType = function() {
        return placementType;
    };
    var placementType = 'unknown';

    /*
    parameters:
        • none
    return values:
        • { ... } - this object contains the resize properties
    events triggered:
        • none
    */
    mraid.getResizeProperties = function() {
        var properties = {
            width: resizeProperties.width,
            height: resizeProperties.height,
            offsetX: resizeProperties.offsetX,
            offsetY: resizeProperties.offsetY,
            customClosePosition: resizeProperties.customClosePosition,
            allowOffscreen: resizeProperties.allowOffscreen
        };
        return properties;
    };
    var resizeProperties = {
        width: false,
        height: false,
        offsetX: false,
        offsetY: false,
        customClosePosition: 'top-right',
        allowOffscreen: true
    };

    /*
    parameters:
        • none
    return values:
        • { ... } - this object contains the orientation properties events
    triggered:
        • none
    */
    mraid.getOrientationProperties = function() {
        return {
            allowOrientationChange: orientationProperties.allowOrientationChange,
            forceOrientation: orientationProperties.forceOrientation
        };
    };
    var orientationProperties = {
        allowOrientationChange: true,
        forceOrientation: "none"
    };

    /*
    parameters:
        • properties:
            JavaScript Object { ... } - this object contains
            the values for allowOrientationChange and forceOrientation.
    return values:
        • none
    events triggered:
        • none
    */
    mraid.setOrientationProperties = function(properties) {
        if (properties.hasOwnProperty('allowOrientationChange')) {
            orientationProperties.allowOrientationChange = properties.allowOrientationChange;
        }

        if (properties.hasOwnProperty('forceOrientation')) {
            orientationProperties.forceOrientation = properties.forceOrientation;
        }

        var json = JSON.stringify({
            allowOrientationChange: orientationProperties.allowOrientationChange,
            forceOrientation: orientationProperties.forceOrientation
        });
        if (isAndroid) {
            executeNendSDK.setOrientationProperties(json);
        } else {
            executeNendSDK.mraidSetOrientationProperties.postMessage(json);
        }
    };

    /*
    parameters:
        • none
    return values:
        • {width, height}
    related event:
        • none
    */
    mraid.getScreenSize = function() {
        return {
            width: screenSize.width,
            height: screenSize.height
        };
    };
    var screenSize = {};

    /*
    parameters:
        • none
    return values:
        • String: "loading", "default", "expanded”, “resized,” or “hidden”
    related events:
        • stateChange
    */
    mraid.getState = function() {
        return state;
    };

    var States = mraid.States = {
        loading: 'loading',
        default: 'default',
        expanded: 'expanded',
        hidden: 'hidden',
        resized: 'resized'
    };
    var state = States.loading;

    /*
    parameters:
        • none
    return values:
        • String – the MRAID version that this SDK is certified against by the IAB, or that this SDK is compliant with.
         For example, for the current version of MRAID, getVersion() will return “2.0.”
    */
    mraid.getVersion = function() {
        return mraid.VERSION;
    };

    /*
    parameters:
        • none
    return values:
        • boolean -
            true: container is on-screen and viewable by the user;
            false: container is off-screen and not viewable
    related events:
        • viewableChange
    */
    mraid.isViewable = function() {
        return isViewable;
    };
    var isViewable = false;

    /*
    parameters:
        • URL - String, the URL of the web page
    return values:
        • None
    */
    mraid.open = function(URL) {
        if (!URL) {
            broadcastEvent(Events.error, 'URL is required.', 'open');
            return;
        }
        var json = JSON.stringify({
             url: URL
        });
        if (isAndroid) {
            executeNendSDK.open(json);
        } else {
            executeNendSDK.mraidOpen.postMessage(json);
        }
    };

    /*
    parameters:
        • URI - String, the URI of the video or video stream
    return values:
        • none
    */
    mraid.playVideo = function(uri) {
        if (!mraid.isViewable()) {
            broadcastEvent(Events.error, 'playVideo cannot be called until the ad is viewable', 'playVideo');
            return;
        }
        if (!uri) {
            broadcastEvent(Events.error, 'playVideo must be called with a valid URI', 'playVideo');
        } else {
            var json = JSON.stringify({
                 uri: uri
            });
            if (isAndroid) {
                executeNendSDK.playVideo(json);
            } else {
                executeNendSDK.mraidPlayVideo.postMessage(json);
            }
        }
    };

    /*
    parameters:
        • event – string, name of event
    • listener – function to be removed return values:
        • none
    events triggered:
        • none
    */
    mraid.removeEventListener = function(event, listener) {
        if (!event) {
            broadcastEvent(Events.error, 'Event is required.', 'removeEventListener');
            return;
        }

        if (listener) {
            var success = false;
            if (listeners[event]) {
                success = listeners[event].remove(listener);
            }

            if (!success) {
                broadcastEvent(Events.error, 'Listener not currently registered for event.', 'removeEventListener');
                return;
            }

        } else if (!listener && listeners[event]) {
            listeners[event].removeAll();
        }

        if (listeners[event] && listeners[event].count === 0) {
            listeners[event] = null;
            delete listeners[event];
        }
    };

    /*
    parameters:
        • none
    return values:
        • none
    events triggered:
        • sizeChange, stateChange
    side effects:
        • changes state
    */
    mraid.resize = function() {
        if (!(this.getState() === States.default || this.getState() === States.resized)) {
            broadcastEvent(Events.error, 'Ad can only be resized from the default or resized state.', 'resize');
        } else if (!resizeProperties.width || !resizeProperties.height) {
            broadcastEvent(Events.error, 'Must set resize properties before calling resize()', 'resize');
        } else {
            var json = JSON.stringify({
                 width: resizeProperties.width,
                 height: resizeProperties.height,
                 offsetX: resizeProperties.offsetX || 0,
                 offsetY: resizeProperties.offsetY || 0,
                 customClosePosition: resizeProperties.customClosePosition,
                 allowOffscreen: !!resizeProperties.allowOffscreen
            });
            if (isAndroid) {
                executeNendSDK.resize(json);
            } else {
                executeNendSDK.mraidResize.postMessage(json);
            }
        }
    };

    /*
    parameters:
        • properties: JavaScript Object { ... } - this object contains the width and height of the expanded ad. For more info see properties object.
    return values:
        • none
    events triggered:
        • none
    */
    mraid.setExpandProperties = function(properties) {
        if (validate(properties, expandPropertyValidators, 'setExpandProperties', true)) {
            if (properties.hasOwnProperty('useCustomClose')) {
                expandProperties.useCustomClose = properties.useCustomClose;
            }
        }
    };
    var expandPropertyValidators = {
        useCustomClose: function(v) { return (typeof v === 'boolean'); },
    };

    /*
    parameters:
        • properties: JavaScript Object { ... } - this object contains the width and height of the resized ad,
         close position, offset direction (all in density-independent pixels), and whether the ad can resize offscreen.
          For more info see properties object.
    return values:
        • none
    events triggered:
        • none
    */
    mraid.setResizeProperties = function(properties) {
        if (validate(properties, resizePropertyValidators, 'setResizeProperties', true)) {
            var desiredProperties = ['width', 'height', 'offsetX', 'offsetY', 'customClosePosition', 'allowOffscreen'];
            var length = desiredProperties.length;

            for (var i = 0; i < length; i++) {
                var propname = desiredProperties[i];
                if (properties.hasOwnProperty(propname)) {
                    resizeProperties[propname] = properties[propname];
                }
            }
        }
    };
    /* Note of resizeProperties:
        Resize ads should be QA tested carefully. Ads that set parameters that
        are impossible for the container to follow will result in an error event
        being triggered and the resize will not take place.
        For example, an error will occur if an ad sets allowOffscreen to “false”
        but sets the width and height of the resize to be too big to actually fit on the screen.
    */
    var resizePropertyValidators = {
        width: function(v) {
            return !isNaN(v) && v > 0;
        },
        height: function(v) {
            return !isNaN(v) && v > 0;
        },
        offsetX: function(v) {
            return !isNaN(v);
        },
        offsetY: function(v) {
            return !isNaN(v);
        },
        customClosePosition: function(v) {
            return (typeof v === 'string' &&
            ['top-right', 'bottom-right', 'top-left', 'bottom-left', 'center', 'top-center', 'bottom-center'].indexOf(v) > -1);
        },
        allowOffscreen: function(v) {
            return (typeof v === 'boolean');
        }
    };

    mraid.storePicture = function() {
        console.log('`storePicture` does not support nendSDK');
    };

    /*
    parameters:
        String, name of feature
    return values:
        • Boolean – true, the feature is supported and getter and events are available; false, the feature is not supported on this device
    */
    mraid.supports = function(feature) {
        return supportProperties[feature];
    };
    var supportProperties = {
        sms: false,
        tel: false,
        calendar: false,
        storePicture: false,
        inlineVideo: false
    };

    /*
    parameters:
        • true – ad creative supplies its own design for the close indicator
        • false – container default image should be displayed for the close indicator
    return values:
        • none
    events triggered:
        • none
    */
    mraid.useCustomClose = function(shouldUseCustomClose) {
        expandProperties.useCustomClose = shouldUseCustomClose;
        //hasSetCustomClose = true;
        var json = JSON.stringify({
             shouldUseCustomClose: shouldUseCustomClose
        });
        if (isAndroid) {
            executeNendSDK.useCustomClose(json);
        } else {
            executeNendSDK.mraidUseCustomClose.postMessage(json);
        }
    };

    // ===== Handling processes belows =======================
    var EventListeners = function(event) {
        this.event = event;
        this.count = 0;
        var listeners = {};

        this.add = function(func) {
            var id = String(func);
            if (!listeners[id]) {
                listeners[id] = func;
                this.count++;
            }
        };

        this.remove = function(func) {
            var id = String(func);
            if (listeners[id]) {
                listeners[id] = null;
                delete listeners[id];
                this.count--;
                return true;
            } else {
                return false;
            }
        };

        this.removeAll = function() {
            for (var id in listeners) {
                if (listeners.hasOwnProperty(id)) this.remove(listeners[id]);
            }
        };

        this.broadcast = function(args) {
            for (var id in listeners) {
                if (listeners.hasOwnProperty(id)) listeners[id].apply(mraid, args);
            }
        };

        this.toString = function() {
            var out = [event, ':'];
            for (var id in listeners) {
                if (listeners.hasOwnProperty(id)) out.push('|', id, '|');
            }
            return out.join('');
        };
    };

    var listeners = {};

    var broadcastEvent = function() {
        var args = new Array(arguments.lezngth);
        var l = arguments.length;
        for (var i = 0; i < l; i++) args[i] = arguments[i];
        var event = args.shift();
        if (listeners[event]) listeners[event].broadcast(args);
    };

    var contains = function(value, array) {
        for (var i in array) {
            if (array[i] === value) return true;
        }
        return false;
    };

    var eventStringify = function(obj) {
        if (typeof obj === 'object') {
            var out = [];
            if (obj.push) {
                // Array.
                for (var p in obj) out.push(obj[p]);
                return '[' + out.join(',') + ']';
            } else {
                // Other object.
                for (var p in obj) out.push("'" + p + "': " + obj[p]);
                return '{' + out.join(',') + '}';
            }
        } else {
            return String(obj);
        }
    };

    var validate = function(obj, validators, action, merge) {
        if (!merge) {
            // Check to see if any required properties are missing.
            if (obj === null) {
                broadcastEvent(Events.error, 'Required object not provided.', action);
                return false;
            } else {
                for (var i in validators) {
                    if (validators.hasOwnProperty(i) && obj[i] === undefined) {
                        broadcastEvent(Events.error, 'Object is missing required property: ' + i, action);
                        return false;
                    }
                }
            }
        }

        for (var prop in obj) {
            var validator = validators[prop];
            var value = obj[prop];
            if (validator && !validator(value)) {
                // Failed validation.
                broadcastEvent(Events.error, 'Value of property ' + prop + ' is invalid: ' + value, action);
                return false;
            }
        }
        return true;
    };
}());
