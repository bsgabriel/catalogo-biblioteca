package com.biblioteca.catalogo;

import com.biblioteca.catalogo.database.config.DatabaseManager;
import com.biblioteca.catalogo.ui.controller.ListagemController;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Launcher {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.error("Não foi possível setar o tema da aplicação. Será usado o tema padrão", e);
        }

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