<!--
	Visualizzatore di commenti / log nella parte HTML di un file .ftl.
	Per utilizzarlo:
	- Nel file .ftl dove si intende usarlo, nelle prime righe impostare
	
		<#import "/base/webapp/common/ftl/gzoomLogger.ftl" as gzoomLogger/>
		
	  NB: il nome dopo 'as' e' arbitrario ma si consiglia di mantenerlo cosi'
	- Nella parte HTML dove si vuole mostrare il messaggio mettere una cosa del tipo
		
		<@gzoomLogger.gzoomInfo message="[NOME_FILE.ftl] MESSAGGIO" type="all"/>
		
	- Le opzioni di 'type' sono:
		- (vuota): il default e' 'html-comment', il messaggio viene visualizzato nell'HTML come commento, 
		  cioe' visibile non all'utente ma solo in console
		- 'html-comment': vedi sopra
		- 'view': visualizzato cone testo sulla pagina. NB: da evitare in un rilascio
		- 'none' (o qualsiasi valore non coincidente coi precedenti): nessuna visualizzazione / log / console
-->

<#macro gzoomInfo message type="html-comment">
<#if ( ("${type}" == "view") || ("${type}" == "all") )>
	<p style="color:Orange;"><font size="-2">${message}</font><p>
</#if>
<#if ( ("${type}" == "html-comment") || ("${type}" == "all") )>	
	<!-- gzoomLogger.gzomInfo (type = '${type}'), message: ${message} -->
</#if>
</#macro>