WorkEffortRootSnapShotView = {
    load: function() {
        WorkEffortRootSnapShotView.loadSnapShotContext($$("a.WorkEffortRootSnapShotCtxButton")[0]);
    },
    
    loadSnapShotContext: function(ctxButton) {
        if(Object.isElement(ctxButton)) {
            ctxButton.onPreClickFunction = function(callback) {
                var jar = new CookieJar({path : "/"});
                jar.removeRegexp("activeLink");
                
                if (callback) {
                    callback();
                }
            }
        }
    }
}

WorkEffortRootSnapShotView.load();