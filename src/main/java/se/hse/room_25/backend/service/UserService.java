package se.hse.room_25.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.hse.room_25.backend.entity.Client;
import se.hse.room_25.backend.repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    ClientRepository clientRepository;

    @Autowired
    public void prepare(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        Optional<Client> client = clientRepository.findByUsername(username);
        if (client.isEmpty()) {
            throw new UsernameNotFoundException("invalid username");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        return new User(client.get().getUsername(), client.get().getPassword(), authorities);
    }
}
