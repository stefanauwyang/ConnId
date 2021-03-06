/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2017 Evolveum. All rights reserved.
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
 */
package org.identityconnectors.framework.common.objects;

import static org.identityconnectors.framework.common.objects.AttributeDeltaBuilder.build;
import static org.identityconnectors.framework.common.objects.LocaleTestUtil.resetLocaleCache;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AttributeDeltaTests {

    @BeforeMethod
    public void before() {
        resetLocaleCache();
    }

    /**
     * Test the case insensitivity.
     */
    @Test
    public void testName() {
        AttributeDelta actual = build("Jack");
        assertEquals(actual, build("jacK"));
        assertTrue(actual.is("JacK"));
    }

    @Test
    public void testArrays() {
        List<byte[]> values1 = new ArrayList<byte[]>();
        values1.add(new byte[] { 0, 1 });
        List<byte[]> values2 = new ArrayList<byte[]>();
        values2.add(new byte[] { 0, 1 });
        //test build attributeDelta with valuesToReplace
        AttributeDelta attribute1 = build("test", values1);
        AttributeDelta attribute2 = build("test", values2);
        assertEquals(attribute1, attribute2, "test failed with valuesToReplace");
        //test build attributeDelta with valuesToAdd and null for valueToRemove
        attribute1 = build("test", values1, null);
        attribute2 = build("test", values2, null);
        assertEquals(attribute1, attribute2, "test failed with valuesToAdd");
        //test build attributeDelta with valueToRemove and null for valueToAdd
        attribute1 = build("test", null, values1);
        attribute2 = build("test", null, values2);
        assertEquals(attribute1, attribute2, "test failed with valueToRemove");
        //test build attributeDelta with valueToAdd and valueToRemove
        attribute1 = build("test", values1, values1);
        attribute2 = build("test", values2, values2);
        assertEquals(attribute1, attribute2, "test failed with valueToAdd and valueToRemove");
    }

    @Test
    public void testNormal() {
        assertEquals(build("test", 1, 2, 4), build("test", 1, 2, 4));
        assertFalse(build("test", 1, 2, 4).equals(build("test", 2, 4)));
    }

    @Test
    public void testEqualsObservesLocale() {
        Locale defLocale = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr"));
            AttributeDelta attribute1 = build("i");
            AttributeDelta attribute2 = build("I");
            assertFalse(attribute1.equals(attribute2));
        } finally {
            Locale.setDefault(defLocale);
        }
    }

    @Test
    public void testHashCodeIndependentOnLocale() {
        Locale defLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            final AttributeDelta attribute = build("i");
            final int hash1 = attribute.hashCode();
            Locale.setDefault(new Locale("tr"));
            int hash2 = attribute.hashCode();
            assertEquals(hash1, hash2);
        } finally {
            Locale.setDefault(defLocale);
        }
    }
}
