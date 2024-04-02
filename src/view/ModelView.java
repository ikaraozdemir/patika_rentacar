package view;

import business.ModelManager;
import entity.Model;

import javax.swing.*;

public class ModelView extends Layout{
    private JPanel container;
    private JLabel lbl_heading;
    private JComboBox cmb_brand;
    private JTextField fld_model_name;
    private JTextField fld_model_year;
    private JComboBox cmb_mode_type;
    private JComboBox cmb_model_fuel;
    private JComboBox cmb_modei_gear;
    private JButton btn_mode_save;
    private Model model;
    private ModelManager modelManager;

    public ModelView ( Model model) {
        this.model = model;
        this.modelManager = new ModelManager();
        this.add(container);
        this.guiInitialize(300, 500);
    }
}
