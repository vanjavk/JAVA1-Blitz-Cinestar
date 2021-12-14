package me.vanjavk.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

public class Actor implements Transferable {
    public static final DataFlavor ACTOR_FLAVOR = new DataFlavor(Actor.class, "Actor");
    public static final DataFlavor[] SUPPORTED_FLAVORS = {ACTOR_FLAVOR};

    private int id;
    private String name;

    public Actor() { }

    public Actor(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Actor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Actor) {
            return Objects.equals(name.toLowerCase(), (((Actor) obj).name.toLowerCase()));
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
        return flavor.equals(ACTOR_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(ACTOR_FLAVOR)) {
            return this;
        }
        throw new UnsupportedFlavorException(flavor);
    }

}
