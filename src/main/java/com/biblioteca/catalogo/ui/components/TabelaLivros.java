package com.biblioteca.catalogo.ui.components;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.mapper.LivroMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class TabelaLivros extends JTable {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final TableModelLivros tableModel;

    public TabelaLivros() {
        tableModel = new TableModelLivros();
        configurarTabela();
    }

    public LivroDto getLivroSelecionado() {
        int linha = this.getSelectedRow();
        if (linha == -1) {
            return null;
        }

        Object[] dados = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < dados.length; i++) {
            dados[i] = tableModel.getValueAt(linha, i);
        }

        return LivroMapper.arrayToLivroDto(dados);
    }

    public void limparRegistros() {
        tableModel.setRowCount(0);
    }

    public void adicionarRegistros(Object[] linha) {
        tableModel.addRow(linha);
    }

    private void configurarTabela() {
        setModel(tableModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        configurarRenderers();
    }

    private void configurarRenderers() {
        this.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (isNull(value)) {
                    setText("");
                    return;
                }

                List<AutorDto> autores = (List<AutorDto>) value;

                if (autores.isEmpty()) {
                    setText("");
                    return;
                }

                String nomes = autores.stream()
                        .map(AutorDto::getNome)
                        .collect(Collectors.joining(", "));

                setText((nomes));
            }
        });

        this.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (isNull(value)) {
                    setText("");
                    return;
                }

                LocalDate dataPublicacao = (LocalDate) value;
                setText((dataPublicacao.format(FORMATO_DATA)));
            }
        });

        this.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (isNull(value)) {
                    setText("");
                    return;
                }

                setText(((EditoraDto) value).getNome());
            }
        });
    }
}
