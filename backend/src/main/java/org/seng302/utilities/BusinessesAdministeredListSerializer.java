package org.seng302.utilities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.seng302.models.Business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Custom serializer to turn the set of Business objects found in User.businessesAdministered
 * into a list of business Id's
 */
public class BusinessesAdministeredListSerializer extends StdSerializer<Set<Business>> {

    public BusinessesAdministeredListSerializer() {
        this(null);
    }

    public BusinessesAdministeredListSerializer(Class<Set<Business>> t) {
        super(t);
    }

    @Override
    public void serialize(
        Set<Business> businesses,
        JsonGenerator generator,
        SerializerProvider provider)
        throws IOException {

        List<Long> ids = new ArrayList<>();
        for (Business business : businesses) {
            ids.add(business.getId());
        }
        generator.writeObject(ids);
    }

}
