package me.vanjavk.dal;

import me.vanjavk.dal.sql.SqlRepository;

public class RepositoryFactory {

    private RepositoryFactory() {
    }

    public static Repository getRepository()  {
        return new SqlRepository();
    }
}

