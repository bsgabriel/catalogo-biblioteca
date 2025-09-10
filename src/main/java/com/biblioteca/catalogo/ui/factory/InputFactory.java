package com.biblioteca.catalogo.ui.factory;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;

@NoArgsConstructor
public final class InputFactory {

    /**
     * Cria um input com um título acima
     *
     * @param componente Input
     * @param titulo     Descrição que fica acima do input
     * @return Um {@link JComponent} contendo um input com um título logo acima
     */
    public static JComponent criarInputTitulo(JComponent componente, String titulo) {
        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout(0, 5));

        JLabel label = new JLabel(titulo);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        painel.add(label, BorderLayout.NORTH);
        painel.add(componente, BorderLayout.CENTER);
        return painel;
    }

    /**
     * Cria um input de texto com a seguinte padronização:
     * <br> -Altura: 25px
     *
     * @return Um {@link JTextField} padrão
     */
    public static JTextField criarInputTexto() {
        JTextField input = new JTextField();
        input.setPreferredSize(new Dimension(0, 25));
        return input;
    }


    /**
     * Cria um input de data com as seguintes padronizações:
     * <br> -Em ptbr
     * <br> -Formato de data brasileiro
     * <br> -Não permite digitação
     * <br> -Ícone de calendário
     *
     * @return Um {@link DatePicker } com configurações padrão
     */
    public static DatePicker criarInputData() {
        DatePickerSettings config = new DatePickerSettings();
        config.setLocale(new Locale("pt", "BR"));
        config.setFormatForDatesCommonEra("dd/MM/yyyy");
        config.setVisibleTodayButton(false);
        config.setAllowKeyboardEditing(false);

        URL dateImageURL = InputFactory.class.getResource("/images/datepickerbutton1.png");
        Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
        ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);

        DatePicker datePicker = new DatePicker(config);
        datePicker.getComponentToggleCalendarButton().setText("");
        datePicker.getComponentToggleCalendarButton().setIcon(dateExampleIcon);

        return datePicker;

    }


}
