package me.vanjavk.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

public class Director implements Transferable {
    public static final DataFlavor DIRECTOR_FLAVOR = new DataFlavor(Director.class, "Director");
    public static final DataFlavor[] SUPPORTED_FLAVORS = {DIRECTOR_FLAVOR};

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Director() {
    }

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Director(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Director) {
            return Objects.equals(name.toLowerCase(), (((Director) obj).name.toLowerCase()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name.toLowerCase());
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return SUPPORTED_FLAVORS;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DIRECTOR_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DIRECTOR_FLAVOR)) {
            return this;
        }
        throw new UnsupportedFlavorException(flavor);
    }

}
