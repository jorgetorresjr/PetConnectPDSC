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

import com.PetConnect.validators.Adulthood;

@Entity
@Table(name = "TB_USUARIO")
@SecondaryTable(
        name = "TB_FOTO_USUARIO",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "ID_USUARIO")
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
        name = "DISC_USUARIO",
        discriminatorType = DiscriminatorType.STRING,
        length = 2
)
public abstract class User implements UserDetails {

    @ManyToMany
    @JoinTable(
            name = "user_services",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    // ===================== USER DETAILS =====================

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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

    // ===================== CAMPOS =====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    protected Long id;

    @Valid
    @Embedded
    protected Address address = new Address();

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "O telefone deve ter entre 10 e 15 caracteres.")
    @Column(name = "TXT_TELEFONE")
    protected String phone;

    @CPF(message = "CPF inválido.")
    @NotBlank(message = "O CPF é obrigatório.")
    @Column(name = "TXT_CPF")
    protected String cpf;

    @NotBlank(message = "O login é obrigatório.")
    @Size(min = 5, max = 100, message = "O login deve ter entre 5 e 100 caracteres.")
    @Column(name = "TXT_LOGIN")
    protected String login;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ' -]+$",
            message = "O nome deve conter apenas letras, espaços, hífen ou apóstrofo."
    )
    @Column(name = "TXT_NOME")
    protected String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(
            name = "IMAGEM_FOTO",
            columnDefinition = "BLOB",
            table = "TB_FOTO_USUARIO",
            nullable = true
    )
    private byte[] photo;

    @Email(message = "E-mail inválido.")
    @NotBlank(message = "O e-mail é obrigatório.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    @Column(name = "TXT_EMAIL")
    protected String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 60, message = "A senha deve ter no mínimo 8 caracteres.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
            message = "A senha deve conter maiúscula, minúscula, número e caractere especial."
    )
    @Column(name = "TXT_SENHA")
    protected String password;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser anterior à data atual.")
    @Adulthood(message = "É necessário ter pelo menos 18 anos.")
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_NASCIMENTO", nullable = true)
    protected Date birthDate;

    // ===================== GETTERS/SETTERS =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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