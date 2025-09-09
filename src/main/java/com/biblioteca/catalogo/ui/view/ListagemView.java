package com.biblioteca.catalogo.ui.view;

import com.biblioteca.catalogo.ui.components.PainelPesquisa;
import com.biblioteca.catalogo.ui.components.TabelaLivros;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static com.biblioteca.catalogo.ui.factory.ButtonFactory.criarBotao;

public abstract class ListagemView extends JFrame {

    private TabelaLivros tabelaLivros;

    private JButton botaoIncluir;
    private JButton botaoEditar;
    private JButton botaoDeletar;
    private JButton botaoImportar;

    private JLabel labelStatus;
    private JProgressBar progressBar;

    private PainelPesquisa painelPesquisa;

    protected abstract void pesquisarLivro(String termo);

    protected abstract void adicionarLivro();

    protected abstract void editarLivro();

    protected abstract void removerLivro();

    protected abstract void importarArquivo();

    protected abstract void atualizarListaLivros();

    public ListagemView() {
        inicializarComponentes();
        criarPainelPrincipal();
        configurarTela();
    }

    private void configurarTela() {
        setTitle("Gerenciamento de Livros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }

    private void inicializarComponentes() {
        tabelaLivros = new TabelaLivros();

        botaoIncluir = criarBotao("Incluir", e -> adicionarLivro());
        botaoEditar = criarBotao("Editar", e -> editarLivro());
        botaoDeletar = criarBotao("Deletar", e -> removerLivro());
        botaoImportar = criarBotao("Importar Arquivo", e -> importarArquivo());

        labelStatus = new JLabel("Pronto");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Carregando...");

        painelPesquisa = new PainelPesquisa("Pesquisar Livros");
        painelPesquisa.setOnPesquisar(this::pesquisarLivro);
    }

    private void criarPainelPrincipal() {
        this.add(FormBuilder.create()
                .layout(new FormLayout("fill:pref:grow", "pref, 5dlu, fill:pref:grow, 5dlu, pref, 5dlu, pref"))
                .panel(new JPanel())
                .border(Paddings.createPadding("5dlu, 5dlu, 5dlu, 5dlu"))
                .add(painelPesquisa).xy(1, 1)
                .add(criarPainelTabela()).xy(1, 3)
                .add(criarPainelBotoes()).xy(1, 5)
                .add(criarPainelStatus()).xy(1, 7)
                .build());
    }

    private JPanel criarPainelTabela() {
        return FormBuilder.create()
                .layout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
                .border(new TitledBorder("Lista de Livros"))
                .panel(new JPanel(new BorderLayout()))
                .addScrolled(tabelaLivros).xy(1, 1)
                .build();
    }

    private JPanel criarPainelBotoes() {
        return FormBuilder.create()
                .layout(new FormLayout("pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref", "pref"))
                .panel(new JPanel())
                .add(botaoIncluir).xy(1, 1)
                .add(botaoEditar).xy(3, 1)
                .add(botaoDeletar).xy(5, 1)
                .add(botaoImportar).xy(7, 1)
                .build();
    }

    private JPanel criarPainelStatus() {
        return FormBuilder.create()
                .layout(new FormLayout("pref, 4dlu, fill:pref:grow", "pref"))
                .add(labelStatus).xy(1, 1)
                .add(progressBar).xy(3, 1)
                .build();
    }

}