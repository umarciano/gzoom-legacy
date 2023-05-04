<#assign svnInfo = Static["com.mapsengineering.base.svnutil.SvnInfoXmlReaderFactory"].newSvnInfoXmlReader()>
${uiLabelMap.BaseVersion}
<br/>
<#assign dummy = svnInfo.read("component://base/config/project.info")?if_exists>
${svnInfo.version?default("?")}
${svnInfo.date?default("?")}
<br/>
<#assign dummy = svnInfo.read("component://custom/config/project.info")?if_exists>
custom
${svnInfo.version?default("?")}
${svnInfo.date?default("?")}
