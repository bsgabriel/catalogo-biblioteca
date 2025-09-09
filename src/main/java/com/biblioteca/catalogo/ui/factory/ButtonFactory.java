package com.biblioteca.catalogo.ui.factory;

import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.event.ActionListener;

@NoArgsConstructor
public final class ButtonFactory {

    /**
     * Cria um botão, setando título e ação
     *
     * @param titulo Título do botão
     * @param acao   Ação a ser executada pelo botão
     * @return Um {@link JButton} com título e ação especificados
     */
    public static JButton criarBotao(String titulo, ActionListener acao) {
        JButton botao = new JButton(titulo);
        botao.addActionListener(acao);
        return botao;
    }

}
