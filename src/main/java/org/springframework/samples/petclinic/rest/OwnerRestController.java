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

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 *
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/owners")
public class OwnerRestController
{
    private ClinicService clinicService;

    @Autowired
    public OwnerRestController(final ClinicService clinicService)
    {
        this.clinicService = clinicService;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/*/lastname/{lastName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Collection<Owner>> getOwnersList(@PathVariable("lastName") final String ownerLastName)
    {
        String anOwnerLastName = ownerLastName;
        if (ownerLastName == null)
        {
            anOwnerLastName = "";
        }
        final var owners = this.clinicService.findOwnerByLastName(anOwnerLastName);
        if (owners.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(owners, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Collection<Owner>> getOwners()
    {
        final var owners = this.clinicService.findAllOwners();
        if (owners.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(owners, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{ownerId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Owner> getOwner(@PathVariable("ownerId") final int ownerId)
    {
        final var owner = this.clinicService.findOwnerById(ownerId);
        if (owner == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(owner, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Owner> addOwner(@RequestBody @Valid final Owner owner, final BindingResult bindingResult,
            final UriComponentsBuilder ucBuilder)
    {
        final var headers = new HttpHeaders();
        if (bindingResult.hasErrors() || owner.getId() != null)
        {
            final var errors = new BindingErrorsResponse(owner.getId());
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        this.clinicService.saveOwner(owner);
        headers.setLocation(ucBuilder.path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri());
        return new ResponseEntity<>(owner, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{ownerId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Owner> updateOwner(@PathVariable("ownerId") final int ownerId,
            @RequestBody @Valid final Owner owner, final BindingResult bindingResult,
            final UriComponentsBuilder ucBuilder)
    {
        boolean bodyIdMatchesPathId = owner.getId() == null || ownerId == owner.getId();
        if (bindingResult.hasErrors() || !bodyIdMatchesPathId)
        {
            final var errors = new BindingErrorsResponse(ownerId, owner.getId());
            errors.addAllErrors(bindingResult);
            HttpHeaders headers = new HttpHeaders();
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        final var currentOwner = this.clinicService.findOwnerById(ownerId);
        if (currentOwner == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentOwner.setAddress(owner.getAddress());
        currentOwner.setCity(owner.getCity());
        currentOwner.setFirstName(owner.getFirstName());
        currentOwner.setLastName(owner.getLastName());
        currentOwner.setTelephone(owner.getTelephone());
        this.clinicService.saveOwner(currentOwner);
        return new ResponseEntity<>(currentOwner, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @RequestMapping(value = "/{ownerId}", method = RequestMethod.DELETE, produces = "application/json")
    @Transactional
    public ResponseEntity<Void> deleteOwner(@PathVariable("ownerId") final int ownerId)
    {
        final var owner = this.clinicService.findOwnerById(ownerId);
        if (owner == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteOwner(owner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
