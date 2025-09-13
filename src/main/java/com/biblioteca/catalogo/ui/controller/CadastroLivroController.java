package com.biblioteca.catalogo.ui.controller;

import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.service.LivroService;
import com.biblioteca.catalogo.ui.view.CadastroLivroView;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import static java.util.Objects.nonNull;

@Slf4j
public class CadastroLivroController extends CadastroLivroView {

    private final LivroService livroService;
    private SwingWorker<LivroDto, Integer> buscaWorker;

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
            protected LivroDto doInBackground() throws Exception {
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
                } catch (Exception e) {
                    log.error("Erro ao buscar livro", e);
                }
            }
        };

        habilitarCarregamento();
        buscaWorker.execute();
    }

    @Override
    protected void salvar() {

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
