 
 <#escape x as x?xml>
 <fo:table table-layout="fixed" width="100%" text-align="center" border-style="solid" border-color="black" border-width="1pt">
 <fo:table-column column-width="proportional-column-width(1)"/>
 <fo:table-column column-width="50mm"/>
 <fo:table-column column-width="proportional-column-width(1)"/>
 <fo:table-body>
   <fo:table-row>
     <fo:table-cell number-columns-spanned="2" border-style="solid" border-color="black" border-width="1pt">
       <fo:block>Titolo</fo:block>
     </fo:table-cell>
     <fo:table-cell number-rows-spanned="2" border-style="solid" border-color="black" border-width="1pt">
       <fo:block>Titolo</fo:block>
     </fo:table-cell>
   </fo:table-row>
   <fo:table-row>
     <fo:table-cell border-style="solid" border-color="black" border-width="1pt">
       <fo:block>Nome1</fo:block>
     </fo:table-cell>
     <fo:table-cell border-style="solid" border-color="black" border-width="1pt">
       <fo:block>Nome2</fo:block>
     </fo:table-cell>
   </fo:table-row>
 </fo:table-body>
</fo:table>
</#escape>