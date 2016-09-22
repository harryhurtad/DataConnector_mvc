/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataconnector.commons.xml;

import com.dataconnector.commons.anotations.CollectionInfoDataConnector;
import com.dataconnector.commons.helper.DataConnectorHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Clase encarga de realizar el procesamiento y mapeo de los descriptorres xml para Dataconnector
 *
 * @version $Revision: 1.1.1 (UTF-8)
 * @since build 19/05/2016
 * @author proveedor_hhurtado email: proveedor_hhurtad@ath.com.co
 */
public class ProccessXMLDataConnector {

    /**
     * Metodo principal que realiza la lectura del documento xml e identifica el
     * Nodo principal
     *
     * @param xmlFile
     * @param objToMap
     */
    public void readDocumentXMLDataconnector(final File xmlFile, Object objToMap) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
          
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getChildNodes();

            System.out.println("----------------------------");

            extractNodeXMlDocumment(nList, objToMap);

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ProccessXMLDataConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
     public void readDocumentXMLDataconnector(final InputStream xmlFile, Object objToMap) {
           try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
          
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getChildNodes();

            System.out.println("----------------------------");

            extractNodeXMlDocumment(nList, objToMap);

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ProccessXMLDataConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
     
     }
    /**
     * Metodo encargado de realiza analizis de los elementos internos del
     * documento xml
     *
     * @param nList
     * @param objToMap
     */
    private void extractNodeXMlDocumment(NodeList nList, Object objToMap) {

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                System.out.println("Current Element :" + nNode.getNodeName() + ": ");

                if (eElement.hasChildNodes() && eElement.getChildNodes().getLength() > 1) {
                    Object tmpValue = rempliObjectToMap(eElement, objToMap, null);
                    // Si la anotacion tiene atributos
                    if (nNode.hasAttributes()) {
                        System.out.println("Atributos..");
                        NamedNodeMap mapAtrMap = nNode.getAttributes();
                        for (int i = 0; i < mapAtrMap.getLength(); i++) {
                            System.out.println(mapAtrMap.item(i).getNodeName() + ": " + mapAtrMap.item(i).getNodeValue());
                            rempliObjectToMap(mapAtrMap.item(i), tmpValue, mapAtrMap.item(i).getNodeValue());
                        }
                    }

                    extractNodeXMlDocumment(eElement.getChildNodes(), tmpValue);

                } else if (eElement.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
                    Text texto = (Text) eElement.getChildNodes().item(0);
                    objToMap = rempliObjectToMap(eElement, objToMap, texto.getTextContent());
                    System.out.println(texto.getTextContent());
                }

            }
        }
    }

    /**
     * Realiza un marchal del elemento xml al atributo correspondiente en el boj
     *
     * @param e
     * @param objToMap
     * @param value
     * @return
     */
    private Object rempliObjectToMap(Node e, Object objToMap, String value) {
        Object objToSet = null;
        Object valueToSet = null;
        Class objReference = null;//En caso de los Collections
        boolean foundFile = false;
        // String nameMethodGet = "get" + e.getNodeName().substring(0, 1).toUpperCase() + e.getNodeName().substring(1);
        try {
            //objToSet = invokeMethod(objToMap, objToMap.getClass().getName(), new Class[]{}, nameMethodGet, null);
            //   Method method = value.getClass().getDeclaredMethod(nameMethodGet, new Class[]{});
            // method.
            //Evalua si existe el campo declarado
            Class cls = objToMap.getClass();
            Field[] listFields = cls.getDeclaredFields();
            for (Field fieldClass : listFields) {
                //Busca el valor seteado en el campo de la clase a travez del metodo get
                if (fieldClass.getName().equals(e.getNodeName())) {
                    String nameMethodGet = "get" + e.getNodeName().substring(0, 1).toUpperCase() + e.getNodeName().substring(1);
                    objToSet = DataConnectorHelper.getInstance().invokeMethod(objToMap, objToMap.getClass().getName(), new Class[]{}, nameMethodGet, null);
                    if (objToSet == null) {
                        objToSet = fieldClass.getType().newInstance();
                    }
                    //Evalua si el campo tiene anotaciones 
                    if (fieldClass.getAnnotations().length > 0) {
                        for (Annotation anotacion : fieldClass.getAnnotations()) {
                            if (anotacion instanceof CollectionInfoDataConnector) {
                                CollectionInfoDataConnector connectorAn = (CollectionInfoDataConnector) anotacion;

                                objReference = Class.forName(connectorAn.nameClassGeneric());
                                break;

                            }
                        }
                    }

                    valueToSet = (value == null) ? objToSet : value;
                    foundFile = true;
                    break;
                }

            }
            // Realiza el seteo del valor al campo buscado
            if (objToSet instanceof java.util.List && objReference != null) {

                List lista = (List) objToSet;
                //Crea instancia y lo adicion a la lista  
                Object newObject = objReference.newInstance();
                lista.add(newObject);
                objToSet = newObject;

            } else if (valueToSet != null && foundFile && objToSet != null) {
                String nameMethodSet = "set" + e.getNodeName().substring(0, 1).toUpperCase() + e.getNodeName().substring(1);

                DataConnectorHelper.getInstance().invokeMethod(objToMap, objToMap.getClass().getName(), new Class[]{objToSet.getClass()}, nameMethodSet, new Object[]{valueToSet});
            }

        } catch (Exception ex) {
            Logger.getLogger(ProccessXMLDataConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TO-DO Falta impl para otros datos primitivos
        return objToSet == null || (objToSet instanceof String) ? objToMap : objToSet;
    }
}
