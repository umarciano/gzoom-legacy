<html>
<head>
    <title>Presa Visione Completata</title>
</head>
<body>
    <script type="text/javascript">
        // Ricarica la pagina precedente (se aperta come popup)
        if (window.opener && !window.opener.closed) {
            window.opener.location.reload();
            window.close();
        } else {
            try {
                if (window.top && window.top !== window) {
                    try {
                        window.top.location.reload();
                    } catch (e) {
                        window.top.location.href = window.top.location.pathname + '?_ts=' + new Date().getTime();
                    }
                } else {
                    // Ricarica la stessa pagina
                    try {
                        window.location.reload();
                    } catch (e) {
                        // Fallback: naviga all'URL corrente con timestamp per forzare fetch dal server
                        window.location.href = window.location.pathname + '?_ts=' + new Date().getTime();
                    }
                }
            } catch (outer) {
                // In caso di errori imprevisti, proviamo comunque a tornare indietro nella cronologia
                if (window.history && window.history.length > 1) {
                    window.history.back();
                }
            }
        }
    </script>
    <p>Presa visione scheda completata. Ricaricamento in corso...</p>
</body>
</html>
