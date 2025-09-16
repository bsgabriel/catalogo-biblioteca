package com.biblioteca.catalogo.ui.helper;

import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;

@NoArgsConstructor
public final class DialogHelper {


    /**
     * Exibeu uma dialog de erro com o título "Erro"
     *
     * @param root     Componente pai
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirErro(Window root, String mensagem) {
        exibirMensagem(root, "Erro", mensagem, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibeu uma dialog informativa com o título padrão "Aviso"
     *
     * @param root     Componente pai
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAviso(Window root, String mensagem) {
        exibirMensagem(root, "Aviso", mensagem, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibeu uma dialog de alerta com o título padrão "Alerta"
     *
     * @param root     Componente pai
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAlerta(Window root, String mensagem) {
        exibirMensagem(root, "Alerta", mensagem, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Exibeu uma dialog de erro
     *
     * @param root     Componente pai
     * @param titulo   Título da dialog
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirErro(Window root, String titulo, String mensagem) {
        exibirMensagem(root, titulo, mensagem, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibeu uma dialog informativa
     *
     * @param root     Componente pai
     * @param titulo   Título da dialog
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAviso(Window root, String titulo, String mensagem) {
        exibirMensagem(root, titulo, mensagem, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibeu uma dialog de alerta
     *
     * @param root     Componente pai
     * @param titulo   Título da dialog
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAlerta(Window root, String titulo, String mensagem) {
        exibirMensagem(root, titulo, mensagem, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Exibe uma dialog de mensagem simples
     *
     * @param root     Componente pai
     * @param titulo   Titulo da dialog
     * @param mensagem Mensagem a ser exibida
     * @param tipo     Tipo do dialog (erro, aviso ou warning)
     */
    private static void exibirMensagem(Window root, String titulo, String mensagem, int tipo) {
        JOptionPane.showMessageDialog(root, mensagem, titulo, tipo);
    }

    /**
     * Exibeu uma dialog de erro com o título "Erro", usando um {@link JTextArea}
     *
     * @param root     Componente pai
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirErroDetalhado(Window root, String mensagem) {
        exibirMensagemDetalhada(root, "Erro", mensagem, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibeu uma dialog informativa com o título padrão "Aviso", usando um {@link JTextArea}
     *
     * @param root     Componente pai
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAvisoDetalhado(Window root, String mensagem) {
        exibirMensagemDetalhada(root, "Aviso", mensagem, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibeu uma dialog de alerta com o título padrão "Alerta", usando um {@link JTextArea}
     *
     * @param root     Componente pai
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAlertaDetalhado(Window root, String mensagem) {
        exibirMensagemDetalhada(root, "Alerta", mensagem, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Exibeu uma dialog de erro, usando um {@link JTextArea}
     *
     * @param root     Componente pai
     * @param titulo   Título da dialog
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirErroDetalhado(Window root, String titulo, String mensagem) {
        exibirMensagemDetalhada(root, titulo, mensagem, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibeu uma dialog informativa, usando um {@link JTextArea}
     *
     * @param root     Componente pai
     * @param titulo   Título da dialog
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAvisoDetalhado(Window root, String titulo, String mensagem) {
        exibirMensagemDetalhada(root, titulo, mensagem, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibeu uma dialog de alerta, usando um {@link JTextArea}
     *
     * @param root     Componente pai
     * @param titulo   Título da dialog
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirAlertaDetalhado(Window root, String titulo, String mensagem) {
        exibirMensagemDetalhada(root, titulo, mensagem, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Exibe uma dialog com um {@link JTextArea} dentro de um {@link JScrollPane}. Usada para exibir informações mais complexas
     *
     * @param root     Componente pai
     * @param titulo   Titulo da dialog
     * @param mensagem Mensagem a ser exibida no TextArea
     * @param tipo     Tipo do dialog (erro, aviso ou warning)
     */
    private static void exibirMensagemDetalhada(Window root, String titulo, String mensagem, int tipo) {
        JTextArea area = new JTextArea(mensagem);
        area.setMargin(new Insets(15, 15, 15, 15));
        area.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(550, 500));
        JOptionPane.showMessageDialog(root, scrollPane, titulo, tipo);
    }

}
