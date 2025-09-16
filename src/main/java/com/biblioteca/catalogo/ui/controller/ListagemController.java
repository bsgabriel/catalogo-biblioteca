package com.biblioteca.catalogo.ui.controller;

import com.biblioteca.catalogo.dto.DadosImportacaoCsvDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.service.LivroService;
import com.biblioteca.catalogo.ui.helper.DialogHelper;
import com.biblioteca.catalogo.ui.view.ListagemView;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class ListagemController extends ListagemView {

    private final LivroService livroService;
    private SwingWorker<DadosImportacaoCsvDto, Void> workerImportacaoCsv;
    private SwingWorker<List<LivroDto>, Void> workerBuscaLivro;

    public ListagemController() {
        super();
        this.livroService = new LivroService();
        atualizarListaLivros();
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
        buscarLivro("");
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

    private void buscarLivro(String termo) {
        if (nonNull(workerBuscaLivro) && !workerBuscaLivro.isDone()) {
            workerBuscaLivro.cancel(true);
        }

        String msg = isBlank(termo) ? "Carregando livros" : String.format("Buscando por: %s", termo);
        habilitarCarregamento(msg);
        workerBuscaLivro = criarWorkerConsultaLivro(termo);
        workerBuscaLivro.execute();
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
                    DialogHelper.exibirErro(ListagemController.this, "Erro", "Ocorreu um erro ao realizar a importação. Verifique o log para mais detalhes");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                if (dadosImportacao.getTotalLinhas() == 0) {
                    sb.append("Nenhum livro importado");
                    DialogHelper.exibirAviso(ListagemController.this, sb.toString());
                    return;
                }

                String titulo = "Importação finalizada";
                int qtdSucesso = dadosImportacao.getLivrosImportados().size();
                int qtdErro = dadosImportacao.getErros().size();

                // apenas sucesso, sem erro
                if (qtdSucesso > 0 && qtdErro == 0) {
                    sb.append("Livros importados com sucesso: ").append(qtdSucesso).append("\n");
                    sb.append("Nenhum livro com erro\n");
                    DialogHelper.exibirAvisoDetalhado(ListagemController.this, titulo, sb.toString());
                    return;
                }

                // apenas erro, sem sucesso
                if (qtdSucesso == 0 && qtdErro > 0) {
                    sb.append("Nenhum livro pôde ser importado devido à erros na importação:\n");
                    dadosImportacao.getErros().forEach(e -> sb.append("- ").append(e).append("\n"));
                    DialogHelper.exibirErroDetalhado(ListagemController.this, titulo, sb.toString());
                    return;
                }

                // sucesso e erro
                sb.append("Livros importados com sucesso: ").append(qtdSucesso).append("\n");
                sb.append("Livros com erro de importação: ").append(qtdErro).append("\n\n");
                sb.append("Erros:\n");
                dadosImportacao.getErros().forEach(e -> sb.append("- ").append(e).append("\n"));
                DialogHelper.exibirAlertaDetalhado(ListagemController.this, titulo, sb.toString());
            }
        };
    }

    private SwingWorker<List<LivroDto>, Void> criarWorkerConsultaLivro(String termo) {
        return new SwingWorker<List<LivroDto>, Void>() {
            @Override
            protected List<LivroDto> doInBackground() {
                if (isBlank(termo)) {
                    log.info("Nenhum termo informado. Buscando todos os livros");
                    return livroService.buscarTodos();
                } else {
                    log.info("Buscando livros pelo termo '{}'", termo);
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                limparTela();
                desabilitarCarregamento();

                List<LivroDto> livros;
                try {
                    livros = get();
                } catch (Exception e) {
                    log.error("Erro ao buscar livros", e);
                    DialogHelper.exibirErro(ListagemController.this, "Ocorreu um erro ao buscar livros");
                    return;
                }

                if (livros.isEmpty()) {
                    atualizarStatus("Nenhum livro encontrado");
                    return;
                }

                livros.forEach(livro -> adicionarLivroTabela(livro));
                atualizarStatus(String.format("Foram encontrados %d registro(s)", livros.size()));
            }
        };
    }


}