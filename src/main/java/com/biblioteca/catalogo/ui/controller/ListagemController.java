package com.biblioteca.catalogo.ui.controller;

import com.biblioteca.catalogo.service.LivroService;
import com.biblioteca.catalogo.ui.view.ListagemView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListagemController extends ListagemView {

    private final LivroService livroService;

    public ListagemController() {
        super();
        this.livroService = new LivroService();
    }

    @Override
    protected void pesquisarLivro(String termo) {
        log.info("buscando pelo livro {}", termo);
        livroService.buscarPorTermo(termo);
    }

    @Override
    protected void adicionarLivro() {
        log.info("Vai abrir tela para cadastro de livro");
        livroService.salvar(null);
        CadastroLivroController controller = new CadastroLivroController(this);
        controller.setVisible(true);
    }

    @Override
    protected void atualizarListaLivros() {
        log.info("limpa a lista busca os livros novamente");
        livroService.buscarTodos();
    }

    @Override
    protected void editarLivro() {
        log.info("abrir tela de edição (tela de cadastro pré preenchida)");
        livroService.salvar(null);
    }

    @Override
    protected void removerLivro() {
        log.info("remover livro selecionado");
        livroService.deletar(null);
    }

    @Override
    protected void importarArquivo() {
        log.info("importar livro de arquivo");
        livroService.importarArquivo("");
    }

}