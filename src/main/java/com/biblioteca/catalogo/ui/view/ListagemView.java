package com.biblioteca.catalogo.ui.view;

import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.ui.components.PainelPesquisa;
import com.biblioteca.catalogo.ui.components.TabelaLivros;
import com.biblioteca.catalogo.ui.helper.DialogHelper;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static com.biblioteca.catalogo.ui.factory.ButtonFactory.criarBotao;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.swing.JOptionPane.*;

public abstract class ListagemView extends JFrame {

    private TabelaLivros tabelaLivros;

    private JButton botaoIncluir;
    private JButton botaoEditar;
    private JButton botaoDeletar;
    private JButton botaoImportar;

    private JLabel labelStatus;
    private JProgressBar progressBar;

    private PainelPesquisa painelPesquisa;

    private boolean resultadoFiltrado;

    public ListagemView() {
        inicializarComponentes();
        criarPainelPrincipal();
        configurarTela();
    }

    /**
     * Efetua a consulta de um livro
     *
     * @param termo Usado para efetuar a busca
     */
    protected abstract void pesquisarLivro(String termo);

    /**
     * Abre a tela de cadastro de livro
     */
    protected abstract void adicionarLivro();

    /**
     * Abre a tela de edição de livro
     */
    protected abstract void editarLivro(LivroDto livroDto);

    /**
     * Exclui o livro selecionado
     */
    protected abstract void removerLivro(LivroDto livroDto);

    /**
     * Abre a tela para importação de arquivo
     */
    protected abstract void importarArquivo();

    /**
     * Atualiza a lista de livros (remove e busca novamente)
     */
    protected abstract void atualizarListaLivros();

    /**
     * Configuração geral da tela (tamanho, título, etc)
     */
    private void configurarTela() {
        setTitle("Gerenciamento de Livros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }

    /**
     * Instanciação dos componentes
     */
    private void inicializarComponentes() {
        tabelaLivros = new TabelaLivros();

        botaoIncluir = criarBotao("Incluir", e -> adicionarLivro());
        botaoEditar = criarBotao("Editar", e -> {
            LivroDto livro = tabelaLivros.getLivroSelecionado();
            if (isNull(livro)) {
                DialogHelper.exibirAlerta(this, "Nenhum livro selecionado");
                return;
            }

            editarLivro(livro);
        });

        botaoDeletar = criarBotao("Deletar", e -> {
            LivroDto livro = tabelaLivros.getLivroSelecionado();
            if (isNull(livro)) {
                DialogHelper.exibirAlerta(this, "Nenhum livro selecionado");
                return;
            }

            if (!confirmarExclusao()) {
                return;
            }

            removerLivro(livro);
        });

        botaoImportar = criarBotao("Importar Arquivo", e -> importarArquivo());

        tabelaLivros.setOnSelecaoAlterada(livro -> {
            boolean possuiSelecao = nonNull(livro);
            botaoEditar.setEnabled(possuiSelecao);
            botaoDeletar.setEnabled(possuiSelecao);
        });

        labelStatus = new JLabel("");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Carregando...");

        painelPesquisa = new PainelPesquisa("Pesquisar Livros");
        painelPesquisa.setOnPesquisar(this::pesquisarLivro);
        painelPesquisa.setOnLimpar(() -> {
            if (resultadoFiltrado) {
                setResultadoFiltrado(false);
                atualizarListaLivros();
            }
        });
    }

    /**
     * Cria o painel principal (que fica no JFrame)
     */
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

    /**
     * Cria um JPanel para a tabela, com borda de título.
     *
     * @return Um @{@link JPanel} com a tabela
     */
    private JPanel criarPainelTabela() {
        return FormBuilder.create()
                .layout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
                .border(new TitledBorder("Lista de Livros"))
                .panel(new JPanel(new BorderLayout()))
                .addScrolled(tabelaLivros).xy(1, 1)
                .build();
    }

    /**
     * Cria o painel de ações (adicionar, editar, remover e importar livro).
     *
     * @return Um {@link JPanel} com botões de ação.
     */
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

    /**
     * Cria o painel com barrinha de carregamento
     *
     * @return Um {@link JPanel} com barra e label de status.
     */
    private JPanel criarPainelStatus() {
        return FormBuilder.create()
                .layout(new FormLayout("pref, 4dlu, fill:pref:grow", "pref"))
                .add(labelStatus).xy(1, 1)
                .add(progressBar).xy(3, 1)
                .build();
    }

    /**
     * Habilita ou desabilita os componentes.
     *
     * @param habilitar Um boolean que indica o estado dos campos
     */
    protected void habilitarCampos(boolean habilitar) {
        tabelaLivros.setEnabled(habilitar);
        botaoIncluir.setEnabled(habilitar);
        botaoEditar.setEnabled(habilitar && nonNull(tabelaLivros.getLivroSelecionado()));
        botaoDeletar.setEnabled(habilitar && nonNull(tabelaLivros.getLivroSelecionado()));
        botaoImportar.setEnabled(habilitar);
        painelPesquisa.setEnabled(habilitar);
    }

    /**
     * Exibe a barra de carregamento com uma mensagem informativa e desabilita os campos em tela
     *
     * @param mensagem Uma {@link String} que será exibida no carregamento
     */
    protected void habilitarCarregamento(String mensagem) {
        progressBar.setString(mensagem);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        habilitarCampos(false);
    }

    /**
     * Esconde a barra de carregamento, habilitando os campos em tela
     */
    protected void desabilitarCarregamento() {
        progressBar.setString("");
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        habilitarCampos(true);
    }

    /**
     * Altera o texto em {@link #labelStatus}
     *
     * @param msg Mensagem a ser exibida
     */
    protected void atualizarStatus(String msg) {
        labelStatus.setText(msg);
    }

    protected void adicionarLivroTabela(LivroDto livroDto) {
        tabelaLivros.adicionarRegistro(livroDto);
    }

    protected void limparTabela() {
        tabelaLivros.limparRegistros();
    }

    protected void limparCampoPesquisa() {
        painelPesquisa.setText("");
    }

    protected void setResultadoFiltrado(boolean aplicouFiltro) {
        resultadoFiltrado = aplicouFiltro;
    }

    private boolean confirmarExclusao() {
        int result = showConfirmDialog(this, "Deseja excluir o livro?", "Confirmação", YES_NO_OPTION);
        return result == YES_OPTION;
    }

}