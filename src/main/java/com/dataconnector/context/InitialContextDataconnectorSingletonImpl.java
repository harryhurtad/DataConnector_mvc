/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataconnector.context;

import com.dataconnector.constans.DataConnectorMessageException;
import com.dataconnector.exceptions.InitialCtxDataConnectorException;
import com.dataconnector.manager.AbstractDataConnectorManager;
import com.dataconnector.manager.DataConnector;
import com.dataconnector.manager.DataConnectorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.inject.Default;

/**
 *
 * @author proveedor_hhurtado
 */
@Default
@Singleton
public class InitialContextDataconnectorSingletonImpl implements InitialContextDataConnector {

    private InitialContextDataConnector initialContextDataConnector;

    public InitialContextDataconnectorSingletonImpl() {

    }

    @PostConstruct
    @Override
    public void initialContext() {

        try (
                InputStream in = InitialContextDataconnectorSingletonImpl.class.getResourceAsStream("/META-INF/DataConnector-conf.xml")) {
            String classContext = "com.dataconnector.context.InitialContextDataconnectorImpl";
            Class classInitialContext = Class.forName(classContext);
            initialContextDataConnector = (InitialContextDataConnector) classInitialContext.getConstructor(new Class[]{InputStream.class, ClassLoader.class}).newInstance(in, this.getClass().getClassLoader());
            initialContextDataConnector.initialContext();

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InitialCtxDataConnectorException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException ex) {
            Logger.getLogger(DataConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println("Si entro!!");
    }

    /**
     * Realiza la creación de la clase factory del DataConnector
     *
     * @param dataConnectorUnitName
     * @return
     * @throws com.dataconnector.exceptions.InitialCtxDataConnectorException
     */
    public DataConnectorFactory createDataConnectorFactory(String dataConnectorUnitName) throws InitialCtxDataConnectorException {

        if (initialContextDataConnector == null) {
            throw new InitialCtxDataConnectorException(DataConnectorMessageException.INITIAL_CTX_MSG_EXPTION);
        }

        return DataConnector.createDataConnectorFactory(dataConnectorUnitName);
    }

}
