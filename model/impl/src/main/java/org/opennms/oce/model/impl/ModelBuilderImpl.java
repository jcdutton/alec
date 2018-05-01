package org.opennms.oce.model.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.opennms.oce.model.api.Model;
import org.opennms.oce.model.api.ModelBuilder;
import org.opennms.oce.model.api.ModelObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

public class ModelBuilderImpl implements ModelBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ModelBuilderImpl.class);
    private BundleContext bcontext;

    @Override
    public Model buildModel()  {
        ModelImpl model = (ModelImpl)ModelImpl.getInstance();
        //something very simple for a while
        try {
            Bundle bundle = bcontext.getBundle();
            InputStream is = bundle.getEntry("/metamodel.xml").openStream();

            JAXBContext ctx = JAXBContext.newInstance(MetaModel.class);
            Unmarshaller um = ctx.createUnmarshaller();
            MetaModel metaModel = (MetaModel) um.unmarshal(is);

            ModelObject mo = new ModelObjectImpl();
            //TODO remove debugging garbage
            LOG.info("real type: " + metaModel.getMetaModelAttributes().get(0).getType());

            //For a while just to return something
            mo.setType(metaModel.getMetaModelAttributes().get(0).getType());
            mo.setFriendlyName("Just dummy name");

            model.setObjectById("root", mo);

        }
        catch (IOException e) {
            LOG.error("Model builder failed: ", e);
        }catch(JAXBException e ) {
            LOG.error("Model builder has issues with jaxb: ", e);
        }

        return model;

    }

    public void setBcontext(BundleContext bcontext) {
        this.bcontext = bcontext;
    }
}