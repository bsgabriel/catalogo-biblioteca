package com.biblioteca.catalogo.ui.components;

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

    @Setter
    private Runnable onLimpar;

    /**
     * Construtor do componente
     *
     * @param titulo Título que será exibido na borda do painel
     */
    public PainelPesquisa(String titulo) {
        inicializarComponentes();
        configurarLayout(titulo);
        configurarEventos();
    }

    private void inicializarComponentes() {
        this.campoPesquisa = new JTextField();
        this.botaoPesquisar = new JButton("Pesquisar");
        this.botaoLimparPesquisa = new JButton("Limpar");
    }

    private void configurarLayout(String titulo) {
        this.setLayout(new FormLayout("right:pref, 5dlu, fill:150dlu:grow, 5dlu, pref, 5dlu, pref", "pref"));
        this.setBorder(new TitledBorder(titulo));

        this.add(new JLabel("Pesquisar:"), "1,1");
        this.add(campoPesquisa, "3,1");
        this.add(botaoPesquisar, "5,1");
        this.add(botaoLimparPesquisa, "7,1");
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

}