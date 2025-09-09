package com.biblioteca.catalogo.ui.components;

import com.biblioteca.catalogo.dto.EditoraDto;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class TableModelLivros extends DefaultTableModel {

    private static final String[] COLUNAS = {"ID", "Título", "Autores", "Data Publicação", "ISBN", "Editora"};

    public TableModelLivros() {
        super(COLUNAS, 0);
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Long.class;
            case 1: return String.class;
            case 2: return List.class;
            case 3: return Long.class;
            case 4: return LocalDate.class;
            case 5: return EditoraDto.class;
            default: return Object.class;
        }
    }
}
