package com.mapsengineering.base.util.logging;

public class FormattedStringBuilder {

    private final StringBuilder buf;
    private final String indentation;
    private int indentLevel;
    private boolean needIndent;
    private StringBuilder indentationBuf;

    public FormattedStringBuilder() {
        this(null, null);
    }

    public FormattedStringBuilder(StringBuilder buf, String indentation) {
        this.buf = buf != null ? buf : new StringBuilder();
        this.indentation = (indentation != null && !indentation.isEmpty()) ? indentation : "    ";
        indentLevel = 0;
        needIndent = false;
        indentationBuf = new StringBuilder();
    }

    public String getIndentation() {
        return indentation;
    }

    public FormattedStringBuilder indent() {
        ++indentLevel;
        return this;
    }

    public FormattedStringBuilder unindent() {
        --indentLevel;
        return this;
    }

    public FormattedStringBuilder append(char c) {
        checkIndent();
        buf.append(c);
        return this;
    }

    public FormattedStringBuilder append(String s) {
        checkIndent();
        buf.append(toString(s));
        return this;
    }

    public FormattedStringBuilder append(Object obj) {
        checkIndent();
        buf.append(toString(obj));
        return this;
    }

    /**
     * Append a Name=Value pair.
     * 
     * @param name
     * @param value
     * @return this
     */
    public FormattedStringBuilder nv(String name, Object value) {
        checkIndent();
        buf.append(toString(name)).append('=').append(toString(value));
        return this;
    }

    public FormattedStringBuilder nl() {
        buf.append('\n');
        needIndent = true;
        return this;
    }

    public FormattedStringBuilder space() {
        return append(' ');
    }

    public FormattedStringBuilder tab() {
        return append('\t');
    }

    public FormattedStringBuilder comma() {
        return append(',');
    }

    public FormattedStringBuilder colon() {
        return append(':');
    }

    public FormattedStringBuilder semicolon() {
        return append(';');
    }

    public FormattedStringBuilder quote() {
        return append('"');
    }

    public FormattedStringBuilder equal() {
        return append('=');
    }

    public FormattedStringBuilder parensOpen() {
        return append('(');
    }

    public FormattedStringBuilder parensClose() {
        return append(')');
    }

    public FormattedStringBuilder bracketOpen() {
        return append('[');
    }

    public FormattedStringBuilder bracketClose() {
        return append(']');
    }

    public FormattedStringBuilder braceOpen() {
        return append('{');
    }

    public FormattedStringBuilder braceClose() {
        return append('}');
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    protected void checkIndent() {
        if (needIndent) {
            indentationBuf.setLength(0);
            int i = indentLevel;
            while (i-- > 0) {
                indentationBuf.append(indentation);
            }
            buf.append(indentationBuf);
            needIndent = false;
        }
    }

    protected String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        String str = obj.toString();
        return adjustIndentString(str);
    }

    protected String adjustIndentString(String str) {
        return str.replaceAll("\n", "\n" + indentationBuf.toString());
    }
}
