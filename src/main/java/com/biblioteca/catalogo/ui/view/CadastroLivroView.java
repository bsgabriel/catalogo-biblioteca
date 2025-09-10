package com.biblioteca.catalogo.ui.view;

import com.biblioteca.catalogo.ui.components.ListaAutores;
import com.biblioteca.catalogo.ui.components.PainelPesquisa;
import com.biblioteca.catalogo.ui.factory.InputFactory;
import com.github.lgooddatepicker.components.DatePicker;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import static com.biblioteca.catalogo.ui.factory.ButtonFactory.criarBotao;
import static com.biblioteca.catalogo.ui.factory.InputFactory.criarInputTitulo;
import static java.util.Objects.nonNull;
import static javax.swing.JOptionPane.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class CadastroLivroView extends JDialog {

    private PainelPesquisa inputISBN;

    private JTextField inputTitulo;
    private JTextField inputEditora;
    private DatePicker inputDataPublicacao;

    private JTextField inputAutor;
    private JButton btnAdicionarAutor;
    private ListaAutores lstAutores;
    private JButton btnRemoverAutor;

    private JButton btnSalvar;
    private JButton btnCancelar;
    private JButton btnLimpar;

    private JLabel labelStatus;
    private JProgressBar progressBar;

    public CadastroLivroView(JFrame parent) {
        super(parent, "Cadastro de Livro", true);
        inicializarComponentes();
        criarPainelPrincipal();
        configurarTela();
    }

    /**
     * Ação disparada pelo componente {@link #inputISBN}
     *
     * @param isbn Código Universal para buscar
     */
    protected abstract void buscarISBN(String isbn);

    /**
     * Ação disparada ao clicar no botão {@link #btnSalvar}
     */
    protected abstract void salvar();

    /**
     * Evento do botão {@link #btnCancelar}. Serve apenas para fechar a tela
     */
    protected void cancelar() {
        if (!possuiDadoInformado() || confirmarCancelar()) {
            this.dispose();
        }
    }

    /**
     * Configura a tela
     */
    private void configurarTela() {
        setSize(700, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelar();
            }
        });
    }

    /**
     * Instanciação dos componentes
     */
    private void inicializarComponentes() {
        inputISBN = new PainelPesquisa("ISBN", false);
        inputISBN.setOnPesquisar(this::buscarISBN);

        inputTitulo = InputFactory.criarInputTexto();
        inputEditora = InputFactory.criarInputTexto();
        inputDataPublicacao = InputFactory.criarInputData();
        inputAutor = InputFactory.criarInputTexto();

        lstAutores = new ListaAutores();
        lstAutores.setOnSelecaoAlterada(autor -> btnRemoverAutor.setEnabled(nonNull(autor)));

        btnAdicionarAutor = criarBotao("Adicionar", e -> adicionarAutor());
        btnRemoverAutor = criarBotao("Remover", e -> removerAutor());
        btnSalvar = criarBotao("Salvar", e -> salvar());
        btnCancelar = criarBotao("Cancelar", e -> cancelar());
        btnLimpar = criarBotao("Limpar", e -> limparTela());

        btnRemoverAutor.setEnabled(false);

        labelStatus = new JLabel("");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Carregando...");

        for (int i = 0; i < 20; i++) {
            lstAutores.adicionarAutor("Autor " + (i + 1));
        }
    }

    /**
     * Adiciona os componentes na janela
     */
    private void criarPainelPrincipal() {
        add(FormBuilder.create()
                .border(Paddings.DIALOG)
                .layout(new FormLayout("fill:pref:grow", "pref, 10dlu, fill:pref:grow, 10dlu, pref, 10dlu, pref"))
                .add(inputISBN).xy(1, 1)
                .add(criarPainelDadosLivro()).xy(1, 3)
                .add(criarPainelBotoes()).xy(1, 5)
                .add(criarPainelStatus()).xy(1, 7)
                .build());
    }

    /**
     * Cria formulário com dados do livro.
     *
     * @return Um {@link JPanel} com forumlários para preencher com dados do livro
     * @see CadastroLivroView#criarPainelDadosEsquerda()
     * @see CadastroLivroView#criarPainelDadosDireita()
     */
    private JPanel criarPainelDadosLivro() {
        return FormBuilder.create()
                .border(new TitledBorder("Dados do Livro"))
                .layout(new FormLayout("3dlu, 150dlu, 5dlu, fill:pref:grow, 3dlu", "3dlu, fill:pref:grow, 3dlu"))
                .add(criarPainelDadosEsquerda()).xy(2, 2)
                .add(criarPainelDadosDireita()).xy(4, 2)
                .build();
    }

    /**
     * Cria o painel dados do livro que ficam no lado esquerdo (título, editora, data de publicação)
     *
     * @return Um {@link JPanel} com dados o livro
     */
    private JPanel criarPainelDadosEsquerda() {
        return FormBuilder.create()
                .layout(new FormLayout("fill:pref:grow", "pref, 10dlu, pref, 10dlu, pref"))
                .add(criarInputTitulo(inputTitulo, "Titulo")).xy(1, 1)
                .add(criarInputTitulo(inputEditora, "Editora")).xy(1, 3)
                .add(criarInputTitulo(inputDataPublicacao, "Data de Publicação")).xy(1, 5)
                .build();
    }

    /**
     * Cria o painel dados do livro que ficam no lado direito (lista de autores + ações)
     *
     * @return Um {@link JPanel} com listagem de autores
     */
    private JPanel criarPainelDadosDireita() {
        JPanel pnlAdicionarAutor = FormBuilder.create()
                .layout(new FormLayout("fill:100dlu:grow, 5dlu, pref", "pref"))
                .add(inputAutor).xy(1, 1)
                .add(btnAdicionarAutor).xy(3, 1)
                .build();

        return FormBuilder.create()
                .layout(new FormLayout("fill:100dlu:grow, 5dlu, pref", "pref, 5dlu, fill:pref:grow, 5dlu, pref"))
                .add(InputFactory.criarInputTitulo(pnlAdicionarAutor, "Autor")).xyw(1, 1, 3)
                .addScrolled(lstAutores).xyw(1, 3, 3)
                .add(btnRemoverAutor).xyw(1, 5, 3)
                .build();
    }

    /**
     * Cria painel de botões de ação
     */
    private JPanel criarPainelBotoes() {
        return FormBuilder.create()
                .layout(new FormLayout("pref, 5dlu, pref, 10dlu, pref", "pref"))
                .add(btnSalvar).xy(1, 1)
                .add(btnCancelar).xy(3, 1)
                .add(btnLimpar).xy(5, 1)
                .build();
    }

    /**
     * Cria painel de status (barra de progresso)
     */
    private JPanel criarPainelStatus() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(labelStatus, BorderLayout.WEST);
        painel.add(progressBar, BorderLayout.CENTER);
        return painel;
    }

    /**
     * Ação disparada ao clicar no botão {@link #btnAdicionarAutor}
     */
    private void adicionarAutor() {
        String nomeAutor = inputAutor.getText();
        if (isBlank(nomeAutor)) {
            return;
        }

        lstAutores.adicionarAutor(nomeAutor);
        inputAutor.setText("");
    }

    /**
     * Ação disparada ao clicar no botão {@link #btnRemoverAutor}
     */
    private void removerAutor() {
        lstAutores.removerSelecionados();
    }

    private boolean confirmarCancelar() {
        int result = showConfirmDialog(this, "As alterações serão perdidas, deseja sair?", "Confirmação", YES_NO_OPTION);
        return result == YES_OPTION;
    }

    private boolean possuiDadoInformado() {
        return isNotBlank(getIsbn())
               || isNotBlank(getAutor())
               || isNotBlank(getEditora())
               || isNotBlank(getTitulo())
               || nonNull(getDataPublicacao())
               || !lstAutores.contemItem();
    }

    /**
     * Ação disparada ao clicar em {@link #btnLimpar}
     */
    protected final void limparTela() {
        inputISBN.limparCampo();
        inputAutor.setText("");
        inputEditora.setText("");
        inputTitulo.setText("");
        inputDataPublicacao.clear();

        lstAutores.limparRegistros();
    }

    protected String getIsbn() {
        return inputISBN.getText();
    }

    protected String getAutor() {
        return inputAutor.getText();
    }

    protected String getEditora() {
        return inputEditora.getText();
    }

    protected String getTitulo() {
        return inputTitulo.getText();
    }

    protected LocalDate getDataPublicacao() {
        return inputDataPublicacao.getDate();
    }



}

