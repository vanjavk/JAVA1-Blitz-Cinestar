package me.vanjavk;

import me.vanjavk.dal.Repository;
import me.vanjavk.dal.RepositoryFactory;
import me.vanjavk.model.Actor;
import me.vanjavk.model.ActorTableModel;
import me.vanjavk.utils.MessageUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class EditActorPanel{
    private List<JTextComponent> validationFields;
    private List<JLabel> errorLabels;

    private JPanel pnlEditActor;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnAdd;
    private JTextField tfName;
    private JLabel lbNameError;
    private JTable tbActor;
    private ActorTableModel actorTableModel;

    private JLabel lbActor;

    private Repository repository;

    private Actor selected;

    public JPanel getPanel() {
        return pnlEditActor;
    }

    public EditActorPanel( ) {

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
                repository.createActor(tfName.getText().trim());
                refresh();
            } catch (SQLException exception) {
                switch (exception.getErrorCode()) {
                    case 2627 -> MessageUtils.showWarningMessage("Action failed", "Actor with same name already exists");
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
                    MessageUtils.showErrorMessage("Action error", "No actor selected!");
                    return;
                }
                if (!formValid()) {
                    MessageUtils.showErrorMessage("Input error", "Invalid input!");
                    return;
                }
                selected.setName(tfName.getText().trim());
                repository.updateActor(selected);
                refresh();
            } catch (SQLException exception) {
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
                repository.deleteActor(selected.getId());
                refresh();
            } catch (SQLException exception) {
                switch (exception.getErrorCode()) {
                    case 547 -> MessageUtils.showErrorMessage("Database error", "Actor is referenced in a movie! Remove all references to delete Actor.");
                    default -> MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
            }
            catch (Exception exception) {
                MessageUtils.showErrorMessage("Error", "Unknown error!");
                exception.printStackTrace();
            }
        });

        tbActor.getSelectionModel().addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                showActor();
            }
        });

        tfName.addCaretListener(e -> formValid());
        pnlEditActor.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
    }

    private void initTable() throws Exception {
        tbActor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbActor.setAutoCreateRowSorter(true);
        tbActor.setRowHeight(25);
        actorTableModel = new ActorTableModel(repository.selectActors());
        tbActor.setModel(actorTableModel);
    }

    public void refresh(){
        try {
            actorTableModel.setActors(repository.selectActors());
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
    private void fillForm(Actor actor) {
        tfName.setText(actor.getName());
    }

    public void showActor() {
        try {
            try{
                selected = repository.selectActor((int) actorTableModel.getValueAt(tbActor.getRowSorter().convertRowIndexToModel(tbActor.getSelectedRow()), 0));

            }catch (Exception exception){
                return;
            }
            fillForm(selected);
        } catch (Exception exception) {
            System.out.println(tbActor.getSelectedRow());
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
