package com.biblioteca.catalogo.ui.components;

import com.biblioteca.catalogo.dto.AutorDto;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

public class ListaAutores extends JList<AutorDto> {

    private final DefaultListModel<AutorDto> listModel;

    @Setter
    private Consumer<AutorDto> onSelecaoAlterada;

    public ListaAutores() {
        this.listModel = new DefaultListModel<>();
        configurarLista();
        configurarEventos();
    }

    private void configurarLista() {
        setModel(listModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof AutorDto) {
                    AutorDto autor = (AutorDto) value;
                    setText(autor.getNome());
                }

                return this;
            }
        });

        addMouseListener(new MouseAdapter() {
            private int lastSelectedIndex = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                int clickedIndex = locationToIndex(e.getPoint());

                if (clickedIndex < 0) {
                    return;
                }

                if (clickedIndex == lastSelectedIndex && clickedIndex == getSelectedIndex()) {
                    clearSelection();
                    lastSelectedIndex = -1;
                } else {
                    setSelectedIndex(clickedIndex);
                    lastSelectedIndex = clickedIndex;
                }
            }
        });

    }

    public void limparRegistros() {
        listModel.clear();
    }

    public void adicionarAutor(AutorDto autor) {
        listModel.addElement(autor);
    }

    private void configurarEventos() {
        addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isNull(onSelecaoAlterada)) {
                return;
            }

            onSelecaoAlterada.accept(getSelectedValue());
        });
    }

}
