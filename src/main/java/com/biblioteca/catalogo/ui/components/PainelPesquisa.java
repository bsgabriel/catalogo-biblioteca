package com.biblioteca.catalogo.ui.components;

import com.biblioteca.catalogo.ui.factory.InputFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

/**
 * Painel com campo de pesquisa, com botões de buscar e limpar
 */
public class PainelPesquisa extends JPanel {

    private JTextField campoPesquisa;
    private JButton botaoPesquisar;
    private JButton botaoLimparPesquisa;

    @Setter
    private Consumer<String> onPesquisar;

    /**
     * Gera um painel de pesquisa com o botão de limpar sendo exibido
     *
     * @param titulo Título que será exibido na borda do painel
     */
    public PainelPesquisa(String titulo) {
        this(titulo, true);
    }

    /**
     * Gera um painel de pesquisa permitindo esconder o botão de limpar
     *
     * @param titulo       Título que será exibido na borda do painel
     * @param exibirLimpar Define se o botão de limpar será exibido ou não
     */
    public PainelPesquisa(String titulo, boolean exibirLimpar) {
        inicializarComponentes();
        configurarLayout(titulo, exibirLimpar);
        configurarEventos();
    }

    private void inicializarComponentes() {
        this.campoPesquisa = InputFactory.criarInputTexto();
        this.botaoPesquisar = new JButton("Pesquisar");
        this.botaoLimparPesquisa = new JButton("Limpar");
    }

    private void configurarLayout(String titulo, boolean exibirLimpar) {
        String colunas = exibirLimpar ? "3dlu, fill:150dlu:grow, 5dlu, pref, 5dlu, pref, 3dlu" : "3dlu, fill:150dlu:grow, 5dlu, pref, 3dlu";
        String linhas = "3dlu, pref, 3dlu";
        this.setLayout(new FormLayout(colunas, linhas));
        this.setBorder(new TitledBorder(titulo));

        CellConstraints cc = new CellConstraints();
        this.add(campoPesquisa, cc.xy(2, 2));
        this.add(botaoPesquisar, cc.xy(4, 2));

        if (exibirLimpar) {
            this.add(botaoLimparPesquisa, cc.xy(6, 2));
        }
    }

    private void configurarEventos() {
        campoPesquisa.addActionListener(e -> executarPesquisa());
        botaoPesquisar.addActionListener(e -> executarPesquisa());

        botaoLimparPesquisa.addActionListener(e -> limparCampo());
    }

    private void executarPesquisa() {
        if (isNull(onPesquisar)) {
            return;
        }

        onPesquisar.accept(campoPesquisa.getText().trim());
    }

    public void limparCampo() {
        campoPesquisa.setText("");
        campoPesquisa.requestFocus();
    }

    /**
     * Habilita/desabilita o painel e seus componentes
     *
     * @param habilitado true para habilitar, false para desabilitar
     */
    @Override
    public void setEnabled(boolean habilitado) {
        super.setEnabled(habilitado);
        campoPesquisa.setEnabled(habilitado);
        botaoPesquisar.setEnabled(habilitado);
        botaoLimparPesquisa.setEnabled(habilitado);
    }

    /**
     * Método que retorna o texto digitado, sem espaços extras
     *
     * @return Uma {@link String} com o texto digitado
     */
    public String getText() {
        return campoPesquisa.getText().trim();
    }


}