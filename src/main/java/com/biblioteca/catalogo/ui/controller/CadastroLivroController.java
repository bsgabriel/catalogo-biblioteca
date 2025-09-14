package com.biblioteca.catalogo.ui.controller;

import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.exception.ConsultaLivroException;
import com.biblioteca.catalogo.service.LivroService;
import com.biblioteca.catalogo.ui.view.CadastroLivroView;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@Slf4j
public class CadastroLivroController extends CadastroLivroView {

    private final LivroService livroService;
    private SwingWorker<LivroDto, Integer> buscaWorker;
    private SwingWorker<Void, Void> cadastroWorker;

    @Setter
    private LivroDto livroEdicao;

    @Setter
    private Consumer<LivroDto> onSalvar;

    /**
     * Abre a tela de cadastro de livro para editar um livro já existente
     *
     * @param parent       Tela que chamou essa
     * @param livroService Service para interação com livros
     * @see LivroService
     */
    public CadastroLivroController(JFrame parent, LivroService livroService) {
        super(parent);
        this.livroService = livroService;
    }

    @Override
    protected void buscarISBN(String isbn) {
        if (buscaWorker != null && !buscaWorker.isDone()) {
            buscaWorker.cancel(true);
        }

        if (isbn == null || isbn.trim().isEmpty()) {
            return;
        }

        limparDadosLivro();
        buscaWorker = new SwingWorker<LivroDto, Integer>() {

            @Override
            protected LivroDto doInBackground() throws ConsultaLivroException {
                return livroService.buscarLivroApi(isbn.trim());
            }

            @Override
            protected void done() {
                desabilitarCarregamento();

                if (isCancelled()) {
                    return;
                }

                try {
                    preencherDadosLivro(get());
                } catch (ExecutionException e) {
                    log.error("Erro ao buscar livro", e.getCause());
                    exibirDialogoErro(e.getCause().getMessage());
                } catch (InterruptedException e) {
                    log.error("Erro ao buscar livro", e);
                    exibirDialogoErro("Ocorreu um erro inesperado ao consultar o livro");
                }
            }
        };

        habilitarCarregamento("Buscando livro...", true);
        buscaWorker.execute();
    }

    @Override
    protected void salvar() {
        if (nonNull(onSalvar)) {
            onSalvar.accept(null);
        }

        if (nonNull(cadastroWorker) && !cadastroWorker.isDone()) {
            cadastroWorker.cancel(true);
        }

        final LivroDto livro = Optional.ofNullable(livroEdicao)
                .map(livroAtualizado -> {
                    livroAtualizado.setTitulo(getTitulo());
                    livroAtualizado.setIsbn(Long.valueOf(getIsbn()));
                    livroAtualizado.setAutores(getAutores());
                    livroAtualizado.setDataPublicacao(getDataPublicacao());
                    livroAtualizado.setEditora(EditoraDto.builder()
                            .nome(getEditora())
                            .build());
                    return livroAtualizado;
                }).orElse(LivroDto.builder()
                        .titulo(getTitulo())
                        .isbn(Long.valueOf(getIsbn()))
                        .autores(getAutores())
                        .dataPublicacao(getDataPublicacao())
                        .editora(EditoraDto.builder()
                                .nome(getEditora())
                                .build())
                        .build());

        cadastroWorker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws ConsultaLivroException {
                livroService.salvar(livro);
                return null;
            }

            @Override
            protected void done() {
                desabilitarCarregamento();

                try {
                    get();
                    exibirDialogoSucesso("Livro salvo com sucesso!");
                    limparTela();
                } catch (ExecutionException e) {
                    log.error("Erro ao buscar livro", e.getCause());
                    exibirDialogoErro(e.getCause().getMessage());
                } catch (InterruptedException e) {
                    log.error("Erro ao buscar livro", e);
                    exibirDialogoErro("Ocorreu um erro inesperado ao salvar o livro");
                }
            }
        };

        habilitarCarregamento("Salvando dados", false);
        cadastroWorker.execute();

    }

    @Override
    protected void cancelarBusca() {
        if (nonNull(buscaWorker)) {
            buscaWorker.cancel(true);
        }
    }

    @Override
    public void dispose() {
        if (buscaWorker != null && !buscaWorker.isDone()) {
            buscaWorker.cancel(true);
        }

        super.dispose();
    }
}
