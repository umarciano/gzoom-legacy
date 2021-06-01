<div class="customMethodMatrix" style="padding-top: 2em;">
    <table id="customMethodMatrixTable" class="basic-table list-table padded-row-table hover-bar resizable  customizable headerFixable multi-editable" cellspacing="0" style="background-color:#D9D9D9;">
        <thead>
            <tr class="header-row-2">
            	<th>  </th>
                <#list listHeaderColumn as headerColumn>
                	<th > ${headerColumn}  </th>
                </#list>
            </tr>
        </thead>
        <tbody style="height: auto;">
        	<#list listMatrix as element>
                <tr>
                	<td style="text-align: center; font-weight: bold;">  ${element.rowInputValue?if_exists}</td>
                	<#list listHeaderColumn as headerColumn>
                	<td style="text-align: right;"> ${element.get(headerColumn)?if_exists}  </td>
                	</#list>
                </tr>
            </#list>        
        </tbody>
    </table>
</div>
