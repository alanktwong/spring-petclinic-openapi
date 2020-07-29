package org.springframework.samples.petclinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService
{
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void saveUser(final User user) throws Exception
    {
        if (user.getRoles() == null || user.getRoles().isEmpty())
        {
            throw new Exception("User must have at least a role set!");
        }

        for (final var role : user.getRoles())
        {
            if (!role.getName().startsWith("ROLE_"))
            {
                role.setName("ROLE_" + role.getName());
            }

            if (role.getUser() == null)
            {
                role.setUser(user);
            }
        }

        userRepository.save(user);
    }
}
