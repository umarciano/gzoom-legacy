res = "success";

//casi con sempre ricerca automatica
listaEntityAlwaysStartSearch = ["QueryConfigView"];

if (listaEntityAlwaysStartSearch.contains(parameters.entityName)) {
	res = "succesStartSearch";
} 

return res;