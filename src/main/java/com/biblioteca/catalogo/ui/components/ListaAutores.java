package com.biblioteca.catalogo.ui.components;

import com.biblioteca.catalogo.dto.AutorDto;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
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
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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

    }

    public void limparRegistros() {
        listModel.clear();
    }

    public void adicionarAutor(String nome) {
        listModel.addElement(AutorDto.builder()
                .nome(nome)
                .build());
    }

    @Override
    public void remove(int index) {
        listModel.remove(index);
    }

    public void removerSelecionados() {
        int[] selecionados = this.getSelectedIndices();
        for (int i = selecionados.length - 1; i >= 0; i--) {
            listModel.remove(selecionados[i]);
        }

    }

    private void configurarEventos() {
        addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isNull(onSelecaoAlterada)) {
                return;
            }

            onSelecaoAlterada.accept(getSelectedValue());
        });
    }

    public boolean contemItem() {
        return listModel.isEmpty();
    }
}
