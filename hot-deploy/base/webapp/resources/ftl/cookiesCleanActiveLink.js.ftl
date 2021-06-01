cookiesCleanActiveLink = {
    clean: function() {
        var jar = new CookieJar({path : "/"});
        jar.removeRegexp("activeLink");
    }
}

cookiesCleanActiveLink.clean();