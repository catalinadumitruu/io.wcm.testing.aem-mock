package io.wcm.testing.mock.aem;

import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.VersionDef;

public class MockVersionDefImpl implements VersionDef {

    private final String identifier;
    private final String description;

    public MockVersionDefImpl(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public MockVersionDefImpl(ContentVariation src) {
        this(src.getName(), src.getDescription());
    }


    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
