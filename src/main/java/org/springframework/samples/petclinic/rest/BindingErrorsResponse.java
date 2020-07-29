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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * @author Vitaliy Fedoriv
 *
 */
public class BindingErrorsResponse
{
    private List<BindingError> bindingErrors = new ArrayList<>();

    public BindingErrorsResponse()
    {
        this(null);
    }

    public BindingErrorsResponse(final Integer id)
    {
        this(null, id);
    }

    public BindingErrorsResponse(final Integer pathId, final Integer bodyId)
    {
        boolean onlyBodyIdSpecified = pathId == null && bodyId != null;
        if (onlyBodyIdSpecified)
        {
            addBodyIdError(bodyId, "must not be specified");
        }
        boolean bothIdsSpecified = pathId != null && bodyId != null;
        if (bothIdsSpecified && !pathId.equals(bodyId))
        {
            addBodyIdError(bodyId, String.format("does not match pathId: %d", pathId));
        }
    }

    private void addBodyIdError(final Integer bodyId, final String message)
    {
        BindingError error = new BindingError();
        error.setObjectName("body");
        error.setFieldName("id");
        error.setFieldValue(bodyId.toString());
        error.setErrorMessage(message);
        addError(error);
    }

    public void addError(final BindingError bindingError)
    {
        this.bindingErrors.add(bindingError);
    }

    public void addAllErrors(final BindingResult bindingResult)
    {
        for (FieldError fieldError : bindingResult.getFieldErrors())
        {
            final var error = new BindingError();
            error.setObjectName(fieldError.getObjectName());
            error.setFieldName(fieldError.getField());
            error.setFieldValue(fieldError.getRejectedValue().toString());
            error.setErrorMessage(fieldError.getDefaultMessage());
            addError(error);
        }
    }

    public String toJSON()
    {
        final var mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        String errorsAsJSON = "";
        try
        {
            errorsAsJSON = mapper.writeValueAsString(bindingErrors);
        }
        catch (final JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return errorsAsJSON;
    }

    @Override
    public String toString()
    {
        return "BindingErrorsResponse [bindingErrors=" + bindingErrors + "]";
    }

    protected class BindingError
    {
        private String objectName;

        private String fieldName;

        private String fieldValue;

        private String errorMessage;

        public BindingError()
        {
            this.objectName = "";
            this.fieldName = "";
            this.fieldValue = "";
            this.errorMessage = "";
        }

        protected void setObjectName(final String objectName)
        {
            this.objectName = objectName;
        }

        protected void setFieldName(final String fieldName)
        {
            this.fieldName = fieldName;
        }

        protected void setFieldValue(final String fieldValue)
        {
            this.fieldValue = fieldValue;
        }

        protected void setErrorMessage(final String errorMessage)
        {
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString()
        {
            return "BindingError [objectName=" + objectName + ", fieldName=" + fieldName + ", fieldValue=" + fieldValue
                    + ", errorMessage=" + errorMessage + "]";
        }
    }
}
