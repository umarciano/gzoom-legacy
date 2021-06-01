/** CookieEvents.java put parameters and cookie value in attribute of request
 * 	activeLink, oldWorkEffortTypeId, parentSelectedId and selectedId is manage in loadTreeView.js
 *  activeLink is delete in cookiesCleanActiveLink.js
 *  activeLink is manage in OnlyHeaderBreadCrumb.ftl, WorkeffortAsocExtView, WorkEffortachieve*, 
 *  activeLink is manage in tabs.js and WorkEffortNoteAndDataManagement, WorkEffortrootSnapShotView, WorkEffortMeasure, GeneralChangeAjaxUpdateAreaInterAppContextLink.js.ftl
 *  selectedRow is manage in cell-selection.js
 *  _VIEW_INDEX_ is manage in paginator.js
 * */
CleanCookie = {
	COOKIE_VIEW_INDEX_ : "_VIEW_INDEX_",
	COOKIE_PAGINATOR_NUMBER : "PAGINATOR_NUMBER",
	/** Id della riga nella tabella */
	COOKIE_SELECTED_ROW : "selectedRow",
	/** Id del tab attivo */
	COOKIE_ACTIVE_LINK : "activeLink",
	/** Id del nodo dell'albero attualmente selezionato	 */
	COOKIE_SELECTED_ID : "selectedId",
	/** Id del padre del nodo  dell'albero attualmente selezionato     */
    COOKIE_PARENT_SELECTED_ID : "parentSelectedId",
	/** ordinamento */
	COOKIE_SORT_FIELD : "sortField",
	
    load : function() {
        var jar = new CookieJar({path : "/"});
        var keys = jar.getKeys();
        keys.each(function(key) {
            if (key.match(CleanCookie.COOKIE_ACTIVE_LINK)) {
                jar.removeRegexp(CleanCookie.COOKIE_ACTIVE_LINK);
            } else if (key.match(CleanCookie.COOKIE_SELECTED_ROW)) {
            	jar.removeRegexp(CleanCookie.COOKIE_SELECTED_ROW);
            } else if (key.match(CleanCookie.COOKIE_VIEW_INDEX_)) {
                jar.removeRegexp(CleanCookie.COOKIE_VIEW_INDEX_);
            } else if (key.match(CleanCookie.COOKIE_SORT_FIELD)) {
              	jar.removeRegexp(CleanCookie.COOKIE_SORT_FIELD);
            } else if (key.match(CleanCookie.COOKIE_SELECTED_ID)) {
                jar.removeRegexp(CleanCookie.COOKIE_SELECTED_ID);
            } else if (key.match(CleanCookie.COOKIE_PARENT_SELECTED_ID)) {
                jar.removeRegexp(CleanCookie.COOKIE_PARENT_SELECTED_ID);
            } else if (key.match('oldWorkEffortTypeId')) {
                jar.removeRegexp('oldWorkEffortTypeId');
            }
        });
    },
    
    loadTreeView : function() {
        var jar = new CookieJar({path : "/"});
         var keys = jar.getKeys();
         keys.each(function(key) {
        	 
        	 if (key.match(CleanCookie.COOKIE_ACTIVE_LINK)) {
                 jar.removeRegexp(CleanCookie.COOKIE_ACTIVE_LINK);
             } else if (key.match(CleanCookie.COOKIE_SELECTED_ID)) {
                 jar.removeRegexp(CleanCookie.COOKIE_SELECTED_ID);
             } else if (key.match(CleanCookie.COOKIE_PARENT_SELECTED_ID)) {
                 jar.removeRegexp(CleanCookie.COOKIE_PARENT_SELECTED_ID);
             } else if (key.match('oldWorkEffortTypeId')) {
                 jar.removeRegexp('oldWorkEffortTypeId');
             } else if (key.match(CleanCookie.COOKIE_VIEW_INDEX_)) {
                 jar.removeRegexp(CleanCookie.COOKIE_VIEW_INDEX_);
             } 
        	 
         });
    }
}
document.observe("dom:loaded", CleanCookie.load);