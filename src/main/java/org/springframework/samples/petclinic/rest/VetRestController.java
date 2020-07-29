/*
 * Copyright 2016-2018 the original author or authors.
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

import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 *
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/vets")
public class VetRestController
{
    @Autowired
    private ClinicService clinicService;

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Collection<Vet>> getAllVets()
    {
        final var vets = List.copyOf(this.clinicService.findAllVets());
        if (vets.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vets, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "/{vetId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Vet> getVet(@PathVariable("vetId") final int vetId)
    {
        final var vet = this.clinicService.findVetById(vetId);
        if (vet == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vet, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Vet> addVet(@RequestBody @Valid final Vet vet,
                                      final BindingResult bindingResult,
                                      final UriComponentsBuilder ucBuilder)
    {
        final var errors = new BindingErrorsResponse();
        final var headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (vet == null))
        {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        this.clinicService.saveVet(vet);
        headers.setLocation(ucBuilder.path("/api/vets/{id}").buildAndExpand(vet.getId()).toUri());
        return new ResponseEntity<>(vet, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "/{vetId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Vet> updateVet(@PathVariable("vetId") final int vetId,
                                         @RequestBody @Valid final Vet vet,
                                         final BindingResult bindingResult)
    {
        final var errors = new BindingErrorsResponse();
        final var headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (vet == null))
        {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        final var currentVet = this.clinicService.findVetById(vetId);
        if (currentVet == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentVet.setFirstName(vet.getFirstName());
        currentVet.setLastName(vet.getLastName());
        currentVet.clearSpecialties();
        for (final var spec : vet.getSpecialties())
        {
            currentVet.addSpecialty(spec);
        }
        this.clinicService.saveVet(currentVet);
        return new ResponseEntity<>(currentVet, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @RequestMapping(value = "/{vetId}", method = RequestMethod.DELETE, produces = "application/json")
    @Transactional
    public ResponseEntity<Void> deleteVet(@PathVariable("vetId") final int vetId)
    {
        final var vet = this.clinicService.findVetById(vetId);
        if (vet == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVet(vet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
