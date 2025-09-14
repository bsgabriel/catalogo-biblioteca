package com.biblioteca.catalogo;

import com.biblioteca.catalogo.config.DatabaseManager;
import com.biblioteca.catalogo.ui.controller.ListagemController;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Launcher {

    public static void main(String[] args) {
         DatabaseManager.getInstance().inicializarBanco();

        SwingUtilities.invokeLater(() -> {
            try {
                ListagemController listagem = new ListagemController();
                listagem.setVisible(true);
            } catch (Exception e) {
                log.error("Erro ao iniciar aplicação", e);
            }
        });
    }

}