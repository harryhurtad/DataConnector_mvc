/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dataconnector.commons.metadata;

import com.dataconnectorcommons.sql.AliasExpression;
import com.dataconnectorcommons.sql.Expression;

/**
 *{Insert class description here}
 * @version $Revision: 1.1.1  (UTF-8)
 * @param <X>
 * @since build 2/06/2016  
 * @author proveedor_hhurtado  email: proveedor_hhurtad@ath.com.co
 */
public interface MetadataFieldDataConnector<X>   {

    X getType();
    String nameField();
}
