package me.vanjavk;

import me.vanjavk.dal.Repository;
import me.vanjavk.dal.RepositoryFactory;
import me.vanjavk.model.Movies;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ExportPanel {
    private JButton btnExport;
    private JPanel pnlExport;
    private JLabel lbExport;

    private Repository repository;

    public JPanel getPanel() {
        return pnlExport;
    }
    public ExportPanel() {
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbExport.setText("Exporting...");
                try {
                    repository = RepositoryFactory.getRepository();
                    JAXBContext context = JAXBContext.newInstance(Movies.class);
                    Marshaller marshaller = context.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    Movies movies = new Movies(repository.selectMovies());
                    marshaller.marshal(movies, new File("export.xml"));
                    lbExport.setText("Done...");
                } catch (Exception exception) {
                    lbExport.setText("Error!");
                    exception.printStackTrace();
                }

            }
        });
    }
}
