package com.biblioteca.catalogo.ui.components.documentfilter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import static java.util.Objects.isNull;

public class DocumentFilterCaixaAlta extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        String texto = isNull(string) ? null : string.toUpperCase();
        super.insertString(fb, offset, texto, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        String texto = isNull(text) ? null : text.toUpperCase();
        super.replace(fb, offset, length, texto, attrs);
    }

}
