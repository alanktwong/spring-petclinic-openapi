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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

/**
 * @author Vitaliy Fedoriv
 *
 */
public class JacksonCustomVisitDeserializer extends StdDeserializer<Visit>
{
    public JacksonCustomVisitDeserializer()
    {
        this(null);
    }

    public JacksonCustomVisitDeserializer(final Class<Visit> t)
    {
        super(t);
    }

    @Override
    public Visit deserialize(final JsonParser parser, final DeserializationContext context) throws IOException
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        var visit = new Visit();
        var pet = new Pet();
        final var mapper = new ObjectMapper();
        Date visitDate = null;
        final JsonNode node = parser.getCodec().readTree(parser);
        final var petNode = node.get("pet");
        pet = mapper.treeToValue(petNode, Pet.class);
        final int visitId = node.get("id").asInt();
        final var visitDateStr = node.get("date").asText(null);
        final var description = node.get("description").asText(null);
        try
        {
            visitDate = formatter.parse(visitDateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new IOException(e);
        }

        if (!(visitId == 0))
        {
            visit.setId(visitId);
        }
        visit.setDate(visitDate);
        visit.setDescription(description);
        visit.setPet(pet);
        return visit;
    }
}
