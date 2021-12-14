package me.vanjavk;

import me.vanjavk.dal.Repository;
import me.vanjavk.dal.RepositoryFactory;
import me.vanjavk.model.Director;
import me.vanjavk.model.DirectorTableModel;
import me.vanjavk.utils.MessageUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class EditDirectorPanel{
    private List<JTextComponent> validationFields;
    private List<JLabel> errorLabels;

    private JPanel pnlEditDirector;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnAdd;
    private JTextField tfName;
    private JLabel lbDirector;
    private JLabel lbNameError;
    private JTable tbDirector;
    private DirectorTableModel directorTableModel;

    private Repository repository;

    private Director selected;

    public JPanel getPanel() {
        return pnlEditDirector;
    }

    public EditDirectorPanel() {

        try {
            repository = RepositoryFactory.getRepository();
            initValidation();
            initTable();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        btnAdd.addActionListener(e -> {
            try {
                if (!formValid()) {
                    MessageUtils.showErrorMessage("Input error", "Invalid input!");
                    return;
                }
                repository.createDirector(tfName.getText().trim());
                refresh();
            }catch (SQLException exception) {
                switch (exception.getErrorCode()) {
                    case 2627 -> MessageUtils.showWarningMessage("Action failed", "Director with same name already exists");
                    default -> MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
            }
            catch (Exception exception) {
                MessageUtils.showErrorMessage("Error", "Unknown error!");
                exception.printStackTrace();
            }
        });
        btnUpdate.addActionListener(e -> {
            try {
                if (selected == null) {
                    MessageUtils.showErrorMessage("Action error", "No director selected!");
                    return;
                }
                if (!formValid()) {
                    MessageUtils.showErrorMessage("Input error", "Invalid input!");
                    return;
                }
                selected.setName(tfName.getText().trim());
                repository.updateDirector(selected);
                refresh();
            }catch (SQLException exception) {
                 MessageUtils.showErrorMessage("Database error", "Unknown error!");
            }catch (Exception exception) {
                MessageUtils.showErrorMessage("Error", "Unknown error!");
                exception.printStackTrace();
            }
        });
        btnDelete.addActionListener(e -> {
            try {
                if (selected == null) {
                    MessageUtils.showErrorMessage("Action error", "No director selected!");
                    return;
                }
                repository.deleteDirector(selected.getId());
                refresh();
            }catch (SQLException exception) {
                switch (exception.getErrorCode()) {
                    case 547 -> MessageUtils.showErrorMessage("Database error", "Director is referenced in a movie! Remove all references to delete Director.");
                    default -> MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
            }
            catch (Exception exception) {
                MessageUtils.showErrorMessage("Error", "Unknown error!");
                exception.printStackTrace();
            }
        });

        tbDirector.getSelectionModel().addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                showDirector();
            }
        });

        tfName.addCaretListener(e -> formValid());
        pnlEditDirector.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
    }

    private void initTable() throws Exception {
        tbDirector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbDirector.setAutoCreateRowSorter(true);
        tbDirector.setRowHeight(25);
        directorTableModel = new DirectorTableModel(repository.selectDirectors());
        tbDirector.setModel(directorTableModel);
    }


    public void refresh(){
        try {
            directorTableModel.setDirectors(repository.selectDirectors());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        clearForm();
        formValid();
    }


    private void initValidation() {
        validationFields = Arrays.asList(tfName);
        errorLabels = Arrays.asList(lbNameError);
    }

    private void fillForm(Director director) {
        tfName.setText(director.getName());
    }

    public void showDirector() {
        try {
            try{
            selected = repository.selectDirector((int) directorTableModel.getValueAt(tbDirector.getRowSorter().convertRowIndexToModel(tbDirector.getSelectedRow()), 0));
            }catch (Exception exception){
                return;
            }
            fillForm(selected);
        } catch (Exception exception) {
            System.out.println(tbDirector.getSelectedRow());
            exception.printStackTrace();
            MessageUtils.showErrorMessage("Database error", "Unable to show directors!");
        }
    }

    private void clearForm() {
        validationFields.forEach(e -> e.setText(""));
        errorLabels.forEach(e -> e.setText(""));
        selected = null;
    }

    private boolean formValid() {
        boolean ok = true;

        for (int i = 0; i < validationFields.size(); i++) {
            ok &= !validationFields.get(i).getText().trim().isEmpty();
            errorLabels.get(i).setText(validationFields.get(i).getText().trim().isEmpty() ? "X" : "");
        }

        return ok;
    }

}
