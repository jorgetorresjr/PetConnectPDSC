package com.PetConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "TB_USUARIO")
@SecondaryTable(
        name = "TB_FOTO_USUARIO",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "ID_USUARIO")
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DISC_USUARIO",
        discriminatorType = DiscriminatorType.STRING, length = 2)
public abstract class User implements UserDetails {
    // Implementação dos métodos da interface UserDetails
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        System.out.println("[DEBUG] getPassword() chamado para usuário: " + this.email + ", hash: " + this.password);
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Retorne roles se desejar
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Valid
    @Embedded
    protected Address address = new Address();

    @NotBlank
    @Column(name = "TXT_TELEFONE")
    protected String phone;

    @CPF
    @NotBlank
    @Column(name = "TXT_CPF")
    protected String cpf;

    @NotBlank
    @Column(name = "TXT_LOGIN")
    protected String login;

    @Pattern(regexp = "^[A-Z][a-z]+( [A-Z][a-z]+)*$", message = "{exemplo.jpa.Usuario.nome}")
    @NotBlank
    @Column(name = "TXT_NOME")
    protected String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "IMG_FOTO", columnDefinition = "BLOB",table = "TB_FOTO_USUARIO", nullable = true)
    private byte[] photo;

    @Email
    @NotBlank
    @Column(name = "TXT_EMAIL")
    protected String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$", message = "{exemplo.jpa.Usuario.senha}")
    @NotBlank
    @Column(name = "TXT_SENHA")
    protected String password;

    @Past
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_NASCIMENTO", nullable = true)
    protected Date birthDate;

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPhoto() {
        return photo;
    }
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

     public void getPassword(String password) {
        this.password = password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

        public String getDiscriminator() {
        return null;
    }
}