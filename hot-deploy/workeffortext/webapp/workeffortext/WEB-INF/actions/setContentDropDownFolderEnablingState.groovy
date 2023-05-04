def contentDropDownEtchFieldName = 'etch';

if ("Y".equals(context.localeSecondarySet)) {
	contentDropDownEtchFieldName = 'etchLang';
}
context.contentDropDownEtchFieldName = contentDropDownEtchFieldName;

