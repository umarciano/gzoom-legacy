<#assign svnInfo = Static["com.mapsengineering.base.svnutil.SvnInfoXmlReaderFactory"].newSvnInfoXmlReader()>
${uiLabelMap.BaseVersion}
<br/>
<#assign dummy = svnInfo.read("component://base/config/svninfo.xml")?if_exists>
${svnInfo.baseName(svnInfo.url)?default("?")}
(rev ${svnInfo.revision?default("?")})
${svnInfo.date?default("?")}
<br/>
<#assign dummy = svnInfo.read("component://custom/config/svninfo.xml")?if_exists>
custom
${svnInfo.baseName(svnInfo.dirName(svnInfo.dirName(svnInfo.url)))?default("?")}
(rev ${svnInfo.revision?default("?")})
${svnInfo.date?default("?")}
