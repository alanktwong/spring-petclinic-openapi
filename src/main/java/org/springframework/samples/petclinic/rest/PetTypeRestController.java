/*
 * Copyright 2016-2017 the original author or authors.
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

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/pettypes")
public class PetTypeRestController
{
    @Autowired
    private ClinicService clinicService;

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Collection<PetType>> getAllPetTypes()
    {
        Collection<PetType> petTypes = List.copyOf(this.clinicService.findAllPetTypes());
        if (petTypes.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petTypes, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @RequestMapping(value = "/{petTypeId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<PetType> getPetType(@PathVariable("petTypeId") final int petTypeId)
    {
        final var petType = this.clinicService.findPetTypeById(petTypeId);
        if (petType == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petType, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<PetType> addPetType(@RequestBody @Valid final PetType petType,
                                              final BindingResult bindingResult,
                                              final UriComponentsBuilder ucBuilder)
    {
        final var errors = new BindingErrorsResponse();
        final var headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (petType == null))
        {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        this.clinicService.savePetType(petType);
        headers.setLocation(ucBuilder.path("/api/pettypes/{id}").buildAndExpand(petType.getId()).toUri());
        return new ResponseEntity<>(petType, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "/{petTypeId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<PetType> updatePetType(@PathVariable("petTypeId") final int petTypeId,
                                                 @RequestBody @Valid final PetType petType,
                                                 final BindingResult bindingResult)
    {
        final var errors = new BindingErrorsResponse();
        final var headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (petType == null))
        {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        final var currentPetType = this.clinicService.findPetTypeById(petTypeId);
        if (currentPetType == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentPetType.setName(petType.getName());
        this.clinicService.savePetType(currentPetType);
        return new ResponseEntity<>(currentPetType, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "/{petTypeId}", method = RequestMethod.DELETE, produces = "application/json")
    @Transactional
    public ResponseEntity<Void> deletePetType(@PathVariable("petTypeId") final int petTypeId)
    {
        final var petType = this.clinicService.findPetTypeById(petTypeId);
        if (petType == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deletePetType(petType);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
