<html>
<head>
    <title>Condivisione Valutazione Completata</title>
</head>
<body>
    <script type="text/javascript">
        // Ricarica la pagina precedente
        if (window.opener && !window.opener.closed) {
            window.opener.location.reload();
            window.close();
        } else {
            // Se non c'è opener, reindirizza al portale "Mie performance" (NOPORTAL_MY)
            // Aggiungiamo un parametro timestamp per forzare il reload del contenuto dal server
            // Usa path assoluto per atterrare nella view VALUTAZIONE (menu GP_MENU_00139)
            var target = '/c/legacy/GP_MENU_00124/GP_MENU_00407/GP_MENU_00139' + '?_ts=' + new Date().getTime();
            // Forza la navigazione a livello top per evitare il caricamento dentro il container
            if (window.top && window.top !== window) {
                window.top.location.href = target;
            } else {
                window.location.href = target;
            }
        }
    </script>
    <p>Condivisione valutazione completata. Ricaricamento in corso...</p>
</body>
</html>
