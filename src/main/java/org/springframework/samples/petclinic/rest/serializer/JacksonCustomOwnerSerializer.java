/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package org.springframework.samples.petclinic.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.springframework.samples.petclinic.model.Owner;

/**
 * @author Vitaliy Fedoriv
 *
 */
public class JacksonCustomOwnerSerializer extends StdSerializer<Owner>
{
    public JacksonCustomOwnerSerializer()
    {
        this(null);
    }

    public JacksonCustomOwnerSerializer(final Class<Owner> t)
    {
        super(t);
    }

    @Override
    public void serialize(final Owner owner, final JsonGenerator jgen, final SerializerProvider provider)
        throws IOException
    {
        final var formatter = new SimpleDateFormat("yyyy/MM/dd");
        jgen.writeStartObject();
        if (owner.getId() == null)
        {
            jgen.writeNullField("id");
        }
        else
        {
            jgen.writeNumberField("id", owner.getId());
        }

        jgen.writeStringField("firstName", owner.getFirstName());
        jgen.writeStringField("lastName", owner.getLastName());
        jgen.writeStringField("address", owner.getAddress());
        jgen.writeStringField("city", owner.getCity());
        jgen.writeStringField("telephone", owner.getTelephone());
        // write pets array
        jgen.writeArrayFieldStart("pets");
        for (final var pet : owner.getPets())
        {
            jgen.writeStartObject(); // pet
            if (pet.getId() == null)
            {
                jgen.writeNullField("id");
            }
            else
            {
                jgen.writeNumberField("id", pet.getId());
            }
            jgen.writeStringField("name", pet.getName());
            jgen.writeStringField("birthDate", formatter.format(pet.getBirthDate()));

            final var petType = pet.getType();
            jgen.writeObjectFieldStart("type");
            jgen.writeNumberField("id", petType.getId());
            jgen.writeStringField("name", petType.getName());
            jgen.writeEndObject(); // type

            if (pet.getOwner().getId() == null)
            {
                jgen.writeNullField("owner");
            }
            else
            {
                jgen.writeNumberField("owner", pet.getOwner().getId());
            }
            // write visits array
            jgen.writeArrayFieldStart("visits");
            for (final var visit : pet.getVisits())
            {
                jgen.writeStartObject(); // visit
                if (visit.getId() == null)
                {
                    jgen.writeNullField("id");
                }
                else
                {
                    jgen.writeNumberField("id", visit.getId());
                }
                jgen.writeStringField("date", formatter.format(visit.getDate()));
                jgen.writeStringField("description", visit.getDescription());
                jgen.writeNumberField("pet", visit.getPet().getId());
                jgen.writeEndObject(); // visit
            }
            jgen.writeEndArray(); // visits
            jgen.writeEndObject(); // pet
        }
        jgen.writeEndArray(); // pets
        jgen.writeEndObject(); // owner
    }
}