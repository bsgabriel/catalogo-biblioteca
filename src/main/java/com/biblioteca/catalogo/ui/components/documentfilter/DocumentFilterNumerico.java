package com.biblioteca.catalogo.ui.components.documentfilter;

import org.apache.commons.lang3.StringUtils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocumentFilterNumerico extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

        if (valorValido(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        if (valorValido(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean valorValido(String text) {
        if (StringUtils.isBlank(text)) {
            return true;
        }
        return text.matches("\\d+");
    }
}
