package br.com.faciltecnologia.consigfacil3.service;

import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // O Spring Security usará o CPF (que definimos como username no Usuario.java) para validar a senha
        return repository.findByCpf(username)
                .orElseThrow(() -> new UsernameNotFoundException("Dados inválidos"));
    }
}
