package com.biblioteca.catalogo.ui.controller;

import com.biblioteca.catalogo.dto.DadosImportacaoCsvDto;
import com.biblioteca.catalogo.service.LivroService;
import com.biblioteca.catalogo.ui.view.ListagemView;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public class ListagemController extends ListagemView {

    private final LivroService livroService;
    private SwingWorker<DadosImportacaoCsvDto, Void> workerImportacaoCsv;

    public ListagemController() {
        super();
        this.livroService = new LivroService();
    }

    @Override
    protected void pesquisarLivro(String termo) {
        log.info("buscando pelo livro {}", termo);
    }

    @Override
    protected void adicionarLivro() {
        log.info("Vai abrir tela para cadastro de livro");
        CadastroLivroController controller = new CadastroLivroController(this, livroService);
        controller.setVisible(true);
    }

    @Override
    protected void atualizarListaLivros() {
        log.info("limpa a lista busca os livros novamente");
    }

    @Override
    protected void editarLivro() {
        log.info("abrir tela de edição (tela de cadastro pré preenchida)");
    }

    @Override
    protected void removerLivro() {
        log.info("remover livro selecionado");
    }

    @Override
    protected void importarArquivo() {
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivos CSV (*.csv)", "csv");

        JFileChooser seletor = new JFileChooser();
        seletor.setFileFilter(filtro);
        seletor.showOpenDialog(this);

        File arquivo = seletor.getSelectedFile();
        if (isNull(arquivo)) {
            log.info("Nenhum arquivo selecionado");
            return;
        }

        if (nonNull(workerImportacaoCsv) && !workerImportacaoCsv.isDone()) {
            workerImportacaoCsv.cancel(true);
        }

        workerImportacaoCsv = criarWorkerProcessamentoCsv(arquivo);
        habilitarCarregamento("Importando livros");
        workerImportacaoCsv.execute();
    }

    private SwingWorker<DadosImportacaoCsvDto, Void> criarWorkerProcessamentoCsv(File arquivo) {
        return new SwingWorker<DadosImportacaoCsvDto, Void>() {
            @Override
            protected DadosImportacaoCsvDto doInBackground() {
                DadosImportacaoCsvDto dadosImportacao = Optional.ofNullable(livroService.carregarDadosCSV(arquivo)).orElse(DadosImportacaoCsvDto.builder().build());
                dadosImportacao.getLivrosImportados().forEach(livroService::salvar);
                return dadosImportacao;
            }

            @Override
            protected void done() {
                desabilitarCarregamento();

                DadosImportacaoCsvDto dadosImportacao;
                try {
                    dadosImportacao = Optional.ofNullable(get()).orElse(DadosImportacaoCsvDto.builder().build());
                } catch (Exception e) {
                    log.error("Erro ao realizar importação", e);
                    exibirMensagemErro("Erro", "Ocorreu um erro ao realizar a importação. Verifique o log para mais detalhes");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                if (dadosImportacao.getTotalLinhas() == 0) {
                    sb.append("Nenhum livro importado");
                    exibirMensagemAviso("Aviso", sb.toString());
                    return;
                }

                String titulo = "Importação finalizada";
                int qtdSucesso = dadosImportacao.getLivrosImportados().size();
                int qtdErro = dadosImportacao.getErros().size();

                // apenas sucesso, sem erro
                if (qtdSucesso > 0 && qtdErro == 0) {
                    sb.append("Livros importados com sucesso: ").append(qtdSucesso).append("\n");
                    sb.append("Nenhum livro com erro\n");
                    exibirMensagemInformativo(titulo, sb.toString());
                    return;
                }

                // apenas erro, sem sucesso
                if (qtdSucesso == 0 && qtdErro > 0) {
                    sb.append("Nenhum livro pôde ser importado devido à erros na importação:\n");
                    dadosImportacao.getErros().forEach(e -> sb.append("- ").append(e).append("\n"));
                    exibirMensagemErro(titulo, sb.toString());
                    return;
                }

                // sucesso e erro
                sb.append("Livros importados com sucesso: ").append(qtdSucesso).append("\n");
                sb.append("Livros com erro de importação: ").append(qtdErro).append("\n\n");
                sb.append("Erros:\n");
                dadosImportacao.getErros().forEach(e -> sb.append("- ").append(e).append("\n"));
                exibirMensagemAviso(titulo, sb.toString());
            }
        };
    }


}