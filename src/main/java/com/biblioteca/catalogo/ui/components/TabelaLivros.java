package com.biblioteca.catalogo.ui.components;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.mapper.LivroMapper;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class TabelaLivros extends JTable {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_EXTENSO = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
    private final TableModelLivros tableModel;

    @Setter
    private Consumer<LivroDto> onSelecaoAlterada;

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

    public void adicionarRegistro(LivroDto livroDto) {
        tableModel.addRow(new Object[]{livroDto.getLivroId(), livroDto.getTitulo(), livroDto.getAutores(), livroDto.getDataPublicacao(), livroDto.getIsbn(), livroDto.getEditora()});
    }

    private void configurarTabela() {
        setModel(tableModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        configurarRenderers();
        configurarEventos();
        setRowHeight(30);
    }

    private void configurarEventos() {
        getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isNull(onSelecaoAlterada)) {
                return;
            }

            onSelecaoAlterada.accept(getLivroSelecionado());
        });
    }

    private void configurarRenderers() {
        //  id
        setarFormatacaoColuna(0, SwingConstants.CENTER, 40, 50, 80, null, null);

        // titulo
        setarFormatacaoColuna(1, SwingConstants.LEFT, 150, 250, Integer.MAX_VALUE, null, null);

        // autores
        setarFormatacaoColuna(2, SwingConstants.LEFT, 120, 200, Integer.MAX_VALUE, this::formatarValorAutores, this::formatarTooltipAutores);

        // data publicação
        setarFormatacaoColuna(3, SwingConstants.CENTER, 120, 120, 120, this::formatarValorDataPublicacao, this::formatarTooltipDataPublicacao);

        // isbn
        setarFormatacaoColuna(4, SwingConstants.CENTER, 100, 120, 150, null, null);

        // Editora
        setarFormatacaoColuna(5, SwingConstants.LEFT, 100, 150, Integer.MAX_VALUE, this::formatarValorEditora, this::formatarValorEditora);

    }

    private void setarFormatacaoColuna(int idxColuna, int alinhamento, Integer lMinima, Integer lPreferida, Integer lMaxima, Function<Object, String> formatacaoValor, Function<Object, String> formatacaoTooltip) {
        DefaultTableCellRenderer renderValor = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (isNull(value)) {
                    setText("");
                    setToolTipText("");
                    return;
                }

                if (isNull(formatacaoValor)) {
                    setText(value.toString());
                } else {
                    setText(formatacaoValor.apply(value));
                }

                if (isNull(formatacaoTooltip)) {
                    setToolTipText(value.toString());
                } else {
                    setToolTipText(formatacaoTooltip.apply(value));
                }
            }

        };
        renderValor.setHorizontalAlignment(alinhamento);

        TableColumn coluna = this.getColumnModel().getColumn(idxColuna);
        coluna.setCellRenderer(renderValor);
        coluna.setMinWidth(lMinima);
        coluna.setPreferredWidth(lPreferida);
        coluna.setMaxWidth(lMaxima);
    }

    private String formatarValorAutores(Object value) {
        List<AutorDto> autores = (List<AutorDto>) value;
        if (autores.isEmpty()) {
            return "";
        }

        return autores.stream()
                .map(AutorDto::getNome)
                .collect(Collectors.joining(", "));

    }

    private String formatarValorEditora(Object value) {
        return ((EditoraDto) value).getNome();
    }

    private String formatarValorDataPublicacao(Object value) {
        LocalDate dataPublicacao = (LocalDate) value;
        return dataPublicacao.format(FORMATO_DATA);
    }

    private String formatarTooltipDataPublicacao(Object value) {
        LocalDate dataPublicacao = (LocalDate) value;
        return dataPublicacao.format(FORMATO_DATA_EXTENSO);
    }

    private String formatarTooltipAutores(Object value) {
        List<AutorDto> autores = (List<AutorDto>) value;
        if (autores.isEmpty()) {
            return "";
        }

        String autoresBr = autores.stream()
                .map(AutorDto::getNome)
                .collect(Collectors.joining("<br>"));

        return "<html>" + autoresBr + "</html>";
    }

}
