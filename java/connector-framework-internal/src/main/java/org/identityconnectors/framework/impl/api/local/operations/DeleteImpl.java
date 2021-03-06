/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/cddl1.php
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://opensource.org/licenses/cddl1.php.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 * Portions Copyrighted 2014 ForgeRock AS.
 * Portions Copyrighted 2014 Evolveum
 */
package org.identityconnectors.framework.impl.api.local.operations;

import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.DeleteOp;

public class DeleteImpl extends ConnectorAPIOperationRunner implements
        org.identityconnectors.framework.api.operations.DeleteApiOp {
	
	// Special logger with SPI operation log name. Used for logging operation entry/exit
    private static final Log OP_LOG = Log.getLog(DeleteOp.class);

    /**
     * Initializes the operation works.
     */
    public DeleteImpl(final ConnectorOperationalContext context,
            final Connector connector) {
        super(context,connector);
    }
    /**
     * Calls the delete method on the Connector side.
     *
     * @see org.identityconnectors.framework.api.operations.CreateApiOp#create(org.identityconnectors.framework.common.objects.ObjectClass, java.util.Set, org.identityconnectors.framework.common.objects.OperationOptions)
     */
    @Override
    public void delete(final ObjectClass objectClass,
            final Uid uid,
            OperationOptions options) {
        Assertions.nullCheck(objectClass, "objectClass");
        if (ObjectClass.ALL.equals(objectClass)) {
            throw new UnsupportedOperationException(
                    "Operation is not allowed on __ALL__ object class");
        }
        Assertions.nullCheck(uid, "uid");
        //cast null as empty
        if ( options == null ) {
            options = new OperationOptionsBuilder().build();
        }
        Connector connector = getConnector();
        final ObjectNormalizerFacade normalizer =
            getNormalizer(objectClass);
        Uid normalizedUid = (Uid)normalizer.normalizeAttribute(uid);
        
        if (isLoggable()) {
        	StringBuilder bld = new StringBuilder();
            bld.append("Enter: delete(");
            bld.append(objectClass).append(", ");
            bld.append(normalizedUid).append(", ");
            bld.append(options).append(")");
            final String msg = bld.toString();
            OP_LOG.log(DeleteOp.class, "delete", SpiOperationLoggingUtil.LOG_LEVEL, msg, null);
        }
        
        try {
        	((DeleteOp) connector).delete(objectClass, normalizedUid, options);
        } catch (RuntimeException e) {
        	SpiOperationLoggingUtil.logOpException(OP_LOG, DeleteOp.class, "delete", e);
        	throw e;
        }
        
        if (isLoggable()) {
        	OP_LOG.log(DeleteOp.class, "delete", SpiOperationLoggingUtil.LOG_LEVEL,
        			"Return", null);
        }
    }
    
    private static boolean isLoggable() {
		return OP_LOG.isLoggable(SpiOperationLoggingUtil.LOG_LEVEL);
	}
}
