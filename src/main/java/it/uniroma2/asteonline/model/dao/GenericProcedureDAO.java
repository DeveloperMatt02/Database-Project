package it.uniroma2.asteonline.model.dao;

import it.uniroma2.asteonline.exception.DAOException;

public interface GenericProcedureDAO<P> {
    P execute(Object... params) throws DAOException;
}
