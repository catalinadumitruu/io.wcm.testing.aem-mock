/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2019 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.testing.mock.aem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.VersionDef;
import com.adobe.cq.dam.cfm.Versionable;
import com.adobe.cq.dam.cfm.VersionedContent;
import com.adobe.granite.asset.api.AssetException;
import com.day.cq.dam.api.Asset;

import static org.apache.jackrabbit.JcrConstants.MIX_VERSIONABLE;

/**
 * Mock implementation of {@link Versionable}.
 */
class MockContentFragment_Versionable implements Versionable {

    /**
     * resource being represented as an Asset
     */
    private final Resource resource;
    private final ResourceResolver resourceResolver;

    MockContentFragment_Versionable(@NotNull Resource resource, ResourceResolver resourceResolver) {
        this.resource = resource;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public Iterator<VersionDef> listVersions() {
        List<VersionDef> versionDefList = new ArrayList<>();
        if (resource == null) {
            return versionDefList.iterator();
        }

        Asset asset = resource.adaptTo(Asset.class);
        if (asset == null) {
            return versionDefList.iterator();
        }

        Session session = resourceResolver.adaptTo(Session.class);
        try {
            Node node = (Node) session.getItem(resource.getPath());
            if (node.isNodeType(MIX_VERSIONABLE)) {
                VersionHistory history = session.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
                Version root = history.getRootVersion();
                VersionIterator iter = history.getAllVersions();
                while (iter.hasNext()) {
                    Version version = iter.nextVersion();
                    VersionDef v = new MockVersionDefImpl(version.getIdentifier(), version.getName());
                    if (!version.isSame(root)) {
                        versionDefList.add(v);
                    }
                }
            }
        } catch (RepositoryException e) {
            throw new AssetException("Failed to retrieve versions at Path [ " + resource.getPath() + " ]", e);
        }

        return versionDefList.iterator();
    }

    @Override
    public VersionDef createVersion(String label, String comment) throws ContentFragmentException {
        return new MockVersionDefImpl(label, comment);
    }

    // --- unsupported operations ---

    @Override
    public VersionedContent getVersionedContent(VersionDef version) throws ContentFragmentException {
        throw new UnsupportedOperationException();
    }

}
